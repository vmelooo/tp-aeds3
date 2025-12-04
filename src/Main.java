import controllers.*;
import dao.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import models.*;
import models.structures.Compressor;
import views.MenuView;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            ArquivoUsuario arqU = new ArquivoUsuario();
            ArquivoTarefa arqT = new ArquivoTarefa();
            ArquivoStatusTarefa arqS = new ArquivoStatusTarefa();
            ArquivoCategoria arqC = new ArquivoCategoria();
            ArquivoApontamentoDeHoras arqA = new ArquivoApontamentoDeHoras();
            ArquivoTarefaCategoriaHash arqTC = new ArquivoTarefaCategoriaHash();

            MenuView view = new MenuView(sc);

            UsuarioController usuarioController = new UsuarioController(arqU, view);
            TarefaController tarefaController = new TarefaController(arqT, arqU, arqS, arqC, arqTC, view);
            StatusController statusController = new StatusController(arqS, view);
            CategoriaController categoriaController = new CategoriaController(arqC, view);
            ApontamentoController apontamentoController = new ApontamentoController(arqA, arqU, arqT, view);

            inicializarStatusPadrao(arqS);
            inicializarCategoriasPadrao(arqC);

            int opcao;
            do {
                System.out.println("\n--- MENU PRINCIPAL ---");
                System.out.println("1 - Usuários");
                System.out.println("2 - Tarefas");
                System.out.println("3 - Status");
                System.out.println("4 - Categorias");
                System.out.println("5 - Apontamentos");
                System.out.println("6 - Compactar arquivos");
                System.out.println("7 - Descompactar arquivos");
                System.out.println("0 - Sair");
                System.out.print("Escolha: ");
                opcao = sc.nextInt();
                sc.nextLine();

                switch (opcao) {
                    case 1 -> menuUsuarios(usuarioController);
                    case 2 -> menuTarefas(tarefaController);
                    case 3 -> menuStatus(statusController);
                    case 4 -> menuCategorias(categoriaController, tarefaController);
                    case 5 -> menuApontamentos(apontamentoController);
                    case 6 -> menuCompactar(); // Chama o novo submenu de compactação
                    case 7 -> menuDescompactar(); // Chama o novo submenu de descompactação
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

    // --- NOVOS MÉTODOS DE MENU PARA COMPRESSÃO ---

    private static void menuCompactar() {
        System.out.println("\n--- COMPACTAR ARQUIVOS ---");
        System.out.println("1 - Usando Huffman (backup.huff)");
        System.out.println("2 - Usando LZW (backup.lzw)");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");
        int opcao = sc.nextInt();
        sc.nextLine();

        if (opcao == 1) {
            compactarHuffman();
        } else if (opcao == 2) {
            compactarLZW();
        } else if (opcao != 0) {
            System.out.println("Opção inválida!");
        }
    }

    private static void menuDescompactar() {
        System.out.println("\n--- DESCOMPACTAR ARQUIVOS ---");
        System.out.println("1 - Descompactar Huffman (backup.huff)");
        System.out.println("2 - Descompactar LZW (backup.lzw)");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");
        int opcao = sc.nextInt();
        sc.nextLine();

        if (opcao == 1) {
            descompactarHuffman();
        } else if (opcao == 2) {
            descompactarLZW();
        } else if (opcao != 0) {
            System.out.println("Opção inválida!");
        }
    }

    private static void compactarHuffman() {
        try {
            File pasta = new File("data");
            File[] arqs = pasta.listFiles((d, n) -> n.endsWith(".db") || n.endsWith(".idx"));

            if (arqs == null || arqs.length == 0) {
                System.out.println("Nenhum arquivo .db ou .idx encontrado.");
            } else {
                File saida = new File("data/backup.huff");
                Compressor.compactarArquivos(Arrays.asList(arqs), saida);
                System.out.println("Arquivos compactados com sucesso usando Huffman.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao compactar com Huffman: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void descompactarHuffman() {
        try {
            File arquivoCompactado = new File("data/backup.huff");
            if (!arquivoCompactado.exists()) {
                System.out.println("Arquivo compactado 'backup.huff' não encontrado.");
            } else {
                Compressor.descompactarArquivos(arquivoCompactado, new File("data"));
                System.out.println("Arquivos descompactados com sucesso usando Huffman.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao descompactar com Huffman: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void compactarLZW() {
        try {
            File pasta = new File("data");
            File[] arqs = pasta.listFiles((d, n) -> n.endsWith(".db") || n.endsWith(".idx"));

            if (arqs == null || arqs.length == 0) {
                System.out.println("Nenhum arquivo .db ou .idx encontrado.");
            } else {
                File saida = new File("data/backup.lzw");
                Compressor.compactarArquivosLZW(Arrays.asList(arqs), saida);
                System.out.println("Arquivos compactados com sucesso usando LZW.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao compactar com LZW: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void descompactarLZW() {
        try {
            File arquivoCompactado = new File("data/backup.lzw");
            if (!arquivoCompactado.exists()) {
                System.out.println("Arquivo compactado 'backup.lzw' não encontrado.");
            } else {
                Compressor.descompactarArquivosLZW(arquivoCompactado, new File("data"));
                System.out.println("Arquivos descompactados com sucesso usando LZW.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao descompactar com LZW: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- MÉTODOS DE MENU EXISTENTES ---

    private static void menuUsuarios(UsuarioController usuarioController) {
        int opcao;
        do {
            System.out.println("\n--- MENU USUÁRIO ---");
            System.out.println("1 - Criar");
            System.out.println("2 - Buscar");
            System.out.println("3 - Atualizar");
            System.out.println("4 - Deletar");
            System.out.println("5 - Listar");
            System.out.println("6 - Gerenciar Telefones");
            System.out.println("7 - Buscar por Padrão");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> usuarioController.criarUsuario();
                case 2 -> usuarioController.buscarUsuario();
                case 3 -> usuarioController.atualizarUsuario();
                case 4 -> usuarioController.deletarUsuario();
                case 5 -> usuarioController.listarUsuarios();
                case 6 -> usuarioController.gerenciarTelefones();
                case 7 -> usuarioController.buscarPorPadrao();
            }
        } while (opcao != 0);
    }

    private static void menuTarefas(TarefaController tarefaController) {
        int opcao;
        do {
            System.out.println("\n--- MENU TAREFA ---");
            System.out.println("1 - Criar");
            System.out.println("2 - Listar por Usuário");
            System.out.println("3 - Atualizar");
            System.out.println("4 - Deletar");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> tarefaController.criarTarefa();
                case 2 -> tarefaController.listarTarefasPorUsuario();
                case 3 -> tarefaController.atualizarTarefa();
                case 4 -> tarefaController.deletarTarefa();
            }
        } while (opcao != 0);
    }

    private static void menuStatus(StatusController statusController) {
        int opcao;
        do {
            System.out.println("\n--- MENU STATUS ---");
            System.out.println("1 - Criar");
            System.out.println("2 - Listar");
            System.out.println("3 - Atualizar");
            System.out.println("4 - Deletar");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> statusController.criarStatus();
                case 2 -> statusController.listarStatus();
                case 3 -> statusController.atualizarStatus();
                case 4 -> statusController.deletarStatus();
            }
        } while (opcao != 0);
    }

    private static void menuCategorias(CategoriaController categoriaController, TarefaController tarefaController) {
        int opcao;
        do {
            System.out.println("\n--- MENU CATEGORIA ---");
            System.out.println("1 - Criar");
            System.out.println("2 - Listar");
            System.out.println("3 - Gerenciar Tarefas");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> categoriaController.criarCategoria();
                case 2 -> categoriaController.listarCategorias();
                case 3 -> tarefaController.gerenciarCategoriasTarefa();
            }
        } while (opcao != 0);
    }

    private static void menuApontamentos(ApontamentoController apontamentoController) {
        int opcao;
        do {
            System.out.println("\n--- MENU APONTAMENTO ---");
            System.out.println("1 - Criar");
            System.out.println("2 - Listar Todos");
            System.out.println("3 - Deletar");
            System.out.println("4 - Listar por Usuário");
            System.out.println("5 - Listar por Tarefa");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> apontamentoController.criarApontamento();
                case 2 -> apontamentoController.listarApontamentos();
                case 3 -> apontamentoController.deletarApontamento();
                case 4 -> apontamentoController.listarApontamentosPorUsuario();
                case 5 -> apontamentoController.listarApontamentosPorTarefa();
                // Removido o código de compressão/descompressão repetido aqui
            }
        } while (opcao != 0);
    }

    private static void inicializarStatusPadrao(ArquivoStatusTarefa arqS) throws Exception {
        List<StatusTarefa> lista = arqS.listarTodosAtivos();
        if (lista.isEmpty()) {
            arqS.create(new StatusTarefa("yellow", 1, "Pendente"));
            arqS.create(new StatusTarefa("blue", 2, "Em andamento"));
            arqS.create(new StatusTarefa("green", 3, "Concluída"));
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