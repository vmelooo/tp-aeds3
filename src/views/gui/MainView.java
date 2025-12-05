package views.gui;

import controllers.*;
import dao.*;
import models.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import utils.SessionManager;

public class MainView extends Application {

    private Stage primaryStage;
    private BorderPane mainLayout;
    private Scene loginScene;
    private Scene mainScene;

    // DAOs
    private ArquivoUsuario arqU;
    private ArquivoTarefa arqT;
    private ArquivoStatusTarefa arqS;
    private ArquivoStatusTarefa arqStatus; // Alias para arqS
    private ArquivoCategoria arqC;
    private ArquivoApontamentoDeHoras arqA;
    private ArquivoTarefaCategoriaHash arqTC;

    // Controllers
    private UsuarioController usuarioController;
    private TarefaController tarefaController;
    private StatusController statusController;
    private CategoriaController categoriaController;
    private ApontamentoController apontamentoController;

    // Views
    private UsuarioViewNew usuarioView;
    private TarefaViewNew tarefaView;
    private StatusViewNew statusView;
    private CategoriaViewNew categoriaView;
    private ApontamentoViewNew apontamentoView;
    private CompressionView compressionView;

    private Label userLabel;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        primaryStage.setTitle("Sistema de Gerenciamento de Tarefas");

        // Inicializar DAOs
        initializeDAOs();

        // Criar tela de login
        LoginView loginView = new LoginView(primaryStage, arqU, this);
        loginScene = loginView.createLoginScene();

        // Mostrar tela de login
        primaryStage.setScene(loginScene);
        primaryStage.setWidth(1000);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    public void reinitializeDAOs() throws Exception {
        initializeDAOs();
        // Refresh login view with new DAOs if currently on login screen
        if (primaryStage.getScene() == loginScene) {
             LoginView loginView = new LoginView(primaryStage, arqU, this);
             loginScene = loginView.createLoginScene();
             primaryStage.setScene(loginScene);
        }
    }
    
    // LoginView reloading
    public ArquivoUsuario getArquivoUsuario() {
        return arqU;
    }

    private void initializeDAOs() throws Exception {
        arqU = new ArquivoUsuario();
        arqT = new ArquivoTarefa();
        arqS = new ArquivoStatusTarefa();
        arqStatus = arqS; // Alias para uso em createHomePage
        arqC = new ArquivoCategoria();
        arqA = new ArquivoApontamentoDeHoras();
        arqTC = new ArquivoTarefaCategoriaHash();

        // Inicializar dados padrão
        inicializarStatusPadrao(arqS);
        inicializarCategoriasPadrao(arqC);

        // Criar views
        usuarioView = new UsuarioViewNew(this);
        tarefaView = new TarefaViewNew(this, arqU, arqS, arqC);
        statusView = new StatusViewNew(this);
        categoriaView = new CategoriaViewNew(this);
        apontamentoView = new ApontamentoViewNew(this, arqU, arqT);
        compressionView = new CompressionView(this);

        // Criar controllers
        usuarioController = new UsuarioController(arqU, usuarioView);
        tarefaController = new TarefaController(arqT, arqU, arqS, arqC, arqTC, tarefaView);
        statusController = new StatusController(arqS, statusView);
        categoriaController = new CategoriaController(arqC, categoriaView);
        apontamentoController = new ApontamentoController(arqA, arqU, arqT, apontamentoView);
    }

