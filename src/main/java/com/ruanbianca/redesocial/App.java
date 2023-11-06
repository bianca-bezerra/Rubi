package com.ruanbianca.redesocial;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import static com.ruanbianca.redesocial.utils.MenuUtils.*;
import static com.ruanbianca.redesocial.utils.ConsoleColors.*;
import com.ruanbianca.redesocial.SocialException;

public class App 
{

  
    final String INCLUIR_PERFIL = "1";
    final String INCLUIR_POSTAGEM = "2";
    final String CONSULTAR_PERFIL = "3";
    final String CONSULTAR_POSTAGEM = "4";
    
    final String EXIBIR_POST_PERFIL = "5";
    final String EXIBIR_POST_HASHTAG = "6";
    final String EXIBIR_POST_POPULARES = "7";
    final String EXIBIR_HASHTAGS_POPULARES = "8";
    final String ATUALIZAR_PERFIL = "9";
    final String REMOVER_PERFIL = "10";
    final String REMOVER_POSTAGEM = "11";
    final String SAIR = "0";
    

    Scanner input = new Scanner(System.in);

    void pausar() {
        System.out.println("\n\nPressione <Enter> para continuar...");
        input.nextLine();
        limparConsole();
    }
    
    public void executar(RedeSocial Rubi){
        
        String titulo = "RUBI";
        String opcoes = "Incluir Perfil,Incluir Postagem,Consultar Perfil,Consultar Postagem,Exibir Postagens por Perfil,Exibir Postagens por Hashtag,Exibir Postagens Populares,Exibir Hashtags Populares";
        String menu = gerarMenu(titulo,opcoes);
        String opcao = "";
        
        menuprincipal:do{

            System.out.print(menu);
            opcao = input.nextLine();

            switch (opcao) {

                case INCLUIR_PERFIL:
                    String nome = lerString("Qual o seu nome? ", input);
                    String username;
                    String email;
                    String biografia;

                    while (true) {
                        username = lerString("Digite um username: ", input);
                        if (Rubi.usuarioJaExite(null, username, null, null)) {
                            System.out.println("Username já está em uso!");
                            if (lerString("Deseja tentar outro? ",input).equals("sim")) {
                                limparConsole();
                                continue menuprincipal;
                            }
                        } else {
                            break;
                    }
                    }
                    while (true) {
                        email = lerString("Digite um endereço de email: ", input);
                        if (Rubi.usuarioJaExite(null, null, email, null)) {
                            System.out.println("Email já está em uso!");
                            if (lerString("Deseja tentar outro? ",input).equals("sim")) {
                                limparConsole();
                                continue menuprincipal;
                            }
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        biografia = lerString("Digite a sua bio: ", input);
                        if (Rubi.usuarioJaExite(null, null, null, biografia)) {
                            System.out.println("Bio já está em uso!");
                            if (lerString("Deseja tentar outro? ",input).equals("sim")) {
                                limparConsole();
                                continue menuprincipal;
                            }
                        } else {
                            break;
                        }
                    }
                    
                    
                    Rubi.incluirPerfil(new Perfil(username, nome, email,biografia));

                    break;

                case INCLUIR_POSTAGEM:
                
                    Postagem novaPostagem;
                    String usernamePost;
                    Optional <Perfil> perfilUser;
                    do {
                        usernamePost= lerString("Digite seu username: ",input);
                        perfilUser = Rubi.consultarPerfilPorUsername(usernamePost);
                        if(perfilUser.isEmpty()){
                            if(lerString("Usuário não encontrado! Tentar novamente? ",input).equals("sim")){
                                limparConsole();
                                continue menuprincipal;
                            }
                        }else 
                            break;
                    }while(true);
                    String texto = lerString("Digite o conteúdo do texto: ",input);
                    novaPostagem = new PostagemAvancada(texto,perfilUser.get(),"deus","paz");
                    Rubi.incluirPostagem(novaPostagem);
                    // if(lerString("Deseja por hashtags? ",input).equals("sim")){
                    //     String hashtags = lerString("Digite as hashtags separadas por # : ",input);
                    //     novaPostagem = new PostagemAvancada(texto,perfilUser.get(),hashtags.split("#"));
                    // }else{
                    //     try{
                    //     novaPostagem = new Postagem(texto,perfilUser.get());
                    //         Rubi.incluirPostagem(novaPostagem);
                    //     }catch(com.ruanbianca.redesocial.NullAtributesException e){
                    //         System.out.println("Você deixou algum atributo nulo!!!");
                    //     }
                    // }
                    break;
                case CONSULTAR_PERFIL:
                    
                    username = lerString("Digite o username do perfil buscado: ", input);
                    Optional<Perfil> perfilBuscado = Rubi.consultarPerfilPorUsername(username);
                    if(perfilBuscado.isPresent()){
                        System.out.println(Perfil.exibirPerfil(perfilBuscado.get()));
                        
                        
                    }else{//ta
                        System.out.println("Perfil não encontrado!");
                    }
                    break;
                    
                
                case CONSULTAR_POSTAGEM:
                
                    texto = lerString("Digite o texto da postagem buscada: ", input);
                    username = lerString("Digite o username do perfil buscado: ", input);
                    perfilBuscado = Rubi.consultarPerfilPorUsername(username);
                    String hashtag = lerString("Digite a hashtag buscada: ", input);
                    ArrayList<Postagem> postagemBuscada = Rubi.consultarPostagens(texto, perfilBuscado.get(), hashtag);
                    if(Optional.ofNullable(postagemBuscada).isPresent()){
                        for(int i = 0; i < postagemBuscada.size(); i++){
                            System.out.println(postagemBuscada.get(i).exibirPostagem(i));
                        }
                    }else{
                        System.out.println("Postagem não encontrada!");
                    }
                    break;

                case EXIBIR_POST_PERFIL:
                    // ArrayList<Postagem> postagensEncontradas;
                    // username = lerString("Digite o username do perfil buscado: ", input);
                    // postagensEncontradas = Rubi.exibirPostagensPorPerfil(username);
                    // if(Optional.ofNullable(postagensEncontradas).isPresent() && postagensEncontradas.size()>0){
                    //     for(Postagem post : postagensEncontradas)
                    //         System.out.println(post.exibirPostagem(0));
                        
                        
                    // }else{
                    //     System.out.println("Nenhuma postagem encontrada para esse perfil!");
                    // }
                    // break;
                    ArrayList<Postagem> postagensEncontradas;
                    username = lerString("Digite o username do perfil buscado: ", input);
                    postagensEncontradas = Rubi.exibirPostagensPorPerfil(username);
                    exibirFeed(postagensEncontradas);
                    break;


                case EXIBIR_POST_HASHTAG:
                    ArrayList<PostagemAvancada> postagensAvancadasEncontradas;
                    String hashtagBuscada = lerString("Digite as hashtags buscada: ", input).split("#")[0];
                    postagensAvancadasEncontradas = Rubi.exibirPostagensPorHashtags(hashtagBuscada);
                    if(Optional.ofNullable(postagensAvancadasEncontradas).isPresent()){
                        for(int i  = 0; i < postagensAvancadasEncontradas.size(); i++){
                            System.out.println(postagensAvancadasEncontradas.get(i).exibirPostagem(i));
                        }
                    }else{

                        System.out.println(RED_BOLD_BRIGHT+"Nenhuma postagem encontrada para essa hashtag!"+RESET);
                    }
                    break;
                case EXIBIR_POST_POPULARES:
                    ArrayList<Postagem> postagensPopulares;
                    postagensPopulares = Rubi.exibirPostagensPopulares();
                    if(Optional.ofNullable(postagensPopulares).isPresent()){
                        for(int i  = 0; i < postagensPopulares.size(); i++){
                            System.out.println(postagensPopulares.get(i).exibirPostagem(i)); 
                        }
                    }else{
                        System.out.println(RED_BOLD_BRIGHT+"Nenhuma postagem encontrada!"+RESET);
                    }
                    break;
                case EXIBIR_HASHTAGS_POPULARES:
                    ArrayList<Hashtag> hashtagsPopulares;
                    hashtagsPopulares = Rubi.exibirHashtagsPopulares();
                    if(Optional.of(hashtagsPopulares).isPresent()){
                        for(Hashtag hash : hashtagsPopulares){
                            System.out.println(hash.getHashtag());
                        }
                    }else{
                        System.out.println(RED_BOLD_BRIGHT+"Nenhuma hashtag encontrada!"+RESET);
                    }
                    break;
                case REMOVER_PERFIL:
                    username = lerString("Digite o username do perfil buscado: ",input);
                    Rubi.removerPerfil(username);
                    System.out.println("Perfil removido com sucesso!");
                    break;
                case SAIR:
                    break;
                default://tá 
                    System.out.println("Opção inválida!");
                    break;
            }
            Rubi.salvarPerfis(Rubi.getCaminhoDoBancoDeDados("Perfil"));
            Rubi.salvarPostagens(Rubi.getCaminhoDoBancoDeDados("Postagem"));
    
            pausar();

        }while(!opcao.equals("0"));
    }
    public static void main( String[] args )
    {
        RedeSocial Rubi = new RedeSocial();
        Rubi.resgatarPerfis(Rubi.getCaminhoDoBancoDeDados("Perfil"));
        Rubi.resgatarPostagens(Rubi.getCaminhoDoBancoDeDados("Postagem"));
        App RubiApp = new App();
        RubiApp.executar(Rubi);
    }

    public static void exibirFeed(ArrayList<Postagem> postagens){
        final String CURTIR_POSTAGEM = "1";
        final String DESCURTIR_POSTAGEM = "2";
        final String SIM = "1";
        String feedAtualizado = "";
        if(Optional.ofNullable(postagens).isPresent() && postagens.size()>0){
            for(int i  = 0; //tipo, se tiver 51 posts, ele vai separar em 6 partes
            //i = 0, 1 , 2 ,3 ,4 ,5
            i < ((postagens.size()-(postagens.size() % 10) )/ 10) + 1; i++) 
            {      
                limparConsole();
                for(int j = i * 10; j < (i *10)+10; j++){
                    if(j >= postagens.size())
                        break;
                    int indexPostVisual = j-(i*10);
                    System.out.println(postagens.get(j).exibirPostagem(indexPostVisual));
                    System.out.println("******** INTERAGIR? *******\n(Enter - Não; 1- SIM)\n>>>");
                    Scanner input = new Scanner(System.in);
                    String resposta = input.nextLine();
                    
                    if(resposta.equals(SIM)){
                        String titulo = "INTERACAO";
                        String opcoes = "Curtir, Descurtir";

                        String submenu = gerarMenu(titulo,opcoes);//tira remover por eqnaunto..
                        System.out.println(submenu);// vamo fazer só o curtir/descurtir primeiro
                        resposta = input.nextLine();// :3

                        System.out.print("Qual post voce deseja ");
                        if(resposta.equals(CURTIR_POSTAGEM)){
                            System.out.println("curtir?\n(0-9)\n>>> ");
                            resposta = input.nextLine();
                            int opcaoEscolhida = Integer.parseInt(resposta);
                            if(opcaoEscolhida >= 0 && opcaoEscolhida <= 9){
                                postagens.get(i * 10 + opcaoEscolhida).curtir();
                            }
                        }
                        else if(resposta.equals(DESCURTIR_POSTAGEM)){
                                System.out.println("descurtir?\n(0-9)\n>>> ");
                                resposta = input.nextLine();
                                int escolha = Integer.parseInt(resposta);
                                if(escolha >= 0 && escolha <= 9)
                                    postagens.get(i * 10 + escolha).descurtir();
                        }                    }
                    
                    if(postagens.get(j) instanceof PostagemAvancada){
                        ((PostagemAvancada)postagens.get(j)).incrementarVisualizacoes();;
                    }boolean vaiMostrarOutroPostDessaIteracao =  j+1 < (i *10) + 10;
                    limparConsole();
                    if(vaiMostrarOutroPostDessaIteracao){
                        feedAtualizado += (vaiMostrarOutroPostDessaIteracao ? (postagens.get(j).exibirPostagem(indexPostVisual))+"\n\n" : (postagens.get(j).exibirPostagem(indexPostVisual)));
                        System.out.println(feedAtualizado);
                    }
                }
            }
        }else{
            System.out.println(RED_BOLD_BRIGHT+"Não há postagens a serem exibidas!"+RESET);
        }
    }



    public static String dividArrEm10Partes(ArrayList<Postagem> postagems){
        ArrayList<ArrayList<Postagem>> saida = new ArrayList<>();
        int contadorTotal = 0, contadorParcial = 0, contadorDeArrays = 0;
        for(;contadorTotal < postagems.size(); contadorTotal++){//ta contando
            if(contadorParcial < 10){//será se dá pra fazwer sem isso?
                //acho q dá...
                saida.get(contadorDeArrays).add(postagems.get(contadorParcial));
            }else{
               //ignora. vem aqui pra cima 
                
            }
        }
        return null;
        //retornar uma matriz com ArrayList<Postagem>  com vetores de 10 elementos
        //oque e pra ela fazer
    }//okay ! beleza,ou terminando ad ec urtir...tava bom? muito bom...
    //tá, mas eu to fazendo essa de partir em 10, pra nao ficar 70 postagem na tela duma vez...
    //oiii, amg aquilo de postagem ter o numero dele da posicao, ta funcionando? entendi
    //acho bom...
    //ah, mas o index tá só visual por enquanto.
    //me ajuda aqui na dividir, que eu te ajudo na remover
}// na