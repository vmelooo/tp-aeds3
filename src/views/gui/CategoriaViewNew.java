package views.gui;

import controllers.CategoriaController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import models.Categoria;
import views.MenuView;

import java.util.List;
import java.util.Optional;

public class CategoriaViewNew extends MenuView {

    private MainView mainView;
    private String lastInput = "";
    private TableView<Categoria> table;
    private ObservableList<Categoria> categoriasList;

    public CategoriaViewNew(MainView mainView) {
        super(null);
        this.mainView = mainView;
        this.categoriasList = FXCollections.observableArrayList();
    }

    public Node createView(String action, CategoriaController controller) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f5f7fa;");

        // Título
        Label title = new Label("Gerenciar Categorias");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

        // Botão Nova Categoria
        Button novaCategoriaBtn = createStyledButton("+ Nova Categoria", "#667eea");
        novaCategoriaBtn.setOnAction(e -> showNovaCategoriaDialog(controller));

        HBox topBar = new HBox(20, title, novaCategoriaBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(title, Priority.ALWAYS);

        // Criar tabela
        createTable(controller);

        // Carregar dados
        loadCategorias(controller);

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

    private void createTable(CategoriaController controller) {
        table = new TableView<>();
        table.setItems(categoriasList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Coluna ID
        TableColumn<Categoria, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);
        idCol.setMaxWidth(100);

        // Coluna Nome
        TableColumn<Categoria, String> nomeCol = new TableColumn<>("Nome");
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        nomeCol.setPrefWidth(400);

        // Coluna Ações
        TableColumn<Categoria, Void> acoesCol = new TableColumn<>("Ações");
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
                    Categoria categoria = getTableView().getItems().get(getIndex());
                    showEditarCategoriaDialog(categoria, controller);
                });

                deleteBtn.setOnAction(event -> {
                    Categoria categoria = getTableView().getItems().get(getIndex());
                    deletarCategoria(categoria, controller);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(idCol, nomeCol, acoesCol);
    }

    private void loadCategorias(CategoriaController controller) {
        try {
            List<Categoria> categorias = controller.getArquivoCategoria().listarTodosAtivos();
            categoriasList.clear();
            categoriasList.addAll(categorias);
        } catch (Exception e) {
            showError("Erro ao carregar categorias: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showNovaCategoriaDialog(CategoriaController controller) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nova Categoria");
        dialog.setHeaderText("Criar uma nova categoria");
        dialog.setContentText("Nome da categoria:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            try {
                lastInput = result.get().trim();
                controller.criarCategoria();
                loadCategorias(controller);
                showSuccess("Categoria criada com sucesso!");
            } catch (Exception ex) {
                showError("Erro ao criar categoria: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void showEditarCategoriaDialog(Categoria categoria, CategoriaController controller) {
        TextInputDialog dialog = new TextInputDialog(categoria.getNome());
        dialog.setTitle("Editar Categoria");
        dialog.setHeaderText("Editar categoria ID: " + categoria.getId());
        dialog.setContentText("Nome da categoria:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            try {
                categoria.setNome(result.get().trim());

                // Atualizar diretamente no arquivo
                if (controller.getArquivoCategoria().update(categoria)) {
                    loadCategorias(controller);
                    showSuccess("Categoria atualizada com sucesso!");
                } else {
                    showError("Falha ao atualizar categoria.");
                }
            } catch (Exception ex) {
                showError("Erro ao atualizar categoria: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void deletarCategoria(Categoria categoria, CategoriaController controller) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Exclusão");
        confirmAlert.setHeaderText("Deletar categoria?");
        confirmAlert.setContentText("Tem certeza que deseja deletar a categoria '" + categoria.getNome() + "'?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (controller.getArquivoCategoria().delete(categoria.getId())) {
                    loadCategorias(controller);
                    showSuccess("Categoria deletada com sucesso!");
                } else {
                    showError("Falha ao deletar categoria.");
                }
            } catch (Exception ex) {
                showError("Erro ao deletar categoria: " + ex.getMessage());
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

    // Métodos da MenuView
    @Override
    public String lerTexto(String prompt) {
        String result = lastInput;
        lastInput = "";
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
        // Não fazer nada
    }

    @Override
    public void exibirErro(String erro) {
        showError(erro);
    }

    @Override
    public void exibirSucesso(String mensagem) {
        showSuccess(mensagem);
    }
}