    public void showMainScreen() {
        mainLayout = new BorderPane();
        mainLayout.setTop(createMenuBar());

        // Criar página inicial
        VBox home = createHomePage();
        mainLayout.setCenter(home);

        mainScene = new Scene(mainLayout, 1200, 700);
        primaryStage.setScene(mainScene);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #667eea; -fx-padding: 5 10;");

        // CSS global para menus - cores brancas nos textos
        String menuBarCss =
            ".menu-bar { -fx-background-color: #667eea; }" +
            ".menu-bar .label { -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; }" +
            ".menu-bar .menu { -fx-background-color: transparent; }" +
            ".menu-bar .menu:hover { -fx-background-color: rgba(255, 255, 255, 0.1); }" +
            ".menu-bar .menu:showing { -fx-background-color: rgba(255, 255, 255, 0.15); }" +
            ".menu-item { -fx-background-color: white; }" +
            ".menu-item:hover { -fx-background-color: #667eea; }" +
            ".menu-item:hover .label { -fx-text-fill: white; }";
        menuBar.setStyle("-fx-background-color: #667eea; -fx-padding: 5 10; " + menuBarCss);

        // Informação do usuário
        Label welcomeLabel = new Label("Bem-vindo, " + SessionManager.getInstance().getNomeUsuarioLogado());
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 15 5 15; -fx-font-size: 13px;");

        userLabel = new Label();
        updateUserLabel();

        Button logoutBtn = new Button("Sair");
        logoutBtn.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.2); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-font-size: 12px; " +
            "-fx-border-color: white; " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 5;"
        );
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.3); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-font-size: 12px; " +
            "-fx-border-color: white; " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 5;"
        ));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.2); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-font-size: 12px; " +
            "-fx-border-color: white; " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 5;"
        ));
        logoutBtn.setOnAction(e -> {
            SessionManager.getInstance().logout();
            primaryStage.setScene(loginScene);
        });

        HBox userBox = new HBox(10, welcomeLabel, logoutBtn);
        userBox.setAlignment(Pos.CENTER_RIGHT);
        userBox.setPadding(new Insets(5, 15, 5, 15));

        // Menu Início
        Menu menuHome = new Menu("🏠 Início");
        MenuItem home = new MenuItem("Página Inicial");
        home.setOnAction(e -> mainLayout.setCenter(createHomePage()));
        menuHome.getItems().add(home);

        // Menu Tarefas
        Menu menuTarefas = new Menu("📋 Tarefas");
        MenuItem gerenciarTarefas = new MenuItem("Gerenciar Tarefas");
        gerenciarTarefas.setOnAction(e -> showTarefaView("gerenciar"));
        menuTarefas.getItems().add(gerenciarTarefas);

        // Menu Categorias
        Menu menuCategorias = new Menu("🏷️ Categorias");
        MenuItem gerenciarCategorias = new MenuItem("Gerenciar Categorias");
        gerenciarCategorias.setOnAction(e -> showCategoriaView("gerenciar"));
        menuCategorias.getItems().add(gerenciarCategorias);

        // Menu Status
        Menu menuStatus = new Menu("📊 Status");
        MenuItem gerenciarStatus = new MenuItem("Gerenciar Status");
        gerenciarStatus.setOnAction(e -> showStatusView("gerenciar"));
        menuStatus.getItems().add(gerenciarStatus);

        // Menu Usuários
        Menu menuUsuarios = new Menu("👥 Usuários");
        MenuItem listarUsuarios = new MenuItem("Listar Usuários");
        listarUsuarios.setOnAction(e -> showUsuarioView("listar"));
        menuUsuarios.getItems().add(listarUsuarios);

        // Menu Apontamentos
        Menu menuApontamentos = new Menu("⏱️ Apontamentos");
        MenuItem gerenciarApontamentos = new MenuItem("Gerenciar Apontamentos");
        gerenciarApontamentos.setOnAction(e -> showApontamentoView("gerenciar"));
        menuApontamentos.getItems().add(gerenciarApontamentos);

        // Menu Ferramentas
        Menu menuFerramentas = new Menu("🛠️ Ferramentas");
        MenuItem compressao = new MenuItem("Compressão de Arquivos");
        compressao.setOnAction(e -> showCompressionView("interactive"));
        menuFerramentas.getItems().add(compressao);

        menuBar.getMenus().addAll(menuHome, menuTarefas, menuCategorias,
            menuStatus, menuUsuarios, menuApontamentos, menuFerramentas);

        // Adicionar userBox à direita
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox menuContainer = new HBox(menuBar, spacer, userBox);
        menuContainer.setStyle("-fx-background-color: #667eea;");

        BorderPane topContainer = new BorderPane();
        topContainer.setCenter(menuContainer);

        return menuBar;
    }

    private VBox createHomePage() {
        VBox home = new VBox(30);
        home.setPadding(new Insets(30));
        home.setStyle("-fx-background-color: #f5f7fa;");

        Label title = new Label("Bem-vindo, " + SessionManager.getInstance().getNomeUsuarioLogado() + "!");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

        Label subtitle = new Label("Dashboard - Suas Tarefas");
        subtitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #666;");

        VBox header = new VBox(5, title, subtitle);

        // Stats Cards
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);
        statsGrid.setPadding(new Insets(20, 0, 20, 0));

        try {
            int idUsuario = SessionManager.getInstance().getIdUsuarioLogado();
            int numTarefas = arqT.listarPorUsuario(idUsuario).size();
            int numApontamentos = arqA.listarPorUsuario(idUsuario).size();

            VBox tarefasCard = createStatCard("Minhas Tarefas", String.valueOf(numTarefas), "#667eea");
            VBox apontamentosCard = createStatCard("Meus Apontamentos", String.valueOf(numApontamentos), "#764ba2");

            statsGrid.add(tarefasCard, 0, 0);
            statsGrid.add(apontamentosCard, 1, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tarefas em Andamento
        Label tarefasTitle = new Label("📋 Suas Tarefas Em Andamento:");
        tarefasTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        FlowPane cardsContainer = new FlowPane();
        cardsContainer.setHgap(20);
        cardsContainer.setVgap(20);
        cardsContainer.setPrefWrapLength(1100);

        try {
            int idUsuario = SessionManager.getInstance().getIdUsuarioLogado();
            java.util.List<Tarefa> todasTarefas = arqT.listarPorUsuario(idUsuario);

            // Filtrar apenas tarefas em andamento
            java.util.List<Tarefa> tarefasEmAndamento = new java.util.ArrayList<>();
            for (Tarefa tarefa : todasTarefas) {
                try {
                    StatusTarefa status = arqStatus.read(tarefa.getIdStatus());
                    if (status != null && status.getNome().equalsIgnoreCase("Em andamento")) {
                        tarefasEmAndamento.add(tarefa);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (tarefasEmAndamento.isEmpty()) {
                Label emptyLabel = new Label("Você não possui tarefas em andamento no momento.");
                emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #999; -fx-padding: 40;");
                cardsContainer.getChildren().add(emptyLabel);
            } else {
                for (Tarefa tarefa : tarefasEmAndamento) {
                    try {
                        StatusTarefa status = arqStatus.read(tarefa.getIdStatus());
                        VBox card = createTarefaCard(tarefa, status);
                        cardsContainer.getChildren().add(card);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ScrollPane scrollPane = new ScrollPane(cardsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        home.getChildren().addAll(header, statsGrid, new Separator(), tarefasTitle, scrollPane);
        return home;
    }

    private VBox createTarefaCard(Tarefa tarefa, StatusTarefa status) {
        VBox card = new VBox(15);
        card.setPrefWidth(340);
        card.setMinHeight(180);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2); " +
            "-fx-cursor: hand;"
        );

        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 4); " +
            "-fx-cursor: hand; " +
            "-fx-scale-x: 1.02; " +
            "-fx-scale-y: 1.02;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2); " +
            "-fx-cursor: hand;"
        ));

        Label tituloLabel = new Label(tarefa.getTitulo());
        tituloLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        tituloLabel.setWrapText(true);
        tituloLabel.setMaxWidth(300);

        Label descLabel = new Label(tarefa.getDescricao());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(300);
        descLabel.setMaxHeight(60);

        Label statusLabel = new Label(status != null ? status.getNome() : "Sem status");
        String statusColor = status != null ? getStatusColor(status.getNome()) : "#999";
        statusLabel.setStyle(
            "-fx-background-color: " + statusColor + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 6 12; " +
            "-fx-background-radius: 15; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold;"
        );

        HBox statusBox = new HBox(statusLabel);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(tituloLabel, descLabel, statusBox);
        card.setOnMouseClicked(e -> showTarefaView("gerenciar"));

        return card;
    }

    private String getStatusColor(String statusNome) {
        return switch (statusNome.toLowerCase()) {
            case "pendente" -> "#FFC107";
            case "em andamento" -> "#2196F3";
            case "concluída", "concluida" -> "#4CAF50";
            case "cancelada" -> "#f44336";
            default -> "#9E9E9E";
        };
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3); " +
            "-fx-min-width: 200; " +
            "-fx-min-height: 150;"
        );

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");

        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }

    private Label createFeatureLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #555; -fx-padding: 5;");
        return label;
    }

    private void updateUserLabel() {
        if (userLabel != null) {
            userLabel.setText("Usuário: " + SessionManager.getInstance().getNomeUsuarioLogado());
        }
    }

    private void showUsuarioView(String action) {
        mainLayout.setCenter(usuarioView.createView(action, usuarioController));
    }

    private void showTarefaView(String action) {
        mainLayout.setCenter(tarefaView.createView(action, tarefaController));
    }

    private void showStatusView(String action) {
        mainLayout.setCenter(statusView.createView(action, statusController));
    }

    private void showCategoriaView(String action) {
        mainLayout.setCenter(categoriaView.createView(action, categoriaController));
    }

    private void showApontamentoView(String action) {
        mainLayout.setCenter(apontamentoView.createView(action, apontamentoController));
    }

    private void showCompressionView(String action) {
        mainLayout.setCenter(compressionView.createView(action));
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void inicializarStatusPadrao(ArquivoStatusTarefa arqS) throws Exception {
        var lista = arqS.listarTodosAtivos();
        if (lista.isEmpty()) {
            arqS.create(new models.StatusTarefa("yellow", 1, "Pendente"));
            arqS.create(new models.StatusTarefa("blue", 2, "Em andamento"));
            arqS.create(new models.StatusTarefa("green", 3, "Concluída"));
        }
    }

    private void inicializarCategoriasPadrao(ArquivoCategoria arqC) throws Exception {
        var lista = arqC.listarTodosAtivos();
        if (lista.isEmpty()) {
            arqC.create(new models.Categoria("Desenvolvimento"));
            arqC.create(new models.Categoria("Reunião"));
            arqC.create(new models.Categoria("Documentação"));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
