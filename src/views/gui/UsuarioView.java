package views.gui;

import controllers.UsuarioController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import views.MenuView;

public class UsuarioView extends MenuView {

    private MainView mainView;
    private TextArea outputArea;
    private TextField inputField;
    private String lastInput = "";

    public UsuarioView(MainView mainView) {
        super(null);
        this.mainView = mainView;
    }

    public Node createView(String action, UsuarioController controller) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Gerenciamento de Usuários");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(400);
        outputArea.setWrapText(true);

        layout.getChildren().add(title);

        switch (action) {
            case "criar" -> layout.getChildren().add(createCriarUsuarioForm(controller));
            case "listar" -> {
                controller.listarUsuarios();
                layout.getChildren().add(outputArea);
            }
            case "buscar" -> layout.getChildren().add(createBuscarUsuarioForm(controller));
            case "atualizar" -> layout.getChildren().add(createAtualizarUsuarioForm(controller));
            case "deletar" -> layout.getChildren().add(createDeletarUsuarioForm(controller));
            case "telefones" -> layout.getChildren().add(createGerenciarTelefonesForm(controller));
            case "padrao" -> layout.getChildren().add(createBuscarPorPadraoForm(controller));
        }

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private Node createCriarUsuarioForm(UsuarioController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField nomeField = new TextField();
        TextField loginField = new TextField();
        PasswordField senhaField = new PasswordField();
        Button submitBtn = new Button("Criar Usuário");

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Login (Email):"), 0, 1);
        grid.add(loginField, 1, 1);
        grid.add(new Label("Senha:"), 0, 2);
        grid.add(senhaField, 1, 2);
        grid.add(submitBtn, 1, 3);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        submitBtn.setOnAction(e -> {
            try {
                String nome = nomeField.getText();
                String login = loginField.getText();
                String senha = senhaField.getText();

                if (nome.isEmpty() || login.isEmpty() || senha.isEmpty()) {
                    exibirErro("Todos os campos são obrigatórios!");
                    return;
                }

                // Simular entrada do scanner
                lastInput = nome;
                controller.criarUsuario();

                nomeField.clear();
                loginField.clear();
                senhaField.clear();
            } catch (Exception ex) {
                exibirErro("Erro ao criar usuário: " + ex.getMessage());
            }
        });

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private Node createBuscarUsuarioForm(UsuarioController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idField = new TextField();
        Button submitBtn = new Button("Buscar");

        grid.add(new Label("ID do Usuário:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(submitBtn, 1, 1);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);

        submitBtn.setOnAction(e -> {
            try {
                if (idField.getText().isEmpty()) {
                    exibirErro("ID é obrigatório!");
                    return;
                }
                lastInput = idField.getText();
                controller.buscarUsuario();
                idField.clear();
            } catch (Exception ex) {
                exibirErro("Erro ao buscar usuário: " + ex.getMessage());
            }
        });

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private Node createAtualizarUsuarioForm(UsuarioController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idField = new TextField();
        TextField nomeField = new TextField();
        Button submitBtn = new Button("Atualizar");

        grid.add(new Label("ID do Usuário:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Novo Nome (deixe vazio para manter):"), 0, 1);
        grid.add(nomeField, 1, 1);
        grid.add(submitBtn, 1, 2);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        submitBtn.setOnAction(e -> {
            try {
                if (idField.getText().isEmpty()) {
                    exibirErro("ID é obrigatório!");
                    return;
                }
                lastInput = idField.getText() + "\n" + nomeField.getText();
                controller.atualizarUsuario();
                idField.clear();
                nomeField.clear();
            } catch (Exception ex) {
                exibirErro("Erro ao atualizar usuário: " + ex.getMessage());
            }
        });

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private Node createDeletarUsuarioForm(UsuarioController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idField = new TextField();
        Button submitBtn = new Button("Deletar");
        submitBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");

        grid.add(new Label("ID do Usuário:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(submitBtn, 1, 1);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        submitBtn.setOnAction(e -> {
            try {
                if (idField.getText().isEmpty()) {
                    exibirErro("ID é obrigatório!");
                    return;
                }

                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmar Exclusão");
                confirmAlert.setHeaderText("Tem certeza que deseja deletar o usuário?");
                confirmAlert.setContentText("Esta ação é irreversível!");

                if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                    lastInput = idField.getText();
                    controller.deletarUsuario();
                    idField.clear();
                }
            } catch (Exception ex) {
                exibirErro("Erro ao deletar usuário: " + ex.getMessage());
            }
        });

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private Node createGerenciarTelefonesForm(UsuarioController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idField = new TextField();
        Button buscarBtn = new Button("Buscar Usuário");

        grid.add(new Label("ID do Usuário:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(buscarBtn, 1, 1);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private Node createBuscarPorPadraoForm(UsuarioController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField padraoField = new TextField();
        ComboBox<String> algoritmoBox = new ComboBox<>();
        algoritmoBox.getItems().addAll("KMP (Knuth-Morris-Pratt)", "Boyer-Moore");
        algoritmoBox.setValue("KMP (Knuth-Morris-Pratt)");
        Button submitBtn = new Button("Buscar");

        grid.add(new Label("Padrão a buscar:"), 0, 0);
        grid.add(padraoField, 1, 0);
        grid.add(new Label("Algoritmo:"), 0, 1);
        grid.add(algoritmoBox, 1, 1);
        grid.add(submitBtn, 1, 2);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);

        submitBtn.setOnAction(e -> {
            try {
                if (padraoField.getText().isEmpty()) {
                    exibirErro("Padrão é obrigatório!");
                    return;
                }
                String opcao = algoritmoBox.getValue().startsWith("KMP") ? "1" : "2";
                lastInput = padraoField.getText() + "\n" + opcao;
                controller.buscarPorPadrao();
                padraoField.clear();
            } catch (Exception ex) {
                exibirErro("Erro ao buscar: " + ex.getMessage());
            }
        });

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    @Override
    public String lerTexto(String prompt) {
        String[] parts = lastInput.split("\n", 2);
        String result = parts[0];
        lastInput = parts.length > 1 ? parts[1] : "";
        return result;
    }

    @Override
    public int lerInteiro(String prompt) {
        try {
            return Integer.parseInt(lerTexto(prompt));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Entrada inválida");
        }
    }

    @Override
    public void exibirMensagem(String mensagem) {
        if (outputArea != null) {
            outputArea.appendText(mensagem + "\n");
        }
    }

    @Override
    public void exibirErro(String erro) {
        if (outputArea != null) {
            outputArea.appendText("ERRO: " + erro + "\n");
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(erro);
        alert.showAndWait();
    }

    @Override
    public void exibirSucesso(String mensagem) {
        if (outputArea != null) {
            outputArea.appendText("✓ " + mensagem + "\n");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
