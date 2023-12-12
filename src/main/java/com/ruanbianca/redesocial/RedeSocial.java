package com.ruanbianca.redesocial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.ruanbianca.redesocial.utils.ConsoleColors.*;

public class RedeSocial {


    private IRepositorioDePerfis _perfis;
    private IRepositorioDePostagens _postagens;


    public RedeSocial(IRepositorioDePerfis perfis, IRepositorioDePostagens postagens) throws NullObjectAsArgumentException{

        Optional.ofNullable(perfis).orElseThrow(NullObjectAsArgumentException::new);
        Optional.ofNullable(postagens).orElseThrow(NullObjectAsArgumentException::new);

        this._perfis = perfis;
        this._postagens = postagens;
    }


    public ArrayList<Postagem> consultarPostagens(String texto,Perfil perfil, String hashtag){//a gente deveria poder passar várias hashtags

        return _postagens.consultarPostagens(texto,perfil,hashtag);
    }


    public Optional<Perfil> consultarPerfil(UUID id){
        
        return _perfis.consultar(id, null, null);
    }

//quer deixar pra tomorrow?
    public Optional<Perfil> consultarPerfilPorUsername(String username){
        
        return _perfis.consultar(null, username, null);
    }


    public boolean usuarioJaExite(UUID id, String username, String email){  
        
        return _perfis.usuarioJaExite(id, username, email);     
    }


    public void incluirPerfil(Perfil perfil) throws NullObjectAsArgumentException, NullAtributesException, UserAlreadyExistsException{
        
        _perfis.incluir(perfil);
    }
    

    public void incluirPostagem(Postagem postagem) throws NullAtributesException{
        
        _postagens.incluir(postagem);
    }


    public void curtir(UUID id) throws PostNotFoundException{

        Optional <Postagem> post = _postagens.consultarPostagem(id);//consultarPostagem ta com pau?
        if(post.isEmpty())
            throw new PostNotFoundException("Ocorreu na linha 70 de Rede Social!");
        post.get().curtir();//oxi - pior q eh - calma ai
        _postagens.atualizarPostagem(post.get()); //acho q eh esse o esqueleto. a gente começa por array q deve ser o mais simples
    }//testar? abre ai 

//yes honey
    public void descurtir(UUID id) throws PostNotFoundException{
        
        Optional <Postagem> post = _postagens.consultarPostagem(id);
        post.orElseThrow(PostNotFoundException::new);
        post.get().descurtir();
        _postagens.atualizarPostagem(post.get()); //acho q eh esse o esqueleto. a gente começa por array q deve ser o mais simples
    }


    public void decrementarVisualizacoes(PostagemAvancada postagem) throws NullObjectAsArgumentException{
        
        Optional.ofNullable(postagem).orElseThrow(NullObjectAsArgumentException::new);
        postagem.decrementarVisualizacoes();
    }


     public void removerPerfil(String username) throws NullAtributesException, UserNotFoundException{
        Perfil per_fil = consultarPerfilPorUsername(username).orElseThrow(UserNotFoundException::new);
        _postagens.removerPostPorPerfil(per_fil);
        _perfis.removerPerfil(username);
    }


    public static String limitarBio(String bio){

        if(bio.length() > 22){
            bio = bio.substring(0,23) + "\n  " + bio.substring(23, bio.length());
        }
        return bio;
    }


    //Todo: tirar daqui e por em RedeSocial
    public static String exibirPerfil(Perfil perfil) {
  
        StringBuilder result = new StringBuilder();
        result.append("╭───────────────────────────────────╮\n");
        result.append("│" + CYAN_BOLD + "           Perfil Info" + RESET + "             │\n");
        result.append("├───────────────────────────────────┤\n");
        result.append("  " + YELLOW + "Username:" + RESET + " " + perfil.getUsername() + "\n");
        result.append("  " + YELLOW + "Nome:" + RESET + " " + perfil.getNome() + "\n");
        result.append("  " + YELLOW + "Email:" + RESET + " " + perfil.getEmail() + "\n");
        result.append("  " + YELLOW + "Biografia:" + RESET + " " + limitarBio(perfil.getBiografia()) + "\n");
        result.append("╰───────────────────────────────────╯");

        return result.toString();
    }
    

    public String exibirPostagem(Postagem postagem) throws NullObjectAsArgumentException, NullAtributesException{
        
        Optional.ofNullable(postagem).orElseThrow(NullObjectAsArgumentException::new);

        Optional<Perfil> opPerfil = consultarPerfil(postagem.getPerfilId());

        if(opPerfil.isPresent()){

            Perfil perfil = opPerfil.get();

            if(postagem instanceof PostagemAvancada){

                PostagemAvancada postagemAv = (PostagemAvancada) postagem;//
                //primeiro tem q tirar isso neh? -> any problem?
                //postagemAv.decrementarVisualizacoes();
                //cha
                StringBuilder strHashtags = new StringBuilder();
                for(String hash:  postagemAv.getHashtags()){
                    strHashtags.append("#"+hash+" ");
                }
                return PURPLE_BOLD+"╔══════════════════════════════════════════════════════\n"+
                "║    "+postagem.getId()+""+perfil.getNome()+RESET+PURPLE_BRIGHT+" @"+ perfil.getUsername() + RESET+"\n║\n║    "+
                    postagem.getTexto()+"\n║    "+GREEN_BOLD_BRIGHT+strHashtags+RESET+"\n║\n║    "
                    +RED_BOLD_BRIGHT+postagem.getCurtidas()+" ❤️   " +RESET + YELLOW_BOLD_BRIGHT + postagem.getDescurtidas() +" 👎   "
                    +RESET+BLUE_BOLD_BRIGHT +postagemAv.getVisualizacoesRestantes()+ " 👀      "+RESET+"•" +postagem.mostrarData() + BLUE_BOLD+
                    "\n╚══════════════════════════════════════════════════════\n"+RESET;
            }

            else{
                return PURPLE_BOLD_BRIGHT+"╔══════════════════════════════════════════════════════\n"+
            "║    "+perfil.getId()+perfil.getNome()+RESET+PURPLE_BRIGHT+" @"+perfil.getUsername()+RESET+"\n║\n║    "+  
                postagem.getTexto()+"\n║\n║    "
                +RED_BOLD_BRIGHT+postagem.getCurtidas()+" ❤️   " +RESET + YELLOW_BOLD_BRIGHT + postagem.getDescurtidas() + " 👎"+RESET+"            •" +postagem.mostrarData() + YELLOW_BOLD
                + "\n╚══════════════════════════════════════════════════════\n"+RESET;

            }
        }
        throw new NullAtributesException();
    }
    

