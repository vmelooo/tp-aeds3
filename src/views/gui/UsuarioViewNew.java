package views.gui;

import controllers.UsuarioController;
import dao.ArquivoApontamentoDeHoras;
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
import models.Usuario;
import utils.SessionManager;
import views.MenuView;

import java.util.List;

public class UsuarioViewNew extends MenuView {

    private MainView mainView;
    private String lastInput = "";
    private ArquivoApontamentoDeHoras arqApontamento;
    private ArquivoUsuario arqUsuario;

    public UsuarioViewNew(MainView mainView) {
        super(null);
        this.mainView = mainView;
        try {
            this.arqApontamento = new ArquivoApontamentoDeHoras();
            this.arqUsuario = new ArquivoUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Node createView(String action, UsuarioController controller) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f5f7fa;");

        Label title = new Label(getTitleForAction(action));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

        Node content = switch (action) {
            case "buscar" -> createBuscarForm(controller);
            case "listar" -> createListarView(controller);
            case "padrao" -> createBuscarPadraoForm(controller);
            case "telefones" -> createGerenciarTelefonesForm(controller);
            default -> new Label("Ação não reconhecida");
        };

        layout.getChildren().addAll(title, new Separator(), content);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f7fa;");
        return scrollPane;
    }

    private String getTitleForAction(String action) {
        return switch (action) {
            case "buscar" -> "Buscar Usuário";
            case "listar" -> "Lista de Usuários";
            case "padrao" -> "Buscar por Padrão";
            case "telefones" -> "Gerenciar Meus Telefones";
            default -> "Usuários";
        };
    }

    private Node createBuscarForm(UsuarioController controller) {
        VBox container = new VBox(15);
        container.setMaxWidth(600);
        container.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10;");

        TextField idField = new TextField();
        idField.setPromptText("ID do usuário");
        styleField(idField);

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(10);

        Button buscarBtn = createButton("Buscar", "#667eea");
        buscarBtn.setMaxWidth(Double.MAX_VALUE);

        buscarBtn.setOnAction(e -> {
            lastInput = idField.getText();
            controller.buscarUsuario();
        });

        container.getChildren().addAll(
            new Label("ID do Usuário:"),
            idField,
            buscarBtn,
            resultArea
        );

        return container;
    }

    private Node createListarView(UsuarioController controller) {
        VBox container = new VBox(15);
        container.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");

        TableView<Usuario> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Carregar usuários na tabela
        ObservableList<Usuario> usuariosList = FXCollections.observableArrayList();
        try {
            List<Usuario> usuarios = arqUsuario.listarAtivos();
            usuariosList.addAll(usuarios);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao carregar usuários");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
        table.setItems(usuariosList);

        TableColumn<Usuario, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        idCol.setMaxWidth(80);

        TableColumn<Usuario, String> nomeCol = new TableColumn<>("Nome");
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        nomeCol.setPrefWidth(200);

        TableColumn<Usuario, String> loginCol = new TableColumn<>("Login");
        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));
        loginCol.setPrefWidth(250);

        // Coluna de Ações com botão para ver apontamentos
        TableColumn<Usuario, Void> acoesCol = new TableColumn<>("Ações");
        acoesCol.setPrefWidth(150);
        acoesCol.setMaxWidth(200);
        acoesCol.setCellFactory(param -> new TableCell<>() {
            private final Button apontamentosBtn = new Button("Ver Apontamentos");

            {
                apontamentosBtn.setStyle(
                    "-fx-background-color: #FF9800; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 11px; " +
                    "-fx-padding: 8 12; " +
                    "-fx-background-radius: 5; " +
                    "-fx-cursor: hand;"
                );

                apontamentosBtn.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    showApontamentosPorUsuarioDialog(usuario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : apontamentosBtn);
                setAlignment(Pos.CENTER);
            }
        });

        table.getColumns().addAll(idCol, nomeCol, loginCol, acoesCol);

        container.getChildren().add(table);
        VBox.setVgrow(table, Priority.ALWAYS);

        return container;
    }

    private void showApontamentosPorUsuarioDialog(Usuario usuario) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Apontamentos do Usuário");
        dialog.setHeaderText("Apontamentos de: " + usuario.getNome());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(700);

