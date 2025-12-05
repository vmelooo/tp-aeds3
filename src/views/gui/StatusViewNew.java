package views.gui;

import controllers.StatusController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import models.StatusTarefa;
import views.MenuView;

import java.util.List;
import java.util.Optional;

public class StatusViewNew extends MenuView {
    private MainView mainView;
    private String lastInput = "";
    private TableView<StatusTarefa> table;
    private ObservableList<StatusTarefa> statusList;

    public StatusViewNew(MainView mainView) {
        super(null);
        this.mainView = mainView;
        this.statusList = FXCollections.observableArrayList();
    }

    public Node createView(String action, StatusController controller) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f5f7fa;");

        // Título
        Label title = new Label("Gerenciar Status");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

        // Botão Novo Status
        Button novoStatusBtn = createStyledButton("+ Novo Status", "#667eea");
        novoStatusBtn.setOnAction(e -> showNovoStatusDialog(controller));

        HBox topBar = new HBox(20, title, novoStatusBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(title, Priority.ALWAYS);

        // Criar tabela
        createTable(controller);

        // Carregar dados
        loadStatus(controller);

        VBox tableContainer = new VBox(10, table);
        tableContainer.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");
        VBox.setVgrow(table, Priority.ALWAYS);

        layout.getChildren().addAll(topBar, new Separator(), tableContainer);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #f5f7fa; -fx-background-color: #f5f7fa;");
        return scrollPane;
    }

    private void createTable(StatusController controller) {
        table = new TableView<>();
        table.setItems(statusList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Coluna ID
        TableColumn<StatusTarefa, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);
        idCol.setMaxWidth(80);

        // Coluna Nome
        TableColumn<StatusTarefa, String> nomeCol = new TableColumn<>("Nome");
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        nomeCol.setPrefWidth(200);

        // Coluna Cor
        TableColumn<StatusTarefa, String> corCol = new TableColumn<>("Cor");
        corCol.setCellValueFactory(new PropertyValueFactory<>("cor"));
        corCol.setPrefWidth(150);

        // Coluna Ordem
        TableColumn<StatusTarefa, Integer> ordemCol = new TableColumn<>("Ordem");
        ordemCol.setCellValueFactory(new PropertyValueFactory<>("ordem"));
        ordemCol.setPrefWidth(100);

        // Coluna Ações
        TableColumn<StatusTarefa, Void> acoesCol = new TableColumn<>("Ações");
        acoesCol.setPrefWidth(180);
        acoesCol.setMaxWidth(200);
        acoesCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Editar");
            private final Button deleteBtn = new Button("Deletar");
            private final HBox pane = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                               "-fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 5; " +
                               "-fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                                 "-fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 5; " +
                                 "-fx-cursor: hand;");
                pane.setAlignment(Pos.CENTER);

                editBtn.setOnAction(event -> {
                    StatusTarefa status = getTableView().getItems().get(getIndex());
                    showEditarStatusDialog(status, controller);
                });

                deleteBtn.setOnAction(event -> {
                    StatusTarefa status = getTableView().getItems().get(getIndex());
                    deletarStatus(status, controller);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(idCol, nomeCol, corCol, ordemCol, acoesCol);
    }

    private void loadStatus(StatusController controller) {
        try {
            List<StatusTarefa> status = controller.getArquivoStatus().listarTodosAtivos();
            statusList.clear();
            statusList.addAll(status);
        } catch (Exception e) {
            showError("Erro ao carregar status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showNovoStatusDialog(StatusController controller) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Novo Status");
        dialog.setHeaderText("Criar um novo status");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome do status");

        TextField corField = new TextField();
        corField.setPromptText("Cor (ex: red, #FF0000)");

        TextField ordemField = new TextField();
        ordemField.setPromptText("Ordem (ex: 1, 2, 3)");

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Cor:"), 0, 1);
        grid.add(corField, 1, 1);
        grid.add(new Label("Ordem:"), 0, 2);
        grid.add(ordemField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                lastInput = corField.getText() + "\n" +
                           ordemField.getText() + "\n" +
                           nomeField.getText();

                controller.criarStatus();
                loadStatus(controller);
                showSuccess("Status criado com sucesso!");
            } catch (Exception ex) {
                showError("Erro ao criar status: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void showEditarStatusDialog(StatusTarefa status, StatusController controller) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Status");
        dialog.setHeaderText("Editar status ID: " + status.getId());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nomeField = new TextField(status.getNome());
        TextField corField = new TextField(status.getCor());
        TextField ordemField = new TextField(String.valueOf(status.getOrdem()));

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Cor:"), 0, 1);
        grid.add(corField, 1, 1);
        grid.add(new Label("Ordem:"), 0, 2);
        grid.add(ordemField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                status.setNome(nomeField.getText());
                status.setCor(corField.getText());
                status.setOrdem(Integer.parseInt(ordemField.getText()));

                if (controller.getArquivoStatus().update(status)) {
                    loadStatus(controller);
                    showSuccess("Status atualizado com sucesso!");
                } else {
                    showError("Falha ao atualizar status.");
                }
            } catch (Exception ex) {
                showError("Erro ao atualizar status: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void deletarStatus(StatusTarefa status, StatusController controller) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Exclusão");
        confirmAlert.setHeaderText("Deletar status?");
        confirmAlert.setContentText("Tem certeza que deseja deletar o status '" + status.getNome() + "'?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (controller.getArquivoStatus().delete(status.getId())) {
                    loadStatus(controller);
                    showSuccess("Status deletado com sucesso!");
                } else {
                    showError("Falha ao deletar status.");
                }
            } catch (Exception ex) {
                showError("Erro ao deletar status: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12 24; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setOpacity(0.8));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        return Integer.parseInt(lerTexto(prompt));
    }

    @Override
    public void exibirMensagem(String mensagem) {}

    @Override
    public void exibirErro(String erro) {
        showError(erro);
    }

    @Override
    public void exibirSucesso(String mensagem) {
        showSuccess(mensagem);
    }
}