    public ArrayList<Postagem> exibirPostagensPorPerfil(String username) { 
    
        //Optional <Perfil> perfil = consultarPorUsername(username);
        Optional <Perfil> perfil = consultarPerfilPorUsername(username);
        if(perfil.isEmpty()){
            //Todo: remover esse print
            System.out.println("Username não encontrado!");
            return null;
        }else{
            System.out.println("Perfil encontrado");
        }
        
        Stream <Postagem> filtrados = _postagens.getPostagens().stream();
        filtrados = filtrados.filter(post -> {
            return post.getPerfilId().equals(perfil.get().getId());
        });
        
        filtrados = filtrados.filter(post -> {
            if(!(post instanceof PostagemAvancada))
                return true;
            else if(((PostagemAvancada)post).ehExibivel()){
                //((PostagemAvancada)post).decrementarVisualizacoes();
                return true;
            }return false;
        });
        List<Postagem> saida = (filtrados.sorted( (o1, o2)->o2.getData().compareTo(o1.getData()) ).toList());
        return new ArrayList<>(saida);
    }
 

    public ArrayList<PostagemAvancada> exibirPostagensPorHashtag(String hashtag){
        
        Stream <PostagemAvancada> filtrados = _postagens.getPostagensAvancadas().stream();
        filtrados = filtrados.filter(post -> {
            if(post.ehExibivel() && post.existeHashtag(hashtag)){
                return true;
            }return false;
        }); 
        List <PostagemAvancada> saida = new ArrayList<>();
        filtrados.sorted( (o1, o2)->o2.getData().compareTo(o1.getData()) ).forEach(p -> saida.add((PostagemAvancada)p));
        return new ArrayList<>(saida);
    }


    public ArrayList<Postagem> exibirPostagensPopulares(){
        
        Stream <Postagem> filtrados = _postagens.getPostagens().stream();
        filtrados = filtrados.filter(post ->  {
            if( !(post instanceof PostagemAvancada) || ((PostagemAvancada)post).ehExibivel()){
                return post.ehPopular();
            }return false;
        });
        List <Postagem> saida = filtrados.sorted( (o1, o2)->o2.getData().compareTo(o1.getData()) ).toList();
        return new ArrayList<>(saida);
    }


    public ArrayList<Hashtag> exibirHashtagsPopulares(){

        Map<String,Integer> mapaHashtags = new HashMap<>();
        Stream <PostagemAvancada> postagens = _postagens.getPostagensAvancadas().stream();
        postagens.forEach(post -> {
            for(String hashtag : post.getHashtags()){

                if(Optional.ofNullable(hashtag).isEmpty())
                    continue;
                    
                if(mapaHashtags.containsKey(hashtag)){
                    int numeroDeUsos = mapaHashtags.get(hashtag);
                    numeroDeUsos++;
                    mapaHashtags.put(hashtag,numeroDeUsos);
                
                }else 
                    mapaHashtags.put(hashtag,1);
            }
        });
      
        ArrayList<Hashtag> asMaisHypadas = new ArrayList<>();
        for(Map.Entry<String,Integer> par : mapaHashtags.entrySet()){
            asMaisHypadas.add(new Hashtag(par.getKey(), par.getValue()));
        }
        Stream <Hashtag> streamHashs = asMaisHypadas.stream().sorted((h1,h2) -> h2.getContadorDeUsos().compareTo(h1.getContadorDeUsos()));
        return new ArrayList<>(streamHashs.toList());
    }     


    public void atualizarPerfil(String username, String novoAtributo, String nomeAtributo) throws UserNotFoundException{

        _perfis.atualizarPerfil(username, novoAtributo, nomeAtributo);
    }
       
        
    //Extra: fazer essa função acontecer
    // public ArrayList<PostagemAvancada> exibirPostagensPorHashtags(String hashtags){
        
    //     Stream <String> streamHashs = Arrays.asList(hashtags.split("#")).stream();
    //     ArrayList<String> listaHashtags = new ArrayList<>(streamHashs.map(hash -> hash.trim()).toList());
    //     Stream <PostagemAvancada> filtrados = _postagens.getPostagensAvancadas().stream();
    //     filtrados = filtrados.filter(post -> {
    //         if(post.ehExibivel()){
    //             for(int i = 0; i< listaHashtags.size(); i++){
    //                 if(post.existeHashtag(listaHashtags.get(i))){
    //                     post.decrementarVisualizacoes();
    //                     return true;
    //                 }
    //             }
    //         }return false;
    //     }); 
    //     List <PostagemAvancada> saida = new ArrayList<>();
    //     filtrados.sorted( (o1, o2)->o2.getData().compareTo(o1.getData()) ).forEach(p -> saida.add((PostagemAvancada)p));
    //     return new ArrayList<>(saida);
    // }
}