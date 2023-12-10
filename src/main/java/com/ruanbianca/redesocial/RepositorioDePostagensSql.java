package com.ruanbianca.redesocial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class RepositorioDePostagensSql implements IRepositorioDePostagens {

    Connection conexao;

    public RepositorioDePostagensSql() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/rubi";
            String usuario = "root";
            String senha = "123456";
            conexao = DriverManager.getConnection(url, usuario, senha);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    ResultSet selectFromTabela(String nomeTabela) throws SQLException {

        try {
            ResultSet resultado = null;
            Statement statement = conexao.createStatement();

            String querySelect = String.format("SELECT * FROM %s", nomeTabela);
            resultado = statement.executeQuery(querySelect);
            return resultado;

        } catch (SQLException e) {
            throw e;
        }
    }

    public ArrayList<Postagem> getPostagens() {

        try {
            ResultSet resultado = selectFromTabela("Postagem");

            ArrayList<Postagem> postagens = new ArrayList<>();
            Postagem postagem;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while (resultado.next()) {

                if (resultado.getString("visualizacoes_restantes") == null) {

                    postagem = new Postagem(UUID.fromString(
                            resultado.getString("id")),
                            UUID.fromString(resultado.getString("perfil_id")),
                            LocalDateTime.parse(resultado.getString("data"), formatter),
                            resultado.getString("texto"),
                            resultado.getInt("curtidas"),
                            resultado.getInt("descurtidas"));

                } else {
                    postagem = new PostagemAvancada(UUID.fromString(
                            resultado.getString("id")),
                            UUID.fromString(resultado.getString("perfil_id")),
                            LocalDateTime.parse(resultado.getString("data"), formatter),
                            resultado.getString("texto"),
                            resultado.getInt("curtidas"),
                            resultado.getInt("descurtidas"),
                            resultado.getInt("visualizacoes_restantes"),
                            new ArrayList<>(Arrays.asList(resultado.getString("hashtags").split("#"))));
                }
                postagens.add(postagem);
            }
            return postagens;
        } catch (SQLException e) {

            System.err.println(
                    "SQL não está funcionando no momento, por favor tente novamente com outro tipo de persistência...");
            e.printStackTrace();
            System.err.flush();
            System.exit(1);
            return null;
        }
    }

    public ArrayList<PostagemAvancada> getPostagensAvancadas() {
        return null;
    }

    public void incluir(Postagem postagem) throws NullObjectAsArgumentException, PostAlreadyExistsException {
        Optional.ofNullable(postagem).orElseThrow(NullObjectAsArgumentException::new);

        try {
            String insert_sql = "INSERT INTO Postagem() VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement insert = conexao.prepareStatement(insert_sql);
            insert.setString(1, postagem.getId().toString());
            insert.setString(2, postagem.getPerfilId().toString());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = postagem.getData().format(formatter);
            insert.setString(3, formattedDateTime);
            insert.setString(4, postagem.getTexto());
            insert.setInt(5, postagem.getCurtidas());
            insert.setInt(6, postagem.getDescurtidas());
            if (postagem instanceof PostagemAvancada) {
                PostagemAvancada postagemAv = (PostagemAvancada) postagem;
                insert.setInt(7, postagemAv.getVisualizacoesRestantes());
                insert.setString(8, postagemAv.getHashtagsParaDb());
            } else {
                insert.setNull(7, Types.INTEGER);
                insert.setNull(8, Types.VARCHAR);
            }
            insert.executeUpdate();

        } catch (SQLException e) {
            System.err.println(
                    "SQL não está funcionando no momento, por favor tente novamente com outro tipo de persistência..."
                            + e.getMessage());
            e.printStackTrace();
            System.err.flush();
            System.exit(1);
        }
    }

    ResultSet selectFromTabelaWhere(String nomeTabela, String condicao) throws SQLException {

        try {
            ResultSet resultado = null;
            Statement statement = conexao.createStatement();

            String querySelect = String.format("SELECT * FROM %s WHERE %s", nomeTabela, condicao);
            resultado = statement.executeQuery(querySelect);
            return resultado;

        } catch (SQLException e) {
            throw e;
        }
    }

    public Optional<Postagem> consultarPostagem(UUID id) {

        try {
            ResultSet resultado = selectFromTabelaWhere("Postagem",String.format("id='%s'",id.toString()));

            Optional<Postagem> postagemOptional = Optional.empty();

            Postagem postagem;//kapoio
//to b-> a gente testa logo ou vamo pro resto? :) 
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (resultado.next()) {
                //ai é só pra saber se eh avancada amg
                if (resultado.getString("visualizacoes_restantes") == null) {

                    postagem = new Postagem(UUID.fromString(
                            resultado.getString("id")),
                            UUID.fromString(resultado.getString("perfil_id")),
                            LocalDateTime.parse(resultado.getString("data"), formatter),
                            resultado.getString("texto"),
                            resultado.getInt("curtidas"),
                            resultado.getInt("descurtidas"));

                } else {
                    postagem = new PostagemAvancada(UUID.fromString(
                            resultado.getString("id")),
                            UUID.fromString(resultado.getString("perfil_id")),
                            LocalDateTime.parse(resultado.getString("data"), formatter),
                            resultado.getString("texto"),
                            resultado.getInt("curtidas"),
                            resultado.getInt("descurtidas"),
                            resultado.getInt("visualizacoes_restantes"),
                            new ArrayList<>(Arrays.asList(resultado.getString("hashtags").split("#"))));
                }
                postagemOptional = Optional.ofNullable(postagem);
            }
            return postagemOptional;
        } catch (SQLException e) {

            System.err.println(
                    "SQL não está funcionando no momento, por favor tente novamente com outro tipo de persistência...");
            e.printStackTrace();
            System.err.flush();
            System.exit(1);
            return null;
        }
    }
    //consultar postagens faz o q mesmo?
    public ArrayList<Postagem> consultarPostagens(String texto, Perfil perfil, String hashtag) {

        Stream <Postagem> filtrados = getPostagens().stream();

        if(Optional.ofNullable(perfil).isPresent()){
            filtrados = filtrados.filter(
                post -> post.getPerfilId().equals(perfil.getId()));
        }

        if(Optional.ofNullable(hashtag).isPresent()) {
            filtrados = filtrados.filter(
                post -> post instanceof PostagemAvancada && (
                    (PostagemAvancada)post).existeHashtag(hashtag));
        }

        if(Optional.ofNullable(texto).isPresent()) {
            filtrados = filtrados.filter(post -> post.getTexto().contains(texto));
        }

        return new ArrayList<Postagem>(filtrados.toList());
    }

    public boolean postagemJaExiste(UUID id) {
        return consultarPostagem(id).isPresent();
    }//olha o zap
}
