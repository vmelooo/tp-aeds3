package views;

import java.util.Scanner;

public class MenuView {

    private final Scanner scanner;

    public MenuView(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Exibe o menu principal e retorna a opção escolhida
     */
    public int exibirMenuPrincipal() {
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
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Lê uma linha de entrada do usuário com um prompt
     */
    public String lerTexto(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Lê um número inteiro do usuário com validação
     */
    public int lerInteiro(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Entrada inválida");
        }
    }

    /**
     * Lê um número double do usuário com validação
     */
    public double lerDouble(String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Entrada inválida");
        }
    }

    /**
     * Exibe uma mensagem ao usuário
     */
    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    /**
     * Exibe uma mensagem de erro
     */
    public void exibirErro(String erro) {
        System.out.println("ERRO: " + erro);
    }

    /**
     * Exibe uma mensagem de sucesso
     */
    public void exibirSucesso(String mensagem) {
        System.out.println("✓ " + mensagem);
    }
}
