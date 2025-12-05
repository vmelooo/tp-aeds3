package views.gui;

import controllers.TarefaController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import views.MenuView;

public class TarefaView extends MenuView {

    private MainView mainView;
    private TextArea outputArea;
    private String lastInput = "";

    public TarefaView(MainView mainView) {
        super(null);
        this.mainView = mainView;
    }

    public Node createView(String action, TarefaController controller) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Gerenciamento de Tarefas");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(400);
        outputArea.setWrapText(true);

        layout.getChildren().add(title);

        switch (action) {
            case "criar" -> layout.getChildren().add(createCriarTarefaForm(controller));
            case "listar" -> layout.getChildren().add(createListarTarefasForm(controller));
            case "atualizar" -> layout.getChildren().add(createAtualizarTarefaForm(controller));
            case "deletar" -> layout.getChildren().add(createDeletarTarefaForm(controller));
            case "categorias" -> layout.getChildren().add(createGerenciarCategoriasForm(controller));
        }

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private Node createCriarTarefaForm(TarefaController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idUsuarioField = new TextField();
        TextField tituloField = new TextField();
        TextArea descricaoArea = new TextArea();
        descricaoArea.setPrefRowCount(3);
        TextField idStatusField = new TextField();
        Button submitBtn = new Button("Criar Tarefa");

        grid.add(new Label("ID do Usuário:"), 0, 0);
        grid.add(idUsuarioField, 1, 0);
        grid.add(new Label("Título:"), 0, 1);
        grid.add(tituloField, 1, 1);
        grid.add(new Label("Descrição:"), 0, 2);
        grid.add(descricaoArea, 1, 2);
        grid.add(new Label("ID do Status:"), 0, 3);
        grid.add(idStatusField, 1, 3);
        grid.add(submitBtn, 1, 4);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        submitBtn.setOnAction(e -> {
            try {
                lastInput = idUsuarioField.getText() + "\n" +
                           tituloField.getText() + "\n" +
                           descricaoArea.getText() + "\n" +
                           idStatusField.getText();
                controller.criarTarefa();
                idUsuarioField.clear();
                tituloField.clear();
                descricaoArea.clear();
                idStatusField.clear();
            } catch (Exception ex) {
                exibirErro("Erro ao criar tarefa: " + ex.getMessage());
            }
        });

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private Node createListarTarefasForm(TarefaController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idUsuarioField = new TextField();
        Button submitBtn = new Button("Listar");

        grid.add(new Label("ID do Usuário:"), 0, 0);
        grid.add(idUsuarioField, 1, 0);
        grid.add(submitBtn, 1, 1);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(400);

        submitBtn.setOnAction(e -> {
            try {
                lastInput = idUsuarioField.getText();
                controller.listarTarefasPorUsuario();
            } catch (Exception ex) {
                exibirErro("Erro ao listar tarefas: " + ex.getMessage());
            }
        });

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private Node createAtualizarTarefaForm(TarefaController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idField = new TextField();
        TextField tituloField = new TextField();
        TextArea descricaoArea = new TextArea();
        descricaoArea.setPrefRowCount(3);
        TextField idUsuarioField = new TextField();
        TextField idStatusField = new TextField();
        Button submitBtn = new Button("Atualizar");

        grid.add(new Label("ID da Tarefa:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Novo Título (vazio = manter):"), 0, 1);
        grid.add(tituloField, 1, 1);
        grid.add(new Label("Nova Descrição (vazio = manter):"), 0, 2);
        grid.add(descricaoArea, 1, 2);
        grid.add(new Label("Novo ID Usuário (vazio = manter):"), 0, 3);
        grid.add(idUsuarioField, 1, 3);
        grid.add(new Label("Novo ID Status (vazio = manter):"), 0, 4);
        grid.add(idStatusField, 1, 4);
        grid.add(submitBtn, 1, 5);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        submitBtn.setOnAction(e -> {
            try {
                lastInput = idField.getText() + "\n" +
                           tituloField.getText() + "\n" +
                           descricaoArea.getText() + "\n" +
                           idUsuarioField.getText() + "\n" +
                           idStatusField.getText();
                controller.atualizarTarefa();
                idField.clear();
                tituloField.clear();
                descricaoArea.clear();
                idUsuarioField.clear();
                idStatusField.clear();
            } catch (Exception ex) {
                exibirErro("Erro ao atualizar tarefa: " + ex.getMessage());
            }
        });

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private Node createDeletarTarefaForm(TarefaController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idField = new TextField();
        Button submitBtn = new Button("Deletar");
        submitBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");

        grid.add(new Label("ID da Tarefa:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(submitBtn, 1, 1);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        submitBtn.setOnAction(e -> {
            try {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmar Exclusão");
                confirmAlert.setHeaderText("Tem certeza que deseja deletar a tarefa?");
                confirmAlert.setContentText("Esta ação é irreversível!");

                if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                    lastInput = idField.getText();
                    controller.deletarTarefa();
                    idField.clear();
                }
            } catch (Exception ex) {
                exibirErro("Erro ao deletar tarefa: " + ex.getMessage());
            }
        });

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private Node createGerenciarCategoriasForm(TarefaController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idTarefaField = new TextField();
        TextField idCategoriaField = new TextField();
        TextField prioridadeField = new TextField();
        Button adicionarBtn = new Button("Adicionar Categoria");
        Button removerBtn = new Button("Remover Categoria");
        removerBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");

        grid.add(new Label("ID da Tarefa:"), 0, 0);
        grid.add(idTarefaField, 1, 0);
        grid.add(new Label("ID da Categoria:"), 0, 1);
        grid.add(idCategoriaField, 1, 1);
        grid.add(new Label("Prioridade (1-10):"), 0, 2);
        grid.add(prioridadeField, 1, 2);

        HBox btnBox = new HBox(10, adicionarBtn, removerBtn);
        btnBox.setAlignment(Pos.CENTER);
        grid.add(btnBox, 1, 3);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);

        adicionarBtn.setOnAction(e -> {
            try {
                lastInput = idTarefaField.getText() + "\n1\n" +
                           idCategoriaField.getText() + "\n" +
                           prioridadeField.getText();
                controller.gerenciarCategoriasTarefa();
            } catch (Exception ex) {
                exibirErro("Erro ao adicionar categoria: " + ex.getMessage());
            }
        });

        removerBtn.setOnAction(e -> {
            try {
                lastInput = idTarefaField.getText() + "\n2\n" + idCategoriaField.getText();
                controller.gerenciarCategoriasTarefa();
            } catch (Exception ex) {
                exibirErro("Erro ao remover categoria: " + ex.getMessage());
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
