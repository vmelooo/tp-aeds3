package views.gui;

import controllers.CategoriaController;
import dao.ArquivoTarefa;
import dao.ArquivoTarefaCategoriaHash;
import dao.ArquivoStatusTarefa;
import dao.ArquivoUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import models.*;
import views.MenuView;

import java.util.List;
import java.util.Optional;

public class CategoriaViewNew extends MenuView {

    private MainView mainView;
    private String lastInput = "";
    private TableView<Categoria> table;
    private ObservableList<Categoria> categoriasList;
    private ArquivoTarefaCategoriaHash arqTarefaCategoria;
    private ArquivoTarefa arqTarefa;
    private ArquivoUsuario arqUsuario;
    private ArquivoStatusTarefa arqStatus;

    public CategoriaViewNew(MainView mainView) {
        super(null);
        this.mainView = mainView;
        this.categoriasList = FXCollections.observableArrayList();
        try {
            this.arqTarefaCategoria = new ArquivoTarefaCategoriaHash();
            this.arqTarefa = new ArquivoTarefa();
            this.arqUsuario = new ArquivoUsuario();
            this.arqStatus = new ArquivoStatusTarefa();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        acoesCol.setPrefWidth(280);
        acoesCol.setMaxWidth(300);
        acoesCol.setCellFactory(param -> new TableCell<>() {
            private final Button verTarefasBtn = new Button("Ver Tarefas");
            private final Button editBtn = new Button("Editar");
            private final Button deleteBtn = new Button("Deletar");
            private final HBox pane = new HBox(8, verTarefasBtn, editBtn, deleteBtn);

            {
                verTarefasBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; " +
                               "-fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 5; " +
                               "-fx-cursor: hand;");
                editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                               "-fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 5; " +
                               "-fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                                 "-fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 5; " +
                                 "-fx-cursor: hand;");
                pane.setAlignment(Pos.CENTER);

                verTarefasBtn.setOnAction(event -> {
                    Categoria categoria = getTableView().getItems().get(getIndex());
                    showTarefasPorCategoriaDialog(categoria);
                });

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

    /**
     * Mostra um diálogo com todas as tarefas associadas a uma categoria
     */
    private void showTarefasPorCategoriaDialog(Categoria categoria) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Tarefas da Categoria");
        dialog.setHeaderText("📋 Tarefas na categoria: " + categoria.getNome());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(900);

        try {
            // Buscar relacionamentos via hash
            List<TarefaCategoria> relacoes = arqTarefaCategoria.listarPorCategoria(categoria.getId());

            if (relacoes.isEmpty()) {
                // Card vazio com estilo
                VBox emptyCard = new VBox(20);
                emptyCard.setAlignment(Pos.CENTER);
                emptyCard.setPadding(new Insets(40));
                emptyCard.setStyle(
                    "-fx-background-color: #f8f9ff; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: #e0e7ff; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 10;"
                );

                Label iconLabel = new Label("📭");
                iconLabel.setStyle("-fx-font-size: 48px;");

                Label emptyLabel = new Label("Nenhuma tarefa associada a esta categoria.");
                emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");

                emptyCard.getChildren().addAll(iconLabel, emptyLabel);
                content.getChildren().add(emptyCard);
            } else {
                // Header com estatísticas
                HBox statsBox = new HBox(20);
                statsBox.setAlignment(Pos.CENTER);
                statsBox.setPadding(new Insets(15));
                statsBox.setStyle("-fx-background-color: #f5f7fa; -fx-background-radius: 10;");

                VBox totalCard = createStatCard("Total de Tarefas", String.valueOf(relacoes.size()), "#667eea");
                statsBox.getChildren().add(totalCard);

                content.getChildren().add(statsBox);

                // Criar tabela de tarefas
                TableView<TarefaExtendida> tarefasTable = new TableView<>();
                ObservableList<TarefaExtendida> tarefasList = FXCollections.observableArrayList();

                for (TarefaCategoria tc : relacoes) {
                    Tarefa tarefa = arqTarefa.read(tc.getIdTarefa());
                    if (tarefa != null && tarefa.isAtivo()) {
                        tarefasList.add(new TarefaExtendida(tarefa, tc.getPrioridade()));
                    }
                }

                tarefasTable.setItems(tarefasList);
                tarefasTable.setPrefHeight(400);
                tarefasTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

                // Coluna ID
                TableColumn<TarefaExtendida, Integer> idCol = new TableColumn<>("ID");
                idCol.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTarefa().getId()).asObject());
                idCol.setPrefWidth(50);

                // Coluna Título
                TableColumn<TarefaExtendida, String> tituloCol = new TableColumn<>("Título");
                tituloCol.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTarefa().getTitulo()));
                tituloCol.setPrefWidth(200);

                // Coluna Descrição
                TableColumn<TarefaExtendida, String> descCol = new TableColumn<>("Descrição");
                descCol.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTarefa().getDescricao()));
                descCol.setPrefWidth(250);

                // Coluna Usuário
                TableColumn<TarefaExtendida, String> usuarioCol = new TableColumn<>("Responsável");
                usuarioCol.setPrefWidth(120);
                usuarioCol.setCellValueFactory(cellData -> {
                    try {
                        Usuario u = arqUsuario.read(cellData.getValue().getTarefa().getIdUsuario());
                        return new javafx.beans.property.SimpleStringProperty(
                            u != null ? u.getNome() : "Desconhecido");
                    } catch (Exception e) {
                        return new javafx.beans.property.SimpleStringProperty("Erro");
                    }
                });

                // Coluna Status
                TableColumn<TarefaExtendida, String> statusCol = new TableColumn<>("Status");
                statusCol.setPrefWidth(120);
                statusCol.setCellValueFactory(cellData -> {
                    try {
                        StatusTarefa s = arqStatus.read(cellData.getValue().getTarefa().getIdStatus());
                        return new javafx.beans.property.SimpleStringProperty(
                            s != null ? s.getNome() : "Sem status");
                    } catch (Exception e) {
                        return new javafx.beans.property.SimpleStringProperty("Erro");
                    }
                });

                // Estilo na célula do status
                statusCol.setCellFactory(column -> new TableCell<TarefaExtendida, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            String color = switch (item.toLowerCase()) {
                                case "pendente" -> "#FFC107";
                                case "em andamento" -> "#2196F3";
                                case "concluída" -> "#4CAF50";
                                default -> "#999";
                            };
                            setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                                    "-fx-font-weight: bold; -fx-alignment: center; -fx-background-radius: 5; " +
                                    "-fx-padding: 5;");
                        }
                    }
                });

                // Coluna Prioridade
                TableColumn<TarefaExtendida, Integer> prioridadeCol = new TableColumn<>("Prioridade");
                prioridadeCol.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getPrioridade()).asObject());
                prioridadeCol.setPrefWidth(80);

                // Estilo na célula de prioridade
                prioridadeCol.setCellFactory(column -> new TableCell<TarefaExtendida, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(String.valueOf(item));
                            String color = item >= 8 ? "#f44336" : item >= 5 ? "#FF9800" : "#4CAF50";
                            setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                                    "-fx-font-weight: bold; -fx-alignment: center; -fx-background-radius: 5;");
                        }
                    }
                });

                tarefasTable.getColumns().addAll(idCol, tituloCol, descCol, usuarioCol, statusCol, prioridadeCol);

                content.getChildren().add(tarefasTable);
                VBox.setVgrow(tarefasTable, Priority.ALWAYS);
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao carregar tarefas");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    /**
     * Cria um card de estatística
     */
    private VBox createStatCard(String titulo, String valor, String cor) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(150);
        card.setMinHeight(100);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: " + cor + "; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );

        Label tituloLabel = new Label(titulo);
        tituloLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-font-weight: bold;");
        tituloLabel.setWrapText(true);
        tituloLabel.setMaxWidth(130);
        tituloLabel.setAlignment(Pos.CENTER);

        Label valorLabel = new Label(valor);
        valorLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + cor + ";");

        card.getChildren().addAll(tituloLabel, valorLabel);
        return card;
    }

    /**
     * Classe auxiliar para representar tarefa com prioridade
     */
    private static class TarefaExtendida {
        private final Tarefa tarefa;
        private final int prioridade;

        public TarefaExtendida(Tarefa tarefa, int prioridade) {
            this.tarefa = tarefa;
            this.prioridade = prioridade;
        }

        public Tarefa getTarefa() {
            return tarefa;
        }

        public int getPrioridade() {
            return prioridade;
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
