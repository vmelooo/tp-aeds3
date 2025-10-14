import dados.*;
import entidades.*;
import java.util.*;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            // Inicialização dos gerenciadores de arquivos (DAOs)
            ArquivoUsuario arqU = new ArquivoUsuario("usuarios.db");
            ArquivoTarefa arqT = new ArquivoTarefa("tarefas.db");
            ArquivoStatusTarefa arqS = new ArquivoStatusTarefa("status.db");
            
            // Novos DAOs (para Categoria, Apontamento e N:N)
            ArquivoCategoria arqC = new ArquivoCategoria("categorias.db");
            ArquivoApontamentoDeHoras arqA = new ArquivoApontamentoDeHoras("apontamentos.db");
            ArquivoTarefaCategoria arqTC = new ArquivoTarefaCategoria("tarefas_categorias.db");

            // Cria status padrão caso arquivo vazio
            inicializarStatusPadrao(arqS);
            // Cria categorias padrão caso arquivo vazio
            inicializarCategoriasPadrao(arqC);


            int opcao;

            do {
                System.out.println("\n===== MENU PRINCIPAL (CRUD COMPLETO) =====");
                
                System.out.println("--- GESTÃO DE USUÁRIOS ---");
                System.out.println("1 - Criar Usuário");
                System.out.println("2 - Buscar Usuário (Read)");
                System.out.println("3 - Atualizar Usuário (Update)");
                System.out.println("4 - Deletar Usuário (Delete Lógico)");
                System.out.println("5 - Listar Usuários Ativos");
                
                System.out.println("--- GESTÃO DE TAREFAS ---");
                System.out.println("6 - Criar Tarefa");
                System.out.println("7 - Listar Tarefas por Usuário");
                System.out.println("8 - Atualizar Tarefa");
                System.out.println("9 - Deletar Tarefa (Delete Lógico)");
                
                System.out.println("--- GESTÃO DE STATUS ---");
                System.out.println("10 - Criar Status");
                System.out.println("11 - Listar Status Ativos");
                System.out.println("12 - Atualizar Status");
                System.out.println("13 - Deletar Status (Lógico)");
                
                System.out.println("--- GESTÃO DE CATEGORIAS ---");
                System.out.println("14 - Criar Categoria");
                System.out.println("15 - Listar Categorias Ativas");
                System.out.println("16 - Gerenciar Categorias de uma Tarefa (N:N)");

                System.out.println("--- GESTÃO DE APONTAMENTOS DE HORAS ---");
                System.out.println("17 - Criar Apontamento");
                System.out.println("18 - Listar Apontamentos Ativos");
                System.out.println("19 - Deletar Apontamento (Lógico)");


                System.out.println("0 - Sair");
                System.out.print("Opção: ");
                
                try {
                    opcao = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    opcao = -1; 
                }

                switch (opcao) {
                    // CRUD USUÁRIO
                    case 1 -> criarUsuario(arqU);
                    case 2 -> lerUsuario(arqU);
                    case 3 -> atualizarUsuario(arqU);
                    case 4 -> deletarUsuario(arqU);
                    case 5 -> listarUsuarios(arqU);
                        
                    // CRUD TAREFA
                    case 6 -> criarTarefa(arqT, arqU, arqS);
                    case 7 -> listarTarefasPorUsuario(arqT, arqU, arqS);
                    case 8 -> atualizarTarefa(arqT, arqU, arqS);
                    case 9 -> deletarTarefa(arqT);
                    
                    // CRUD STATUS
                    case 10 -> criarStatus(arqS); 
                    case 11 -> listarStatus(arqS); 
                    case 12 -> atualizarStatus(arqS);
                    case 13 -> deletarStatus(arqS);
                    
                    // CRUD CATEGORIA
                    case 14 -> criarCategoria(arqC);
                    case 15 -> listarCategorias(arqC);
                    case 16 -> gerenciarTarefaCategoria(arqT, arqC, arqTC);
                    
                    // CRUD APONTAMENTO
                    case 17 -> criarApontamento(arqA, arqU, arqT);
                    case 18 -> listarApontamentos(arqA);
                    case 19 -> deletarApontamento(arqA);


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
    
    // --- Métodos de Inicialização ---

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

    /* ******************************************************
     * MÉTODOS DE USUÁRIO (CRUD)
     * ****************************************************** */
    private static void criarUsuario(ArquivoUsuario arqU) throws Exception {
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Login (Email): ");
        String login = sc.nextLine();
        System.out.print("Senha: ");
        String senha = sc.nextLine();
        
        Usuario u = new Usuario(nome, login, senha); 
        int id = arqU.create(u);
        System.out.println("Usuário criado com ID " + id);
    }
    
    private static void lerUsuario(ArquivoUsuario arqU) throws Exception {
        System.out.print("ID do Usuário para busca: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Usuario u = arqU.read(id);
            if (u != null) {
                System.out.println(u);
            } else {
                System.out.println("Usuário não encontrado ou está logicamente excluído.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }
    
    private static void atualizarUsuario(ArquivoUsuario arqU) throws Exception {
        System.out.print("ID do Usuário para atualizar: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Usuario u = arqU.read(id);
            
            if (u == null) {
                System.out.println("Usuário não encontrado ou está logicamente excluído.");
                return;
            }
            
            System.out.println("--- Atualizando Usuário " + id + " ---");
            System.out.println("Nome atual: " + u.getNome());
            System.out.print("Novo nome (Enter para manter): ");
            String novoNome = sc.nextLine();
            if (!novoNome.isEmpty()) u.setNome(novoNome);

            if (arqU.update(u)) System.out.println("Usuário atualizado com sucesso.");
            else System.out.println("Falha ao atualizar. (Registro inexistente)");
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }
    
    private static void deletarUsuario(ArquivoUsuario arqU) throws Exception {
        System.out.print("ID do Usuário para exclusão LÓGICA: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            if (arqU.delete(id)) {
                System.out.println("Usuário ID " + id + " removido logicamente.");
            } else {
                System.out.println("Falha ao remover Usuário (ID não encontrado/já inativo).");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }
    
    private static void listarUsuarios(ArquivoUsuario arqU) throws Exception {
        List<Usuario> lista = arqU.listarAtivos(); 
        if (lista.isEmpty()) {
            System.out.println("Nenhum usuário ativo cadastrado.");
            return;
        }
        System.out.println("Usuários Ativos:");
        for (Usuario u : lista) {
            System.out.println(u);
        }
    }


    /* ******************************************************
     * MÉTODOS DE TAREFA (CRUD)
     * ****************************************************** */
    
    private static void criarTarefa(ArquivoTarefa arqT, ArquivoUsuario arqU, ArquivoStatusTarefa arqS) throws Exception {
        
        System.out.print("ID do Usuário responsável: ");
        int idUsuario;
        try {
            idUsuario = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de Usuário inválido. Tarefa não criada."); return;
        }
        Usuario u = arqU.read(idUsuario);
        if (u == null) {
            System.out.println("ERRO: Usuário ID " + idUsuario + " não encontrado. Tarefa não criada.");
            return; 
        }
        
        System.out.print("Título: ");
        String titulo = sc.nextLine();
        System.out.print("Descrição: ");
        String descricao = sc.nextLine();

        listarStatus(arqS);
        System.out.print("Escolha o ID do status: ");
        int idStatus;
        try {
            idStatus = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de Status inválido. Tarefa não criada."); return;
        }
        StatusTarefa s = arqS.read(idStatus);
        if (s == null) {
             System.out.println("ERRO: Status ID " + idStatus + " não encontrado. Tarefa não criada.");
            return; 
        }

        Tarefa t = new Tarefa();
        t.setTitulo(titulo);
        t.setDescricao(descricao);
        t.setIdUsuario(idUsuario);
        t.setIdStatus(idStatus); 

        int id = arqT.create(t);
        System.out.println("Tarefa criada com ID " + id);
    }

    private static void listarTarefasPorUsuario(ArquivoTarefa arqT, ArquivoUsuario arqU, ArquivoStatusTarefa arqS) throws Exception {
        System.out.print("ID do Usuário para listar tarefas: ");
        int idUsuario;
        try {
            idUsuario = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido."); return;
        }

        Usuario u = arqU.read(idUsuario);
        if (u == null) {
             System.out.println("Usuário ID " + idUsuario + " não encontrado ou está inativo.");
             return;
        }
        
        List<Tarefa> lista = arqT.listarPorUsuario(idUsuario); 
        
        if (lista.isEmpty()) {
            System.out.println("Nenhuma tarefa ativa encontrada para o Usuário " + u.getNome() + " (ID " + idUsuario + ").");
            return;
        }
        
        System.out.println("Tarefas de " + u.getNome() + ":");
        for (Tarefa t : lista) {
            StatusTarefa s = arqS.read(t.getIdStatus());
            String statusNome = (s != null && s.isAtivo()) ? s.getNomes().get(0) : "Status Inativo/Inexistente";
            System.out.println(String.format("ID: %d | Título: %s | Status: %s", t.getId(), t.getTitulo(), statusNome));
        }
    }
    
    private static void atualizarTarefa(ArquivoTarefa arqT, ArquivoUsuario arqU, ArquivoStatusTarefa arqS) throws Exception {
        System.out.print("ID da tarefa para atualizar: ");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido."); return;
        }
        
        Tarefa t = arqT.read(id);
        
        if (t == null) {
            System.out.println("Tarefa não encontrada ou está logicamente excluída.");
            return;
        }
        
        System.out.println("--- Atualizando Tarefa " + id + " ---");
        
        System.out.print("Novo título (" + t.getTitulo() + "): ");
        String novoTitulo = sc.nextLine();
        if (!novoTitulo.isEmpty()) t.setTitulo(novoTitulo);
        
        System.out.print("Nova descrição (" + t.getDescricao() + "): ");
        String novaDesc = sc.nextLine();
        if (!novaDesc.isEmpty()) t.setDescricao(novaDesc);
        
        // Atualização de ID_usuario (FK)
        System.out.print("Novo ID de Usuário (Atual: " + t.getIdUsuario() + ". Enter para manter): ");
        String novoIdUsuarioStr = sc.nextLine();
        if (!novoIdUsuarioStr.isEmpty()) {
            try {
                int novoIdUsuario = Integer.parseInt(novoIdUsuarioStr);
                Usuario novoU = arqU.read(novoIdUsuario);
                if (novoU == null) {
                    System.out.println("ERRO: Novo Usuário ID " + novoIdUsuario + " não encontrado. Usuário não alterado.");
                } else {
                    t.setIdUsuario(novoIdUsuario);
                }
            } catch (NumberFormatException e) {
                 System.out.println("ID de Usuário inválido. Usuário não alterado.");
            }
        }
        
        // Atualização de ID_status (FK)
        listarStatus(arqS);
        System.out.print("Novo ID de Status (Atual: " + t.getIdStatus() + ". Enter para manter): ");
        String novoIdStatusStr = sc.nextLine();
        if (!novoIdStatusStr.isEmpty()) {
            try {
                int novoIdStatus = Integer.parseInt(novoIdStatusStr);
                StatusTarefa novoS = arqS.read(novoIdStatus);
                if (novoS == null) {
                    System.out.println("ERRO: Novo Status ID " + novoIdStatus + " não encontrado. Status não alterado.");
                } else {
                    t.setIdStatus(novoIdStatus);
                }
            } catch (NumberFormatException e) {
                System.out.println("ID de Status inválido. Status não alterado.");
            }
        }


        if (arqT.update(t)) System.out.println("Tarefa atualizada com sucesso.");
        else System.out.println("Falha ao atualizar."); 
    }

    private static void deletarTarefa(ArquivoTarefa arqT) throws Exception {
        System.out.print("ID da tarefa para exclusão LÓGICA: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            if (arqT.delete(id)) {
                System.out.println("Tarefa ID " + id + " removida logicamente.");
            } else {
                System.out.println("Falha ao remover Tarefa (ID não encontrado/já inativo).");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }


    /* ******************************************************
     * MÉTODOS DE STATUS (CRUD - Completo)
     * ****************************************************** */
     
    private static void listarStatus(ArquivoStatusTarefa arqS) throws Exception {
        List<StatusTarefa> lista = arqS.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum status ativo cadastrado.");
            return;
        }
        System.out.println("Status ativos disponíveis:");
        for (StatusTarefa s : lista) {
            System.out.println(String.format("ID: %d -> Nomes: %s | Cor: %s | Ordem: %d", 
                                             s.getId(), s.getNomes(), s.getCor(), s.getOrdem()));
        }
    }

    private static void criarStatus(ArquivoStatusTarefa arqS) throws Exception {
        System.out.print("Cor: ");
        String cor = sc.nextLine();
        System.out.print("Ordem: ");
        int ordem;
        try {
            ordem = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ordem inválida. Status não criado."); return;
        }
        
        System.out.println("Digite nomes (um por linha). vazio para terminar:");
        List<String> nomes = new ArrayList<>();
        while (true) {
            String n = sc.nextLine();
            if (n.isEmpty()) break;
            nomes.add(n);
        }
        
        if (nomes.isEmpty()) {
            System.out.println("Status deve ter pelo menos um nome. Criação cancelada."); return;
        }
        
        StatusTarefa s = new StatusTarefa(cor, ordem, nomes);
        int id = arqS.create(s);
        System.out.println("Status criado com id " + id);
    }
    
    private static void atualizarStatus(ArquivoStatusTarefa arqS) throws Exception {
        System.out.print("ID do Status para atualizar: ");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido."); return;
        }
        
        StatusTarefa s = arqS.read(id);
        
        if (s == null) {
            System.out.println("Status não encontrado ou está logicamente excluído.");
            return;
        }
        
        System.out.println("--- Atualizando Status " + id + " ---");
        
        System.out.print("Nova Cor (" + s.getCor() + ". Enter para manter): ");
        String novaCor = sc.nextLine();
        if (!novaCor.isEmpty()) s.setCor(novaCor);

        System.out.print("Nova Ordem (" + s.getOrdem() + ". Enter para manter): ");
        String novaOrdemStr = sc.nextLine();
        if (!novaOrdemStr.isEmpty()) {
            try {
                s.setOrdem(Integer.parseInt(novaOrdemStr));
            } catch (NumberFormatException e) {
                System.out.println("Ordem inválida. Ordem não alterada.");
            }
        }

        System.out.println("Nomes Atuais: " + s.getNomes());
        System.out.print("Deseja substituir a lista de Nomes? (S/N): ");
        if (sc.nextLine().equalsIgnoreCase("S")) {
            System.out.println("Digite os novos nomes (um por linha). vazio para terminar:");
            List<String> novosNomes = new ArrayList<>();
            while (true) {
                String n = sc.nextLine();
                if (n.isEmpty()) break;
                novosNomes.add(n);
            }
            if (!novosNomes.isEmpty()) s.setNomes(novosNomes);
        }

        if (arqS.update(s)) System.out.println("Status atualizado com sucesso.");
        else System.out.println("Falha ao atualizar.");
    }
    
    private static void deletarStatus(ArquivoStatusTarefa arqS) throws Exception {
        System.out.print("ID do Status para exclusão LÓGICA: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            if (arqS.delete(id)) {
                System.out.println("Status ID " + id + " removido logicamente.");
            } else {
                System.out.println("Falha ao remover Status (ID não encontrado/já inativo).");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }
    
    /* ******************************************************
     * MÉTODOS DE CATEGORIA (CRUD)
     * ****************************************************** */

    private static void criarCategoria(ArquivoCategoria arqC) throws Exception {
        System.out.print("Nome da Categoria: ");
        String nome = sc.nextLine();
        
        Categoria c = new Categoria(nome);
        int id = arqC.create(c);
        System.out.println("Categoria criada com ID " + id);
    }
    
    private static void listarCategorias(ArquivoCategoria arqC) throws Exception {
        List<Categoria> lista = arqC.listarTodosAtivos();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma categoria ativa cadastrada.");
            return;
        }
        System.out.println("Categorias Ativas:");
        for (Categoria c : lista) {
            System.out.println(c);
        }
    }

    private static void gerenciarTarefaCategoria(ArquivoTarefa arqT, ArquivoCategoria arqC, ArquivoTarefaCategoria arqTC) throws Exception {
        System.out.print("ID da Tarefa para gerenciar categorias: ");
        int idTarefa;
        try {
            idTarefa = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido."); return;
        }
        
        if (arqT.read(idTarefa) == null) {
            System.out.println("Tarefa não encontrada ou está inativa."); return;
        }
        
        System.out.println("\n--- Gerenciando Categorias para Tarefa ID " + idTarefa + " ---");
        
        List<TarefaCategoria> relacoes = arqTC.listarPorTarefa(idTarefa);
        if (relacoes.isEmpty()) {
             System.out.println("Nenhuma categoria associada a esta tarefa.");
        } else {
            System.out.println("Categorias Atuais:");
            for(TarefaCategoria tc : relacoes) {
                Categoria c = arqC.read(tc.getIdCategoria());
                String nome = (c != null) ? c.getNome() : "INEXISTENTE";
                System.out.println(String.format("ID Categoria: %d | Nome: %s | Prioridade: %d", tc.getIdCategoria(), nome, tc.getPrioridade()));
            }
        }
        
        System.out.println("\nAções:");
        System.out.println("1 - Adicionar Categoria");
        System.out.println("2 - Remover Categoria (Lógico)");
        System.out.print("Escolha uma opção (ou Enter para sair): ");
        String opcao = sc.nextLine();
        
        if (opcao.equals("1")) {
            listarCategorias(arqC);
            System.out.print("ID da Categoria a adicionar: ");
            int idCategoria = Integer.parseInt(sc.nextLine());
            
            if (arqC.read(idCategoria) == null) {
                System.out.println("Categoria não encontrada."); return;
            }
            
            System.out.print("Prioridade (1-10): ");
            int prioridade = Integer.parseInt(sc.nextLine());
            
            if (arqTC.create(new TarefaCategoria(idTarefa, idCategoria, prioridade))) {
                System.out.println("Relacionamento adicionado com sucesso.");
            } else {
                System.out.println("Relacionamento já existe ou falha na criação.");
            }
            
        } else if (opcao.equals("2")) {
             System.out.print("ID da Categoria a remover: ");
             int idCategoria = Integer.parseInt(sc.nextLine());
             
             if (arqTC.delete(idTarefa, idCategoria)) {
                 System.out.println("Relacionamento removido logicamente.");
             } else {
                 System.out.println("Relacionamento não encontrado.");
             }
        }
    }


    /* ******************************************************
     * MÉTODOS DE APONTAMENTO DE HORAS (CRUD)
     * ****************************************************** */

    private static void criarApontamento(ArquivoApontamentoDeHoras arqA, ArquivoUsuario arqU, ArquivoTarefa arqT) throws Exception {
        
        System.out.print("ID do Usuário que está apontando as horas: ");
        int idUsuario;
        try {
            idUsuario = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de Usuário inválido. Apontamento não criado."); return;
        }
        if (arqU.read(idUsuario) == null) {
            System.out.println("ERRO: Usuário não encontrado ou inativo. Apontamento não criado."); return;
        }

        System.out.print("ID da Tarefa: ");
        int idTarefa;
        try {
            idTarefa = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de Tarefa inválido. Apontamento não criado."); return;
        }
        if (arqT.read(idTarefa) == null) {
            System.out.println("ERRO: Tarefa não encontrada ou inativa. Apontamento não criado."); return;
        }

        System.out.print("Descrição do Apontamento: ");
        String descricao = sc.nextLine();
        System.out.print("Duração em horas (ex: 1.5): ");
        double duracao;
        try {
            duracao = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Duração inválida. Apontamento não criado."); return;
        }

        ApontamentoDeHoras a = new ApontamentoDeHoras();
        a.setIdUsuario(idUsuario);
        a.setIdTarefa(idTarefa);
        a.setDescricao(descricao);
        a.setDuracao(duracao);

        int id = arqA.create(a);
        System.out.println("Apontamento criado com ID " + id);
    }
    
    private static void listarApontamentos(ArquivoApontamentoDeHoras arqA) throws Exception {
        List<ApontamentoDeHoras> lista = arqA.listarTodosAtivos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum apontamento ativo cadastrado.");
            return;
        }
        System.out.println("Apontamentos Ativos:");
        for (ApontamentoDeHoras a : lista) {
            System.out.println(a);
        }
    }
    
    private static void deletarApontamento(ArquivoApontamentoDeHoras arqA) throws Exception {
        System.out.print("ID do Apontamento para exclusão LÓGICA: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            if (arqA.delete(id)) {
                System.out.println("Apontamento ID " + id + " removido logicamente.");
            } else {
                System.out.println("Falha ao remover Apontamento (ID não encontrado/já inativo).");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }
}