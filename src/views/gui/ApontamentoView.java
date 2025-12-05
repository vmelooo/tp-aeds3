package views.gui;

import controllers.ApontamentoController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import views.MenuView;

public class ApontamentoView extends MenuView {

    private MainView mainView;
    private TextArea outputArea;
    private String lastInput = "";

    public ApontamentoView(MainView mainView) {
        super(null);
        this.mainView = mainView;
    }

    public Node createView(String action, ApontamentoController controller) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Gerenciamento de Apontamentos");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(400);
        outputArea.setWrapText(true);

        layout.getChildren().add(title);

        switch (action) {
            case "criar" -> layout.getChildren().add(createCriarApontamentoForm(controller));
            case "listar" -> {
                controller.listarApontamentos();
                layout.getChildren().add(outputArea);
            }
            case "deletar" -> layout.getChildren().add(createDeletarApontamentoForm(controller));
            case "usuario" -> layout.getChildren().add(createListarPorUsuarioForm(controller));
            case "tarefa" -> layout.getChildren().add(createListarPorTarefaForm(controller));
        }

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private Node createCriarApontamentoForm(ApontamentoController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idUsuarioField = new TextField();
        TextField idTarefaField = new TextField();
        TextField dataField = new TextField();
        dataField.setPromptText("dd/MM/yyyy");
        TextField horasField = new TextField();
        horasField.setPromptText("Horas trabalhadas");
        Button submitBtn = new Button("Criar Apontamento");

        grid.add(new Label("ID do Usuário:"), 0, 0);
        grid.add(idUsuarioField, 1, 0);
        grid.add(new Label("ID da Tarefa:"), 0, 1);
        grid.add(idTarefaField, 1, 1);
        grid.add(new Label("Data (dd/MM/yyyy):"), 0, 2);
        grid.add(dataField, 1, 2);
        grid.add(new Label("Horas:"), 0, 3);
        grid.add(horasField, 1, 3);
        grid.add(submitBtn, 1, 4);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        submitBtn.setOnAction(e -> {
            try {
                lastInput = idUsuarioField.getText() + "\n" +
                           idTarefaField.getText() + "\n" +
                           dataField.getText() + "\n" +
                           horasField.getText();
                controller.criarApontamento();
                idUsuarioField.clear();
                idTarefaField.clear();
                dataField.clear();
                horasField.clear();
            } catch (Exception ex) {
                exibirErro("Erro ao criar apontamento: " + ex.getMessage());
            }
        });

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private Node createDeletarApontamentoForm(ApontamentoController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idField = new TextField();
        Button submitBtn = new Button("Deletar");
        submitBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");

        grid.add(new Label("ID do Apontamento:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(submitBtn, 1, 1);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        submitBtn.setOnAction(e -> {
            try {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmar Exclusão");
                confirmAlert.setHeaderText("Tem certeza que deseja deletar o apontamento?");
                confirmAlert.setContentText("Esta ação é irreversível!");

                if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                    lastInput = idField.getText();
                    controller.deletarApontamento();
                    idField.clear();
                }
            } catch (Exception ex) {
                exibirErro("Erro ao deletar apontamento: " + ex.getMessage());
            }
        });

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private Node createListarPorUsuarioForm(ApontamentoController controller) {
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
                controller.listarApontamentosPorUsuario();
            } catch (Exception ex) {
                exibirErro("Erro ao listar apontamentos: " + ex.getMessage());
            }
        });

        VBox container = new VBox(15, grid, outputArea);
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private Node createListarPorTarefaForm(ApontamentoController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField idTarefaField = new TextField();
        Button submitBtn = new Button("Listar");

        grid.add(new Label("ID da Tarefa:"), 0, 0);
        grid.add(idTarefaField, 1, 0);
        grid.add(submitBtn, 1, 1);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(400);

        submitBtn.setOnAction(e -> {
            try {
                lastInput = idTarefaField.getText();
                controller.listarApontamentosPorTarefa();
            } catch (Exception ex) {
                exibirErro("Erro ao listar apontamentos: " + ex.getMessage());
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
    public double lerDouble(String prompt) {
        try {
            return Double.parseDouble(lerTexto(prompt));
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
