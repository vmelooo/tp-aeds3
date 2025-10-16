import dao.*;
import models.*;
import controllers.*;
import views.MenuView;

import java.util.*;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            // Inicialização dos DAOs (Data Access Objects)
            ArquivoUsuario arqU = new ArquivoUsuario("usuarios.db");
            ArquivoTarefa arqT = new ArquivoTarefa("tarefas.db");
            ArquivoStatusTarefa arqS = new ArquivoStatusTarefa("status.db");
            ArquivoCategoria arqC = new ArquivoCategoria("categorias.db");
            ArquivoApontamentoDeHoras arqA = new ArquivoApontamentoDeHoras("apontamentos.db");
            ArquivoTarefaCategoria arqTC = new ArquivoTarefaCategoria("tarefas_categorias.db");

            // Inicialização da VIEW
            MenuView view = new MenuView(sc);

            // Inicialização dos CONTROLLERs
            UsuarioController usuarioController = new UsuarioController(arqU, view);
            TarefaController tarefaController = new TarefaController(arqT, arqU, arqS, arqC, arqTC, view);
            StatusController statusController = new StatusController(arqS, view);
            CategoriaController categoriaController = new CategoriaController(arqC, view);
            ApontamentoController apontamentoController = new ApontamentoController(arqA, arqU, arqT, view);

            // Cria dados padrão
            inicializarStatusPadrao(arqS);
            inicializarCategoriasPadrao(arqC);

            // Loop principal da aplicação
            int opcao;
            do {
                opcao = view.exibirMenuPrincipal();

                switch (opcao) {
                    // CRUD USUÁRIO
                    case 1 -> usuarioController.criarUsuario();
                    case 2 -> usuarioController.buscarUsuario();
                    case 3 -> usuarioController.atualizarUsuario();
                    case 4 -> usuarioController.deletarUsuario();
                    case 5 -> usuarioController.listarUsuarios();

                    // CRUD TAREFA
                    case 6 -> tarefaController.criarTarefa();
                    case 7 -> tarefaController.listarTarefasPorUsuario();
                    case 8 -> tarefaController.atualizarTarefa();
                    case 9 -> tarefaController.deletarTarefa();

                    // CRUD STATUS
                    case 10 -> statusController.criarStatus();
                    case 11 -> statusController.listarStatus();
                    case 12 -> statusController.atualizarStatus();
                    case 13 -> statusController.deletarStatus();

                    // CRUD CATEGORIA
                    case 14 -> categoriaController.criarCategoria();
                    case 15 -> categoriaController.listarCategorias();
                    case 16 -> tarefaController.gerenciarCategoriasTarefa();

                    // CRUD APONTAMENTO
                    case 17 -> apontamentoController.criarApontamento();
                    case 18 -> apontamentoController.listarApontamentos();
                    case 19 -> apontamentoController.deletarApontamento();

                    case 0 -> System.out.println("Tchau!");
                    default -> System.out.println("Opção inválida!");
                }
            } while (opcao != 0);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }

    private static void inicializarStatusPadrao(ArquivoStatusTarefa arqS) throws Exception {
        List<StatusTarefa> lista = arqS.listarTodos();
        if (lista.isEmpty()) {
            List<String> nomes1 = Arrays.asList("Pendente");
            List<String> nomes2 = Arrays.asList("Em andamento");
            List<String> nomes3 = Arrays.asList("Concluída");
            arqS.create(new StatusTarefa("yellow", 1, nomes1));
            arqS.create(new StatusTarefa("blue", 2, nomes2));
            arqS.create(new StatusTarefa("green", 3, nomes3));
            System.out.println("Statuses padrões criados.");
        }
    }

    private static void inicializarCategoriasPadrao(ArquivoCategoria arqC) throws Exception {
        List<Categoria> lista = arqC.listarTodosAtivos();
        if (lista.isEmpty()) {
            arqC.create(new Categoria("Desenvolvimento"));
            arqC.create(new Categoria("Reunião"));
            arqC.create(new Categoria("Documentação"));
            System.out.println("Categorias padrões criadas.");
        }
    }
}
