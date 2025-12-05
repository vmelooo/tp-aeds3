package views.gui;

import controllers.StatusController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import views.MenuView;

public class StatusView extends MenuView {

    private MainView mainView;
    private TextArea outputArea;
    private String lastInput = "";

    public StatusView(MainView mainView) {
        super(null);
        this.mainView = mainView;
    }

    public Node createView(String action, StatusController controller) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Gerenciamento de Status");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(400);
        outputArea.setWrapText(true);

        layout.getChildren().add(title);

        switch (action) {
            case "criar" -> layout.getChildren().add(createCriarStatusForm(controller));
            case "listar" -> {
                controller.listarStatus();
                layout.getChildren().add(outputArea);
            }
            case "atualizar" -> layout.getChildren().add(createAtualizarStatusForm(controller));
            case "deletar" -> layout.getChildren().add(createDeletarStatusForm(controller));
        }

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private Node createCriarStatusForm(StatusController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField nomeField = new TextField();
        TextField corField = new TextField();
        corField.setPromptText("Ex: red, blue, green");
        TextField ordemField = new TextField();
        ordemField.setPromptText("1, 2, 3...");
        Button submitBtn = new Button("Criar Status");

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Cor:"), 0, 1);
        grid.add(corField, 1, 1);
        grid.add(new Label("Ordem:"), 0, 2);
        grid.add(ordemField, 1, 2);
        grid.add(submitBtn, 1, 3);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        submitBtn.setOnAction(e -> {
            try {
                lastInput = corField.getText() + "\n" +
                           ordemField.getText() + "\n" +
                           nomeField.getText();
                controller.criarStatus();
                nomeField.clear();
                corField.clear();
                ordemField.clear();
            } catch (Exception ex) {
                exibirErro("Erro ao criar status: " + ex.getMessage());
            }
        });

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private Node createAtualizarStatusForm(StatusController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idField = new TextField();
        TextField nomeField = new TextField();
        TextField corField = new TextField();
        TextField ordemField = new TextField();
        Button submitBtn = new Button("Atualizar");

        grid.add(new Label("ID do Status:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Novo Nome (vazio = manter):"), 0, 1);
        grid.add(nomeField, 1, 1);
        grid.add(new Label("Nova Cor (vazio = manter):"), 0, 2);
        grid.add(corField, 1, 2);
        grid.add(new Label("Nova Ordem (vazio = manter):"), 0, 3);
        grid.add(ordemField, 1, 3);
        grid.add(submitBtn, 1, 4);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        submitBtn.setOnAction(e -> {
            try {
                lastInput = idField.getText() + "\n" +
                           nomeField.getText() + "\n" +
                           corField.getText() + "\n" +
                           ordemField.getText();
                controller.atualizarStatus();
                idField.clear();
                nomeField.clear();
                corField.clear();
                ordemField.clear();
            } catch (Exception ex) {
                exibirErro("Erro ao atualizar status: " + ex.getMessage());
            }
        });

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private Node createDeletarStatusForm(StatusController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idField = new TextField();
        Button submitBtn = new Button("Deletar");
        submitBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");

        grid.add(new Label("ID do Status:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(submitBtn, 1, 1);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        submitBtn.setOnAction(e -> {
            try {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmar Exclusão");
                confirmAlert.setHeaderText("Tem certeza que deseja deletar o status?");
                confirmAlert.setContentText("Esta ação é irreversível!");

                if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                    lastInput = idField.getText();
                    controller.deletarStatus();
                    idField.clear();
                }
            } catch (Exception ex) {
                exibirErro("Erro ao deletar status: " + ex.getMessage());
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
