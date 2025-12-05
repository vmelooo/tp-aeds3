package views.gui;

import controllers.CategoriaController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import views.MenuView;

public class CategoriaView extends MenuView {

    private MainView mainView;
    private TextArea outputArea;
    private String lastInput = "";

    public CategoriaView(MainView mainView) {
        super(null);
        this.mainView = mainView;
    }

    public Node createView(String action, CategoriaController controller) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Gerenciamento de Categorias");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(400);
        outputArea.setWrapText(true);

        layout.getChildren().add(title);

        switch (action) {
            case "criar" -> layout.getChildren().add(createCriarCategoriaForm(controller));
            case "listar" -> {
                controller.listarCategorias();
                layout.getChildren().add(outputArea);
            }
        }

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private Node createCriarCategoriaForm(CategoriaController controller) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField nomeField = new TextField();
        Button submitBtn = new Button("Criar Categoria");

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(submitBtn, 1, 1);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        submitBtn.setOnAction(e -> {
            try {
                if (nomeField.getText().isEmpty()) {
                    exibirErro("Nome é obrigatório!");
                    return;
                }
                lastInput = nomeField.getText();
                controller.criarCategoria();
                nomeField.clear();
            } catch (Exception ex) {
                exibirErro("Erro ao criar categoria: " + ex.getMessage());
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