        try {
            List<ApontamentoDeHoras> apontamentos = arqApontamento.listarPorUsuario(usuario.getId());

            if (apontamentos.isEmpty()) {
                Label emptyLabel = new Label("Nenhum apontamento registrado para este usuário.");
                emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
                content.getChildren().add(emptyLabel);
            } else {
                TableView<ApontamentoDeHoras> tableApont = new TableView<>();
                ObservableList<ApontamentoDeHoras> apontList = FXCollections.observableArrayList(apontamentos);
                tableApont.setItems(apontList);
                tableApont.setPrefHeight(300);
                tableApont.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

                TableColumn<ApontamentoDeHoras, Integer> idCol = new TableColumn<>("ID");
                idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
                idCol.setPrefWidth(50);

                TableColumn<ApontamentoDeHoras, Integer> tarefaCol = new TableColumn<>("ID Tarefa");
                tarefaCol.setCellValueFactory(new PropertyValueFactory<>("idTarefa"));
                tarefaCol.setPrefWidth(100);

                TableColumn<ApontamentoDeHoras, Double> duracaoCol = new TableColumn<>("Duração (h)");
                duracaoCol.setCellValueFactory(new PropertyValueFactory<>("duracao"));
                duracaoCol.setPrefWidth(100);

                TableColumn<ApontamentoDeHoras, String> descCol = new TableColumn<>("Descrição");
                descCol.setCellValueFactory(new PropertyValueFactory<>("descricao"));
                descCol.setPrefWidth(350);

                tableApont.getColumns().addAll(idCol, tarefaCol, duracaoCol, descCol);

                // Calcular total de horas
                double totalHoras = apontamentos.stream()
                    .mapToDouble(ApontamentoDeHoras::getDuracao)
                    .sum();

                Label totalLabel = new Label(String.format("Total de horas apontadas: %.2f h", totalHoras));
                totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

                // Card com estatísticas adicionais
                HBox statsBox = new HBox(20);
                statsBox.setAlignment(Pos.CENTER);
                statsBox.setPadding(new Insets(15));
                statsBox.setStyle("-fx-background-color: #f5f7fa; -fx-background-radius: 10;");

                VBox totalCard = createStatCard("Total de Apontamentos", String.valueOf(apontamentos.size()), "#4ECDC4");
                VBox horasCard = createStatCard("Total de Horas", String.format("%.2f h", totalHoras), "#FF6B6B");

                statsBox.getChildren().addAll(totalCard, horasCard);

                content.getChildren().addAll(statsBox, tableApont, totalLabel);
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao carregar apontamentos");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

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

    private Node createBuscarPadraoForm(UsuarioController controller) {
        VBox container = new VBox(15);
        container.setMaxWidth(600);
        container.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10;");

        TextField padraoField = new TextField();
        padraoField.setPromptText("Padrão a buscar");
        styleField(padraoField);

        ComboBox<String> algoritmoBox = new ComboBox<>();
        algoritmoBox.getItems().addAll("KMP (Knuth-Morris-Pratt)", "Boyer-Moore");
        algoritmoBox.setValue("KMP (Knuth-Morris-Pratt)");

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(10);

        Button buscarBtn = createButton("Buscar", "#667eea");
        buscarBtn.setMaxWidth(Double.MAX_VALUE);

        buscarBtn.setOnAction(e -> {
            String opcao = algoritmoBox.getValue().startsWith("KMP") ? "1" : "2";
            lastInput = padraoField.getText() + "\n" + opcao;
            controller.buscarPorPadrao();
        });

        container.getChildren().addAll(
            new Label("Padrão:"),
            padraoField,
            new Label("Algoritmo:"),
            algoritmoBox,
            buscarBtn,
            resultArea
        );

        return container;
    }

    private Node createGerenciarTelefonesForm(UsuarioController controller) {
        VBox container = new VBox(15);
        container.setMaxWidth(600);
        container.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10;");

        int idUsuario = SessionManager.getInstance().getIdUsuarioLogado();

        TextField telefoneField = new TextField();
        telefoneField.setPromptText("Número de telefone");
        styleField(telefoneField);

        ListView<String> listView = new ListView<>();
        listView.setPrefHeight(200);

        Button adicionarBtn = createButton("Adicionar Telefone", "#4CAF50");
        Button removerBtn = createButton("Remover Selecionado", "#f44336");

        HBox btnBox = new HBox(10, adicionarBtn, removerBtn);

        adicionarBtn.setOnAction(e -> {
            lastInput = idUsuario + "\n1\n" + telefoneField.getText();
            controller.gerenciarTelefones();
            telefoneField.clear();
        });

        removerBtn.setOnAction(e -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                lastInput = idUsuario + "\n2\n" + selected;
                controller.gerenciarTelefones();
            }
        });

        container.getChildren().addAll(
            new Label("Meus Telefones:"),
            listView,
            new Label("Novo Telefone:"),
            telefoneField,
            btnBox
        );

        return container;
    }

    private void styleField(TextInputControl field) {
        field.setStyle("-fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #e0e0e0; " +
                      "-fx-border-radius: 8; -fx-font-size: 14;");
    }

    private Button createButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                    "-fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 12 24; " +
                    "-fx-background-radius: 8; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.8));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
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
    public void exibirMensagem(String mensagem) {}

    @Override
    public void exibirErro(String erro) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setContentText(erro);
        alert.showAndWait();
    }

    @Override
    public void exibirSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
