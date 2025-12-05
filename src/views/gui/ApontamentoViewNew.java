package views.gui;

import controllers.ApontamentoController;
import dao.ArquivoTarefa;
import dao.ArquivoUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import models.ApontamentoDeHoras;
import models.Tarefa;
import utils.SessionManager;
import views.MenuView;

import java.util.List;
import java.util.Optional;

public class ApontamentoViewNew extends MenuView {
    private MainView mainView;
    private ArquivoUsuario arqUsuario;
    private ArquivoTarefa arqTarefa;
    private String lastInput = "";
    private TableView<ApontamentoDeHoras> table;
    private ObservableList<ApontamentoDeHoras> apontamentosList;

    public ApontamentoViewNew(MainView mainView, ArquivoUsuario arqUsuario, ArquivoTarefa arqTarefa) {
        super(null);
        this.mainView = mainView;
        this.arqUsuario = arqUsuario;
        this.arqTarefa = arqTarefa;
        this.apontamentosList = FXCollections.observableArrayList();
    }

    public Node createView(String action, ApontamentoController controller) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f5f7fa;");

        // Título
        Label title = new Label("Gerenciar Apontamentos de Horas");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

        // Botão Novo Apontamento
        Button novoApontamentoBtn = createStyledButton("+ Novo Apontamento", "#667eea");
        novoApontamentoBtn.setOnAction(e -> showNovoApontamentoDialog(controller));

        HBox topBar = new HBox(20, title, novoApontamentoBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(title, Priority.ALWAYS);

        // Criar tabela
        createTable(controller);

        // Carregar dados
        loadApontamentos(controller);

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

    private void createTable(ApontamentoController controller) {
        table = new TableView<>();
        table.setItems(apontamentosList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Coluna ID
        TableColumn<ApontamentoDeHoras, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);
        idCol.setMaxWidth(80);

        // Coluna Tarefa
        TableColumn<ApontamentoDeHoras, String> tarefaCol = new TableColumn<>("Tarefa");
        tarefaCol.setPrefWidth(250);
        tarefaCol.setCellValueFactory(cellData -> {
            try {
                Tarefa t = arqTarefa.read(cellData.getValue().getIdTarefa());
                return new javafx.beans.property.SimpleStringProperty(
                    t != null ? t.getTitulo() : "Tarefa ID " + cellData.getValue().getIdTarefa()
                );
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Erro");
            }
        });

        // Coluna Descrição
        TableColumn<ApontamentoDeHoras, String> descricaoCol = new TableColumn<>("Descrição");
        descricaoCol.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        descricaoCol.setPrefWidth(300);

        // Coluna Duração
        TableColumn<ApontamentoDeHoras, Double> duracaoCol = new TableColumn<>("Duração (h)");
        duracaoCol.setCellValueFactory(new PropertyValueFactory<>("duracao"));
        duracaoCol.setPrefWidth(100);

        // Coluna Ações
        TableColumn<ApontamentoDeHoras, Void> acoesCol = new TableColumn<>("Ações");
        acoesCol.setPrefWidth(120);
        acoesCol.setMaxWidth(150);
        acoesCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Deletar");

            {
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                                 "-fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 5; " +
                                 "-fx-cursor: hand;");

                deleteBtn.setOnAction(event -> {
                    ApontamentoDeHoras apontamento = getTableView().getItems().get(getIndex());
                    deletarApontamento(apontamento, controller);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        table.getColumns().addAll(idCol, tarefaCol, descricaoCol, duracaoCol, acoesCol);
    }

    private void loadApontamentos(ApontamentoController controller) {
        try {
            int idUsuario = SessionManager.getInstance().getIdUsuarioLogado();
            List<ApontamentoDeHoras> apontamentos = controller.getArquivoApontamento().listarPorUsuario(idUsuario);
            apontamentosList.clear();
            apontamentosList.addAll(apontamentos);
        } catch (Exception e) {
            showError("Erro ao carregar apontamentos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showNovoApontamentoDialog(ApontamentoController controller) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Novo Apontamento");
        dialog.setHeaderText("Registrar horas de trabalho");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // ComboBox com TODAS as tarefas (de todos os usuários)
        ComboBox<Tarefa> tarefaCombo = new ComboBox<>();
        try {
            // Listar TODAS as tarefas de TODOS os usuários
            List<Tarefa> tarefas = arqTarefa.listarTodosAtivos();
            tarefaCombo.getItems().addAll(tarefas);
            if (!tarefaCombo.getItems().isEmpty()) {
                tarefaCombo.setValue(tarefaCombo.getItems().get(0));
            }
            tarefaCombo.setConverter(new javafx.util.StringConverter<Tarefa>() {
                @Override
                public String toString(Tarefa tarefa) {
                    if (tarefa == null) return "";
                    try {
                        // Mostrar título + nome do usuário responsável
                        models.Usuario usuario = arqUsuario.read(tarefa.getIdUsuario());
                        String nomeUsuario = usuario != null ? usuario.getNome() : "Usuário " + tarefa.getIdUsuario();
                        return tarefa.getTitulo() + " (" + nomeUsuario + ")";
                    } catch (Exception e) {
                        return tarefa.getTitulo();
                    }
                }

                @Override
                public Tarefa fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            showError("Erro ao carregar tarefas: " + e.getMessage());
            e.printStackTrace();
        }

        TextArea descricaoArea = new TextArea();
        descricaoArea.setPromptText("Descrição do trabalho realizado");
        descricaoArea.setPrefRowCount(3);

        TextField duracaoField = new TextField();
        duracaoField.setPromptText("Ex: 2.5");

        grid.add(new Label("Tarefa:"), 0, 0);
        grid.add(tarefaCombo, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(descricaoArea, 1, 1);
        grid.add(new Label("Duração (horas):"), 0, 2);
        grid.add(duracaoField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int idUsuario = SessionManager.getInstance().getIdUsuarioLogado();
                Tarefa tarefaSelecionada = tarefaCombo.getValue();

                if (tarefaSelecionada == null) {
                    showError("Selecione uma tarefa!");
                    return;
                }

                if (descricaoArea.getText().isEmpty() || duracaoField.getText().isEmpty()) {
                    showError("Todos os campos são obrigatórios!");
                    return;
                }

                lastInput = idUsuario + "\n" +
                           tarefaSelecionada.getId() + "\n" +
                           descricaoArea.getText() + "\n" +
                           duracaoField.getText();

                controller.criarApontamento();
                loadApontamentos(controller);
                showSuccess("Apontamento registrado com sucesso!");

            } catch (Exception ex) {
                showError("Erro ao criar apontamento: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void deletarApontamento(ApontamentoDeHoras apontamento, ApontamentoController controller) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Exclusão");
        confirmAlert.setHeaderText("Deletar apontamento?");
        confirmAlert.setContentText("Tem certeza que deseja deletar este apontamento de " +
                                   apontamento.getDuracao() + " horas?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                lastInput = String.valueOf(apontamento.getId());
                controller.deletarApontamento();
                loadApontamentos(controller);
                showSuccess("Apontamento deletado com sucesso!");
            } catch (Exception ex) {
                showError("Erro ao deletar apontamento: " + ex.getMessage());
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
    public double lerDouble(String prompt) {
        return Double.parseDouble(lerTexto(prompt));
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
