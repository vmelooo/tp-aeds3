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
        VBox layout = new VBox(30);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: #f5f7fa;");
        layout.setAlignment(Pos.TOP_CENTER);

        // Header
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);

        Label title = new Label("🔍 Buscar Usuários por Padrão");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

        Label subtitle = new Label("Algoritmos de busca por padrão: KMP e Boyer-Moore");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #999; -fx-font-style: italic;");

        headerBox.getChildren().addAll(title, subtitle);

        // Card de busca
        VBox searchCard = new VBox(25);
        searchCard.setMaxWidth(800);
        searchCard.setAlignment(Pos.CENTER);
        searchCard.setPadding(new Insets(40));
        searchCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.4), 20, 0, 0, 5);"
        );

        // Ícone e descrição
        Label iconLabel = new Label("🔤");
        iconLabel.setStyle("-fx-font-size: 60px;");

        Label descLabel = new Label("Digite um padrão para buscar nos nomes dos usuários");
        descLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666; -fx-text-alignment: center;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(700);

        // Campo de padrão
        VBox inputSection = new VBox(15);
        inputSection.setAlignment(Pos.CENTER);
        inputSection.setMaxWidth(700);

        Label padraoLabel = new Label("Padrão de busca:");
        padraoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        TextField padraoField = new TextField();
        padraoField.setPromptText("Ex: Silva, João, Ana...");
        padraoField.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-padding: 15; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #667eea; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10;"
        );
        padraoField.setMaxWidth(500);

        // Seleção de algoritmo
        Label algLabel = new Label("Algoritmo:");
        algLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        HBox algoritmoBox = new HBox(15);
        algoritmoBox.setAlignment(Pos.CENTER);

        ToggleGroup algoritmoGroup = new ToggleGroup();

        RadioButton kmpRadio = new RadioButton("KMP (Knuth-Morris-Pratt)");
        kmpRadio.setToggleGroup(algoritmoGroup);
        kmpRadio.setSelected(true);
        kmpRadio.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        RadioButton bmRadio = new RadioButton("Boyer-Moore");
        bmRadio.setToggleGroup(algoritmoGroup);
        bmRadio.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        algoritmoBox.getChildren().addAll(kmpRadio, bmRadio);

        inputSection.getChildren().addAll(padraoLabel, padraoField, algLabel, algoritmoBox);

        // Botão buscar
        Button buscarBtn = new Button("🔍 Buscar");
        buscarBtn.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15 50; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.5), 10, 0, 0, 3);"
        );
        buscarBtn.setOnMouseEntered(e -> buscarBtn.setStyle(
            "-fx-background-color: #5568d3; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15 50; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.7), 15, 0, 0, 5);"
        ));
        buscarBtn.setOnMouseExited(e -> buscarBtn.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15 50; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.5), 10, 0, 0, 3);"
        ));

        // Área de resultados
        VBox resultBox = new VBox(15);
        resultBox.setStyle(
            "-fx-background-color: #f8f9ff; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 20; " +
            "-fx-border-color: #e0e7ff; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10;"
        );
        resultBox.setMaxWidth(750);
        resultBox.setVisible(false);

        Label resultTitle = new Label("📊 Resultados da Busca:");
        resultTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

        VBox resultList = new VBox(10);
        resultList.setStyle("-fx-padding: 10;");

        ScrollPane resultScroll = new ScrollPane(resultList);
        resultScroll.setFitToWidth(true);
        resultScroll.setPrefHeight(300);
        resultScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        resultBox.getChildren().addAll(resultTitle, resultScroll);

        // Ação de buscar
        buscarBtn.setOnAction(e -> {
            String padrao = padraoField.getText().trim();
            if (padrao.isEmpty()) {
                showError("Por favor, digite um padrão de busca!");
                return;
            }

            String opcao = kmpRadio.isSelected() ? "1" : "2";
            String algoritmoNome = kmpRadio.isSelected() ? "KMP" : "Boyer-Moore";

            try {
                // Medir tempo de busca
                long startTime = System.nanoTime();

                lastInput = padrao + "\n" + opcao;
                java.util.List<dao.ArquivoUsuario> temp = new java.util.ArrayList<>();

                // Executar busca
                java.util.List<models.Usuario> usuarios = arqUsuario.listarAtivos();
                java.util.List<String> resultados = new java.util.ArrayList<>();

                for (models.Usuario u : usuarios) {
                    String texto = u.getNome();
                    java.util.List<Integer> indices;

                    if (opcao.equals("1")) {
                        indices = dao.PatternMatching.searchKMP(texto, padrao);
                    } else {
                        indices = dao.PatternMatching.searchBoyerMoore(texto, padrao);
                    }

                    if (!indices.isEmpty()) {
                        String destaque = criarDestaque(texto, indices, padrao.length());
                        resultados.add("ID " + u.getId() + ": " + destaque + " (" + u.getLogin() + ")");
                    }
                }

                long endTime = System.nanoTime();
                double tempoMs = (endTime - startTime) / 1_000_000.0;

                resultList.getChildren().clear();

                if (resultados.isEmpty()) {
                    Label noResults = new Label("❌ Nenhum usuário encontrado com o padrão \"" + padrao + "\"");
                    noResults.setStyle("-fx-font-size: 14px; -fx-text-fill: #f44336; -fx-font-weight: bold;");
                    resultList.getChildren().add(noResults);
                } else {
                    Label countLabel = new Label("✅ " + resultados.size() + " usuário(s) encontrado(s)");
                    countLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    resultList.getChildren().add(countLabel);

                    for (String resultado : resultados) {
                        Label itemLabel = new Label("• " + resultado);
                        itemLabel.setStyle(
                            "-fx-font-size: 13px; " +
                            "-fx-text-fill: #333; " +
                            "-fx-padding: 8; " +
                            "-fx-background-color: white; " +
                            "-fx-background-radius: 5;"
                        );
                        itemLabel.setWrapText(true);
                        itemLabel.setMaxWidth(680);
                        resultList.getChildren().add(itemLabel);
                    }

                    Label tempoLabel = new Label(String.format("⚡ Tempo de busca: %.3f ms usando %s", tempoMs, algoritmoNome));
                    tempoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #9C27B0; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");
                    resultList.getChildren().add(tempoLabel);
                }

                resultBox.setVisible(true);

            } catch (Exception ex) {
                showError("Erro ao buscar: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Permitir buscar com Enter
        padraoField.setOnAction(e -> buscarBtn.fire());

        // Botão voltar
        Button voltarBtn = new Button("← Voltar");
        voltarBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #667eea; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-cursor: hand; " +
            "-fx-underline: true;"
        );
        voltarBtn.setOnAction(e -> {
            if (mainView != null) {
                mainView.showMainScreen();
            }
        });

        searchCard.getChildren().addAll(iconLabel, descLabel, inputSection, buscarBtn, resultBox);

        layout.getChildren().addAll(headerBox, searchCard, voltarBtn);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f7fa; -fx-background-color: #f5f7fa;");
        return scrollPane;
    }

    private String criarDestaque(String texto, java.util.List<Integer> indices, int len) {
        StringBuilder sb = new StringBuilder(texto);
        for (int i = indices.size() - 1; i >= 0; i--) {
            int idx = indices.get(i);
            sb.insert(idx + len, "]");
            sb.insert(idx, "[");
        }
        return sb.toString();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setContentText(message);
        alert.showAndWait();
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
