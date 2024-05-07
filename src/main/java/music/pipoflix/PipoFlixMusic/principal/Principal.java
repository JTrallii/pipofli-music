package music.pipoflix.PipoFlixMusic.principal;

import music.pipoflix.PipoFlixMusic.model.Artista;
import music.pipoflix.PipoFlixMusic.model.Musica;
import music.pipoflix.PipoFlixMusic.model.TipoArtista;
import music.pipoflix.PipoFlixMusic.repository.ArtistaRepository;
import music.pipoflix.PipoFlixMusic.service.ConsultaChatGpt;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private final ArtistaRepository repositorio;
    private Scanner leitura = new Scanner(System.in);

    public Principal(ArtistaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;

        while (opcao!= 9) {
            var menu = """
                    *** Screen Sound Músicas ***                    
                                        
                    1- Cadastrar artistas
                    2- Cadastrar músicas
                    3- Listar músicas
                    4- Buscar músicas por artistas
                    5- Pesquisar dados sobre um artista
                                    
                    9 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    cadastrarArtistas();
                    break;
                case 2:
                    cadastrarMusicas();
                    break;
                case 3:
                    listarMusicas();
                    break;
                case 4:
                    buscarMusicasPorArtista();
                    break;
                case 5:
                    pesquisarDadosDoArtista();
                    break;
                case 9:
                    System.out.println("Encerrando a aplicação!");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }

    }

    private void pesquisarDadosDoArtista() {
        try {
            System.out.println("Pesquisar dados sobre qual artista?");
            var nome = leitura.nextLine();
            var resposta = ConsultaChatGpt.obterInformacao(nome);
            System.out.println(resposta.trim());
        } catch (NullPointerException e) {
            System.out.println("O tempo grátis da API encerrou");
        }
    }

    private void buscarMusicasPorArtista() {
        System.out.println("Buscar músicas de que Artista ?");
        var nome = leitura.nextLine();
        List<Musica> musicas = repositorio.buscaMusicasPorArtista(nome);
        musicas.forEach(System.out::println);
    }

    private void listarMusicas() {
        List<Artista> artistas = repositorio.findAll();
        artistas.forEach(a -> a.getMusicas().forEach(System.out::println));
    }

    private void cadastrarMusicas() {
        System.out.println("Cadastrar música de que artista ?");
        var nome = leitura.nextLine();
        Optional<Artista> artista = repositorio.findByNomeContainingIgnoreCase(nome);
        if (artista.isPresent()) {
            System.out.println("Informe o título da música: ");
            var nomeMusica = leitura.nextLine();
            Musica musica = new Musica(nomeMusica);

            //ainda não fizemos um vínculo na música. Não especificamos que esta
            //música pertence a este artista. Poderíamos ter feito isso no construtor.
            //Mas podemos, imediatamente após criar a música, incluir musica.setArtista(),
            //passando artista.get() para ele conseguir fazer aquele vínculo bidirecional
            //e gerar a chave estrangeira no nosso banco.
            musica.setArtista(artista.get());
            //Agora vamos fazer com que esse artista recebe essa musica e save no BD
            artista.get().getMusicas().add(musica);
            repositorio.save(artista.get());
        } else {
            System.out.println("Artista não encontrado !");
        }
    }

    private void cadastrarArtistas() {
        var cadastrarNovo = "S";

        while (cadastrarNovo.equalsIgnoreCase("s")) {
            System.out.println("Informe o nome desse artista: ");
            var nome = leitura.nextLine();
            System.out.println("Informe o tipo desse artista: (Solo, Dupla ou Banda");
            var tipo = leitura.nextLine();
            TipoArtista tipoArtista = TipoArtista.valueOf(tipo.toUpperCase());
            Artista artista = new Artista(nome, tipoArtista);
            repositorio.save(artista);
            System.out.println("Cadastrar novo artista ? (S/N)");
            cadastrarNovo = leitura.nextLine();
        }
    }
}
























