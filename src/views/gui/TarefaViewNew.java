package views.gui;

import controllers.TarefaController;
import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import models.*;
import utils.SessionManager;
import views.MenuView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TarefaViewNew extends MenuView {

    private MainView mainView;
    private ArquivoUsuario arqUsuario;
    private ArquivoStatusTarefa arqStatus;
    private ArquivoCategoria arqCategoria;
    private ArquivoTarefaCategoriaHash arqTarefaCategoria;
    private ArquivoApontamentoDeHoras arqApontamento;
    private String lastInput = "";
    private TableView<TarefaExtendida> table;
    private ObservableList<TarefaExtendida> tarefasList;

    // Filtros
    private ComboBox<StatusTarefa> filtroStatus;
    private ComboBox<Usuario> filtroUsuario;
    private TextField filtroBusca;

    public TarefaViewNew(MainView mainView, ArquivoUsuario arqUsuario,
            ArquivoStatusTarefa arqStatus, ArquivoCategoria arqCategoria) {
        super(null);
        this.mainView = mainView;
        this.arqUsuario = arqUsuario;
        this.arqStatus = arqStatus;
        this.arqCategoria = arqCategoria;
        this.tarefasList = FXCollections.observableArrayList();

        try {
            this.arqTarefaCategoria = new ArquivoTarefaCategoriaHash();
            this.arqApontamento = new ArquivoApontamentoDeHoras();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Node createView(String action, TarefaController controller) {
        switch (action) {
            case "buscarPorId" -> {
                return createBuscarPorIdView(controller);
            }
            default -> {
                return createGerenciarView(controller);
            }
        }
    }

    private Node createGerenciarView(TarefaController controller) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f5f7fa;");

        // Título e botões
        Label title = new Label("Gerenciar Tarefas");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

        Button novaTarefaBtn = createStyledButton("+ Nova Tarefa", "#667eea");
        novaTarefaBtn.setOnAction(e -> showNovaTarefaDialog(controller));

        Button buscarIdBtn = createStyledButton("🔑 Buscar por ID (Hash)", "#FF9800");
        buscarIdBtn.setOnAction(e -> mainView.showTarefaView("buscarPorId"));

        HBox topBar = new HBox(15, title, novaTarefaBtn, buscarIdBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(title, Priority.ALWAYS);

        // Área de Filtros
        HBox filtrosBox = createFiltrosBox(controller);

        // Criar tabela
        createTable(controller);

        // Carregar dados
        loadTarefas(controller);

        VBox tableContainer = new VBox(10, table);
        tableContainer.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");
        VBox.setVgrow(table, Priority.ALWAYS);

        layout.getChildren().addAll(topBar, filtrosBox, new Separator(), tableContainer);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #f5f7fa; -fx-background-color: #f5f7fa;");
        return scrollPane;
    }

    private HBox createFiltrosBox(TarefaController controller) {
        HBox filtrosBox = new HBox(15);
        filtrosBox.setAlignment(Pos.CENTER_LEFT);
        filtrosBox.setPadding(new Insets(10));
        filtrosBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15;");

        // Filtro por Status
        Label lblStatus = new Label("Status:");
        lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        filtroStatus = new ComboBox<>();
        filtroStatus.setPromptText("Todos");
        filtroStatus.setPrefWidth(150);
        styleComboBox(filtroStatus);
        try {
            List<StatusTarefa> statusList = arqStatus.listarTodosAtivos();
            filtroStatus.getItems().add(null); // Opção "Todos"
            filtroStatus.getItems().addAll(statusList);
            filtroStatus.setConverter(new javafx.util.StringConverter<StatusTarefa>() {
                @Override
                public String toString(StatusTarefa status) {
                    return status != null ? status.getNome() : "Todos";
                }

                @Override
                public StatusTarefa fromString(String string) {
                    return null;
                }
            });
            filtroStatus.setValue(null);
            filtroStatus.setOnAction(e -> aplicarFiltros(controller));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Filtro por Usuário
        Label lblUsuario = new Label("Usuário:");
        lblUsuario.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        filtroUsuario = new ComboBox<>();
        filtroUsuario.setPromptText("Todos");
        filtroUsuario.setPrefWidth(150);
        styleComboBox(filtroUsuario);
        try {
            List<Usuario> usuarios = arqUsuario.listarAtivos();
            filtroUsuario.getItems().add(null); // Opção "Todos"
            filtroUsuario.getItems().addAll(usuarios);
            filtroUsuario.setConverter(new javafx.util.StringConverter<Usuario>() {
                @Override
                public String toString(Usuario usuario) {
                    return usuario != null ? usuario.getNome() : "Todos";
                }

                @Override
                public Usuario fromString(String string) {
                    return null;
                }
            });
            filtroUsuario.setValue(null);
            filtroUsuario.setOnAction(e -> aplicarFiltros(controller));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Busca por texto
        Label lblBusca = new Label("Buscar:");
        lblBusca.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        filtroBusca = new TextField();
        filtroBusca.setPromptText("Digite para buscar...");
        filtroBusca.setPrefWidth(200);
        styleTextField(filtroBusca);
        filtroBusca.textProperty().addListener((obs, old, novo) -> aplicarFiltros(controller));

        Button limparFiltros = createStyledButton("Limpar Filtros", "#999");
        limparFiltros.setOnAction(e -> {
            filtroStatus.setValue(null);
            filtroUsuario.setValue(null);
            filtroBusca.clear();
            loadTarefas(controller);
        });

        filtrosBox.getChildren().addAll(
                lblStatus, filtroStatus,
                lblUsuario, filtroUsuario,
                lblBusca, filtroBusca,
                limparFiltros);

        return filtrosBox;
    }

    private void aplicarFiltros(TarefaController controller) {
        try {
            // Listar TODAS as tarefas de TODOS os usuários
            List<Tarefa> todasTarefas = controller.getArquivoTarefa().listarTodosAtivos();

            // Aplicar filtros
            List<Tarefa> tarefasFiltradas = todasTarefas.stream()
                    .filter(t -> {
                        // Filtro de status
                        if (filtroStatus.getValue() != null) {
                            if (t.getIdStatus() != filtroStatus.getValue().getId()) {
                                return false;
                            }
                        }
                        // Filtro de usuário
                        if (filtroUsuario.getValue() != null) {
                            if (t.getIdUsuario() != filtroUsuario.getValue().getId()) {
                                return false;
                            }
                        }
                        // Filtro de busca
                        if (filtroBusca.getText() != null && !filtroBusca.getText().isEmpty()) {
                            String busca = filtroBusca.getText().toLowerCase();
                            return t.getTitulo().toLowerCase().contains(busca) ||
                                    t.getDescricao().toLowerCase().contains(busca);
                        }
                        return true;
                    })
                    .collect(Collectors.toList());

            // Atualizar tabela
            tarefasList.clear();
            for (Tarefa tarefa : tarefasFiltradas) {
                tarefasList.add(new TarefaExtendida(tarefa));
            }
        } catch (Exception e) {
            showError("Erro ao aplicar filtros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTable(TarefaController controller) {
        table = new TableView<>();
        table.setItems(tarefasList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Coluna ID
        TableColumn<TarefaExtendida, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTarefa().getId())
                        .asObject());
        idCol.setPrefWidth(50);
        idCol.setMaxWidth(80);

        // Coluna Título
        TableColumn<TarefaExtendida, String> tituloCol = new TableColumn<>("Título");
        tituloCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTarefa().getTitulo()));
        tituloCol.setPrefWidth(180);

        // Coluna Descrição
        TableColumn<TarefaExtendida, String> descricaoCol = new TableColumn<>("Descrição");
        descricaoCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTarefa().getDescricao()));
        descricaoCol.setPrefWidth(200);

        // Coluna Usuário
        TableColumn<TarefaExtendida, String> usuarioCol = new TableColumn<>("Usuário");
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

        // Coluna Categorias
        TableColumn<TarefaExtendida, String> categoriasCol = new TableColumn<>("Categorias");
        categoriasCol.setPrefWidth(150);
        categoriasCol.setCellValueFactory(cellData -> {
            try {
                List<TarefaCategoria> relacoes = arqTarefaCategoria.listarPorTarefa(
                        cellData.getValue().getTarefa().getId());
                if (relacoes.isEmpty()) {
                    return new javafx.beans.property.SimpleStringProperty("-");
                }

                List<String> nomesCategorias = new ArrayList<>();
                for (TarefaCategoria tc : relacoes) {
                    Categoria cat = arqCategoria.read(tc.getIdCategoria());
                    if (cat != null) {
                        nomesCategorias.add(cat.getNome());
                    }
                }
                return new javafx.beans.property.SimpleStringProperty(
                        String.join(", ", nomesCategorias));
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
                        s != null ? s.getNome() : "Desconhecido");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Erro");
            }
        });

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

        // Coluna Ações
        TableColumn<TarefaExtendida, Void> acoesCol = new TableColumn<>("Ações");
        acoesCol.setPrefWidth(300);
        acoesCol.setMaxWidth(350);
        acoesCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Editar");
            private final Button categoriasBtn = new Button("Categorias");
            private final Button apontamentosBtn = new Button("Apontamentos");
            private final Button deleteBtn = new Button("Deletar");
            private final HBox pane = new HBox(5, editBtn, categoriasBtn, apontamentosBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                        "-fx-font-size: 10px; -fx-padding: 5 8; -fx-background-radius: 5; " +
                        "-fx-cursor: hand;");
                categoriasBtn.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; " +
                        "-fx-font-size: 10px; -fx-padding: 5 8; -fx-background-radius: 5; " +
                        "-fx-cursor: hand;");
                apontamentosBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; " +
                        "-fx-font-size: 10px; -fx-padding: 5 8; -fx-background-radius: 5; " +
                        "-fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                        "-fx-font-size: 10px; -fx-padding: 5 8; -fx-background-radius: 5; " +
                        "-fx-cursor: hand;");
                pane.setAlignment(Pos.CENTER);

                editBtn.setOnAction(event -> {
                    Tarefa tarefa = getTableView().getItems().get(getIndex()).getTarefa();
                    showEditarTarefaDialog(tarefa, controller);
                });

                categoriasBtn.setOnAction(event -> {
                    Tarefa tarefa = getTableView().getItems().get(getIndex()).getTarefa();
                    showGerenciarCategoriasDialog(tarefa, controller);
                });

                apontamentosBtn.setOnAction(event -> {
                    Tarefa tarefa = getTableView().getItems().get(getIndex()).getTarefa();
                    showApontamentosPorTarefaDialog(tarefa);
                });

                deleteBtn.setOnAction(event -> {
                    Tarefa tarefa = getTableView().getItems().get(getIndex()).getTarefa();
                    deletarTarefa(tarefa, controller);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(idCol, tituloCol, descricaoCol, usuarioCol,
                categoriasCol, statusCol, acoesCol);
    }

    private void showApontamentosPorTarefaDialog(Tarefa tarefa) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Apontamentos da Tarefa");
        dialog.setHeaderText("Apontamentos de: " + tarefa.getTitulo());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(700);

        try {
            List<ApontamentoDeHoras> apontamentos = arqApontamento.listarPorTarefa(tarefa.getId());

            if (apontamentos.isEmpty()) {
                Label emptyLabel = new Label("Nenhum apontamento registrado para esta tarefa.");
                emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
                content.getChildren().add(emptyLabel);
            } else {
                TableView<ApontamentoDeHoras> tableApont = new TableView<>();
                ObservableList<ApontamentoDeHoras> apontList = FXCollections.observableArrayList(apontamentos);
                tableApont.setItems(apontList);
                tableApont.setPrefHeight(300);

                TableColumn<ApontamentoDeHoras, Integer> idCol = new TableColumn<>("ID");
                idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
                idCol.setPrefWidth(50);

                TableColumn<ApontamentoDeHoras, String> usuarioCol = new TableColumn<>("Usuário");
                usuarioCol.setPrefWidth(150);
                usuarioCol.setCellValueFactory(cellData -> {
                    try {
                        Usuario u = arqUsuario.read(cellData.getValue().getIdUsuario());
                        return new javafx.beans.property.SimpleStringProperty(
                                u != null ? u.getNome() : "Desconhecido");
                    } catch (Exception e) {
                        return new javafx.beans.property.SimpleStringProperty("Erro");
                    }
                });

                TableColumn<ApontamentoDeHoras, Double> duracaoCol = new TableColumn<>("Duração (h)");
                duracaoCol.setCellValueFactory(new PropertyValueFactory<>("duracao"));
                duracaoCol.setPrefWidth(100);

                TableColumn<ApontamentoDeHoras, String> descCol = new TableColumn<>("Descrição");
                descCol.setCellValueFactory(new PropertyValueFactory<>("descricao"));
                descCol.setPrefWidth(300);

                tableApont.getColumns().addAll(idCol, usuarioCol, duracaoCol, descCol);

                // Calcular total de horas
                double totalHoras = apontamentos.stream()
                        .mapToDouble(ApontamentoDeHoras::getDuracao)
                        .sum();

                Label totalLabel = new Label(String.format("Total de horas apontadas: %.2f h", totalHoras));
                totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

                content.getChildren().addAll(tableApont, totalLabel);
            }
        } catch (Exception e) {
            showError("Erro ao carregar apontamentos: " + e.getMessage());
            e.printStackTrace();
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showGerenciarCategoriasDialog(Tarefa tarefa, TarefaController controller) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Gerenciar Categorias");
        dialog.setHeaderText("Categorias da Tarefa: " + tarefa.getTitulo());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(600);

        // Lista de categorias atuais
        ListView<String> listaCategorias = new ListView<>();
        listaCategorias.setPrefHeight(200);

        // Função para recarregar categorias
        Runnable recarregarCategorias = () -> {
            try {
                List<TarefaCategoria> relacoes = arqTarefaCategoria.listarPorTarefa(tarefa.getId());
                ObservableList<String> items = FXCollections.observableArrayList();

                for (TarefaCategoria tc : relacoes) {
                    Categoria cat = arqCategoria.read(tc.getIdCategoria());
                    if (cat != null) {
                        items.add(cat.getNome() + " (Prioridade: " + tc.getPrioridade() + ")");
                    }
                }

                listaCategorias.setItems(items);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        recarregarCategorias.run();

        // Botões de ação
        HBox botoesBox = new HBox(10);
        botoesBox.setAlignment(Pos.CENTER);

        Button adicionarBtn = createStyledButton("Adicionar Categoria", "#4CAF50");
        adicionarBtn.setOnAction(e -> {
            try {
                List<Categoria> todasCategorias = arqCategoria.listarTodosAtivos();
                List<TarefaCategoria> categoriasAtuais = arqTarefaCategoria.listarPorTarefa(tarefa.getId());

                // Filtrar categorias já adicionadas
                List<Categoria> categoriasDisponiveis = todasCategorias.stream()
                        .filter(cat -> categoriasAtuais.stream()
                                .noneMatch(tc -> tc.getIdCategoria() == cat.getId()))
                        .collect(Collectors.toList());

                if (categoriasDisponiveis.isEmpty()) {
                    showError("Todas as categorias já foram adicionadas a esta tarefa.");
                    return;
                }

                Dialog<ButtonType> addDialog = new Dialog<>();
                addDialog.setTitle("Adicionar Categoria");
                addDialog.setHeaderText("Selecione a categoria");

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20));

                ComboBox<Categoria> catCombo = new ComboBox<>();
                catCombo.getItems().addAll(categoriasDisponiveis);
                catCombo.setConverter(new javafx.util.StringConverter<Categoria>() {
                    @Override
                    public String toString(Categoria cat) {
                        return cat != null ? cat.getNome() : "";
                    }

                    @Override
                    public Categoria fromString(String string) {
                        return null;
                    }
                });
                styleComboBox(catCombo);

                Spinner<Integer> prioridadeSpinner = new Spinner<>(1, 10, 5);
                prioridadeSpinner.setEditable(true);
                prioridadeSpinner.setPrefWidth(100);

                grid.add(new Label("Categoria:"), 0, 0);
                grid.add(catCombo, 1, 0);
                grid.add(new Label("Prioridade (1-10):"), 0, 1);
                grid.add(prioridadeSpinner, 1, 1);

                addDialog.getDialogPane().setContent(grid);
                addDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                Optional<ButtonType> result = addDialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    if (catCombo.getValue() == null) {
                        showError("Selecione uma categoria!");
                        return;
                    }

                    TarefaCategoria tc = new TarefaCategoria(
                            tarefa.getId(),
                            catCombo.getValue().getId(),
                            prioridadeSpinner.getValue());

                    arqTarefaCategoria.create(tc);
                    recarregarCategorias.run();
                    loadTarefas(controller); // Recarregar tabela principal
                    showSuccess("Categoria adicionada com sucesso!");
                }
            } catch (Exception ex) {
                showError("Erro ao adicionar categoria: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        Button removerBtn = createStyledButton("Remover Categoria", "#f44336");
        removerBtn.setOnAction(e -> {
            try {
                List<TarefaCategoria> relacoes = arqTarefaCategoria.listarPorTarefa(tarefa.getId());

                if (relacoes.isEmpty()) {
                    showError("Nenhuma categoria para remover.");
                    return;
                }

                Dialog<ButtonType> removeDialog = new Dialog<>();
                removeDialog.setTitle("Remover Categoria");
                removeDialog.setHeaderText("Selecione a categoria a remover");

                VBox box = new VBox(10);
                box.setPadding(new Insets(20));

                ComboBox<TarefaCategoria> tcCombo = new ComboBox<>();
                tcCombo.getItems().addAll(relacoes);
                tcCombo.setConverter(new javafx.util.StringConverter<TarefaCategoria>() {
                    @Override
                    public String toString(TarefaCategoria tc) {
                        if (tc == null)
                            return "";
                        try {
                            Categoria cat = arqCategoria.read(tc.getIdCategoria());
                            return cat != null ? cat.getNome() : "Categoria " + tc.getIdCategoria();
                        } catch (Exception e) {
                            return "Erro";
                        }
                    }

                    @Override
                    public TarefaCategoria fromString(String string) {
                        return null;
                    }
                });
                styleComboBox(tcCombo);

                box.getChildren().addAll(new Label("Categoria:"), tcCombo);

                removeDialog.getDialogPane().setContent(box);
                removeDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                Optional<ButtonType> result = removeDialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    if (tcCombo.getValue() == null) {
                        showError("Selecione uma categoria!");
                        return;
                    }

                    arqTarefaCategoria.delete(tarefa.getId(), tcCombo.getValue().getIdCategoria());
                    recarregarCategorias.run();
                    loadTarefas(controller); // Recarregar tabela principal
                    showSuccess("Categoria removida com sucesso!");
                }
            } catch (Exception ex) {
                showError("Erro ao remover categoria: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        botoesBox.getChildren().addAll(adicionarBtn, removerBtn);

        content.getChildren().addAll(
                new Label("Categorias atuais:"),
                listaCategorias,
                botoesBox);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.setOnCloseRequest(e -> loadTarefas(controller));

        dialog.showAndWait();
    }

    private void loadTarefas(TarefaController controller) {
        try {
            // Listar TODAS as tarefas de TODOS os usuários
            List<Tarefa> tarefas = controller.getArquivoTarefa().listarTodosAtivos();

            tarefasList.clear();
            for (Tarefa tarefa : tarefas) {
                tarefasList.add(new TarefaExtendida(tarefa));
            }
        } catch (Exception e) {
            showError("Erro ao carregar tarefas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showNovaTarefaDialog(TarefaController controller) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nova Tarefa");
        dialog.setHeaderText("Criar uma nova tarefa");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField tituloField = new TextField();
        tituloField.setPromptText("Título da tarefa");
        styleTextField(tituloField);

        TextArea descricaoArea = new TextArea();
        descricaoArea.setPromptText("Descrição da tarefa");
        descricaoArea.setPrefRowCount(4);
        styleTextField(descricaoArea);

        ComboBox<StatusTarefa> statusCombo = new ComboBox<>();
        try {
            List<StatusTarefa> statusList = arqStatus.listarTodosAtivos();
            statusCombo.getItems().addAll(statusList);
            if (!statusCombo.getItems().isEmpty()) {
                statusCombo.setValue(statusCombo.getItems().get(0));
            }
            statusCombo.setConverter(new javafx.util.StringConverter<StatusTarefa>() {
                @Override
                public String toString(StatusTarefa status) {
                    return status != null ? status.getNome() : "";
                }

                @Override
                public StatusTarefa fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            showError("Erro ao carregar status: " + e.getMessage());
        }
        styleComboBox(statusCombo);

        grid.add(new Label("Título:"), 0, 0);
        grid.add(tituloField, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(descricaoArea, 1, 1);
        grid.add(new Label("Status:"), 0, 2);
        grid.add(statusCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (tituloField.getText().isEmpty() || descricaoArea.getText().isEmpty()) {
                showError("Título e descrição são obrigatórios!");
                return;
            }

            try {
                int idUsuario = SessionManager.getInstance().getIdUsuarioLogado();
                StatusTarefa statusSelecionado = statusCombo.getValue();

                if (statusSelecionado == null) {
                    showError("Selecione um status!");
                    return;
                }

                lastInput = idUsuario + "\n" +
                        tituloField.getText() + "\n" +
                        descricaoArea.getText() + "\n" +
                        statusSelecionado.getId();

                controller.criarTarefa();
                loadTarefas(controller);
                showSuccess("Tarefa criada com sucesso!");

            } catch (Exception ex) {
                showError("Erro ao criar tarefa: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void showEditarTarefaDialog(Tarefa tarefa, TarefaController controller) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Tarefa");
        dialog.setHeaderText("Editar tarefa ID: " + tarefa.getId());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField tituloField = new TextField(tarefa.getTitulo());
        styleTextField(tituloField);

        TextArea descricaoArea = new TextArea(tarefa.getDescricao());
        descricaoArea.setPrefRowCount(4);
        styleTextField(descricaoArea);

        ComboBox<StatusTarefa> statusCombo = new ComboBox<>();
        try {
            List<StatusTarefa> statusList = arqStatus.listarTodosAtivos();
            statusCombo.getItems().addAll(statusList);

            StatusTarefa statusAtual = arqStatus.read(tarefa.getIdStatus());
            if (statusAtual != null) {
                statusCombo.setValue(statusAtual);
            } else if (!statusCombo.getItems().isEmpty()) {
                statusCombo.setValue(statusCombo.getItems().get(0));
            }

            statusCombo.setConverter(new javafx.util.StringConverter<StatusTarefa>() {
                @Override
                public String toString(StatusTarefa status) {
                    return status != null ? status.getNome() : "";
                }

                @Override
                public StatusTarefa fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            showError("Erro ao carregar status: " + e.getMessage());
        }
        styleComboBox(statusCombo);

        grid.add(new Label("Título:"), 0, 0);
        grid.add(tituloField, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(descricaoArea, 1, 1);
        grid.add(new Label("Status:"), 0, 2);
        grid.add(statusCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                StatusTarefa statusSelecionado = statusCombo.getValue();

                if (statusSelecionado == null) {
                    showError("Selecione um status!");
                    return;
                }

                tarefa.setTitulo(tituloField.getText());
                tarefa.setDescricao(descricaoArea.getText());
                tarefa.setIdStatus(statusSelecionado.getId());

                if (controller.getArquivoTarefa().update(tarefa)) {
                    loadTarefas(controller);
                    showSuccess("Tarefa atualizada com sucesso!");
                } else {
                    showError("Falha ao atualizar tarefa.");
                }

            } catch (Exception ex) {
                showError("Erro ao atualizar tarefa: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void deletarTarefa(Tarefa tarefa, TarefaController controller) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Exclusão");
        confirmAlert.setHeaderText("Deletar tarefa?");
        confirmAlert.setContentText("Tem certeza que deseja deletar a tarefa '" + tarefa.getTitulo() + "'?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                lastInput = String.valueOf(tarefa.getId());
                controller.deletarTarefa();
                loadTarefas(controller);
                showSuccess("Tarefa deletada com sucesso!");
            } catch (Exception ex) {
                showError("Erro ao deletar tarefa: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Cria a interface visual de busca de tarefa por ID usando Hash Extensível
     */
    private Node createBuscarPorIdView(TarefaController controller) {
        VBox layout = new VBox(30);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: #f5f7fa;");
        layout.setAlignment(Pos.TOP_CENTER);

        // Header
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);

        Label title = new Label("🔍 Buscar Tarefa por ID");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

        Label subtitle = new Label("Busca otimizada usando Hash Extensível - Complexidade O(1)");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #999; -fx-font-style: italic;");

        headerBox.getChildren().addAll(title, subtitle);

        // Card de busca
        VBox searchCard = new VBox(25);
        searchCard.setMaxWidth(700);
        searchCard.setAlignment(Pos.CENTER);
        searchCard.setPadding(new Insets(40));
        searchCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.4), 20, 0, 0, 5);"
        );

        // Ícone e descrição
        Label iconLabel = new Label("🔑");
        iconLabel.setStyle("-fx-font-size: 60px;");

        Label descLabel = new Label("Digite o ID da tarefa para buscá-la instantaneamente");
        descLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666; -fx-text-alignment: center;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(600);

        // Campo de ID
        HBox inputBox = new HBox(15);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setMaxWidth(500);

        TextField idField = new TextField();
        idField.setPromptText("Digite o ID da tarefa");
        idField.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-padding: 15; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #667eea; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-min-width: 300;"
        );

        Button buscarBtn = new Button("Buscar");
        buscarBtn.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15 40; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.5), 10, 0, 0, 3);"
        );
        buscarBtn.setOnMouseEntered(e -> buscarBtn.setStyle(
            "-fx-background-color: #5568d3; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15 40; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.7), 15, 0, 0, 5);"
        ));
        buscarBtn.setOnMouseExited(e -> buscarBtn.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15 40; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.5), 10, 0, 0, 3);"
        ));

        inputBox.getChildren().addAll(idField, buscarBtn);

        // Área de resultado
        VBox resultBox = new VBox(15);
        resultBox.setStyle(
            "-fx-background-color: #f8f9ff; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 20; " +
            "-fx-border-color: #e0e7ff; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10;"
        );
        resultBox.setMaxWidth(650);
        resultBox.setVisible(false);

        Label resultTitle = new Label("Resultado da Busca:");
        resultTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

        VBox tarefaInfo = new VBox(10);
        tarefaInfo.setStyle("-fx-padding: 10;");

        resultBox.getChildren().addAll(resultTitle, tarefaInfo);

        // Ação do botão buscar
        buscarBtn.setOnAction(e -> {
            String idStr = idField.getText().trim();
            if (idStr.isEmpty()) {
                showError("Por favor, digite um ID!");
                return;
            }

            try {
                int id = Integer.parseInt(idStr);

                // Medir tempo de busca para mostrar performance do hash
                long startTime = System.nanoTime();
                Tarefa tarefa = controller.getArquivoTarefa().read(id);
                long endTime = System.nanoTime();
                double tempoMs = (endTime - startTime) / 1_000_000.0;

                tarefaInfo.getChildren().clear();

                if (tarefa == null) {
                    Label notFoundLabel = new Label("❌ Tarefa com ID " + id + " não encontrada.");
                    notFoundLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #f44336; -fx-font-weight: bold;");
                    tarefaInfo.getChildren().add(notFoundLabel);
                } else {
                    // Informações da tarefa
                    Label idLabel = createInfoLabel("ID:", String.valueOf(tarefa.getId()), "#667eea");
                    Label tituloLabel = createInfoLabel("Título:", tarefa.getTitulo(), "#333");
                    Label descricaoLabel = createInfoLabel("Descrição:", tarefa.getDescricao(), "#555");

                    try {
                        Usuario usuario = arqUsuario.read(tarefa.getIdUsuario());
                        String nomeUsuario = usuario != null ? usuario.getNome() : "Desconhecido";
                        Label usuarioLabel = createInfoLabel("Responsável:", nomeUsuario, "#555");

                        StatusTarefa status = arqStatus.read(tarefa.getIdStatus());
                        String statusNome = status != null ? status.getNome() : "Sem status";
                        Label statusLabel = createInfoLabel("Status:", statusNome, getStatusColorText(statusNome));

                        // Tempo de busca
                        Label tempoLabel = new Label(String.format("⚡ Tempo de busca: %.3f ms (Hash Extensível)", tempoMs));
                        tempoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");

                        tarefaInfo.getChildren().addAll(idLabel, tituloLabel, descricaoLabel,
                            usuarioLabel, statusLabel, new Separator(), tempoLabel);

                        // Botão para abrir detalhes
                        Button detalhesBtn = createStyledButton("Ver Detalhes Completos", "#667eea");
                        detalhesBtn.setOnAction(ev -> {
                            mainView.showTarefaView("gerenciar");
                        });
                        tarefaInfo.getChildren().add(detalhesBtn);

                    } catch (Exception ex) {
                        showError("Erro ao carregar detalhes: " + ex.getMessage());
                    }
                }

                resultBox.setVisible(true);

            } catch (NumberFormatException ex) {
                showError("ID inválido! Digite apenas números.");
            } catch (Exception ex) {
                showError("Erro ao buscar tarefa: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Permitir buscar com Enter
        idField.setOnAction(e -> buscarBtn.fire());

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
        voltarBtn.setOnAction(e -> mainView.showTarefaView("gerenciar"));

        searchCard.getChildren().addAll(iconLabel, descLabel, inputBox, resultBox);

        layout.getChildren().addAll(headerBox, searchCard, voltarBtn);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f7fa; -fx-background-color: #f5f7fa;");
        return scrollPane;
    }

    private Label createInfoLabel(String label, String value, String color) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);

        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #999;");
        labelText.setMinWidth(100);

        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 14px; -fx-text-fill: " + color + "; -fx-font-weight: bold;");
        valueText.setWrapText(true);
        valueText.setMaxWidth(500);

        Label result = new Label(label + " " + value);
        result.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
        return result;
    }

    private String getStatusColorText(String statusNome) {
        return switch (statusNome.toLowerCase()) {
            case "pendente" -> "#FFC107";
            case "em andamento" -> "#2196F3";
            case "concluída", "concluida" -> "#4CAF50";
            default -> "#999";
        };
    }

    // Métodos auxiliares de estilo
    private void styleTextField(TextInputControl field) {
        field.setStyle(
                "-fx-padding: 12; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 8; " +
                        "-fx-font-size: 14;");
        field.setPrefWidth(300);
    }

    private void styleComboBox(ComboBox<?> combo) {
        combo.setStyle(
                "-fx-padding: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 8; " +
                        "-fx-font-size: 14;");
        combo.setPrefWidth(300);
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
                        "-fx-cursor: hand;");
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

    // Classe auxiliar para wrapper de Tarefa
    public static class TarefaExtendida {
        private final Tarefa tarefa;

        public TarefaExtendida(Tarefa tarefa) {
            this.tarefa = tarefa;
        }

        public Tarefa getTarefa() {
            return tarefa;
        }
    }

    // Métodos da MenuView
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
        // Não fazer nada - mensagens serão exibidas via alerts
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
