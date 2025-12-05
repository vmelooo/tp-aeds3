package views.gui;

import controllers.RSAController;
import dao.ArquivoUsuario;
import dao.PatternMatching;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.File;
import models.Usuario;
import models.structures.Compressor;

import utils.SessionManager;

import java.util.List;

public class LoginView {

    private Stage primaryStage;
    private ArquivoUsuario arquivoUsuario;
    private RSAController rsaController;
    private MainView mainView;

    public LoginView(Stage primaryStage, ArquivoUsuario arquivoUsuario, MainView mainView) {
        this.primaryStage = primaryStage;
        this.arquivoUsuario = arquivoUsuario;
        this.mainView = mainView;
        this.rsaController = new RSAController();
        this.rsaController.initialize();
    }

    public Scene createLoginScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #667eea;");

        // Card de login
        VBox loginCard = new VBox(20);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(40));
        loginCard.setMaxWidth(400);
        loginCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);"
        );

        // Título
        Label titleLabel = new Label("Sistema de Gerenciamento de Tarefas");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#667eea"));
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);

        Label subtitleLabel = new Label("Faça login para continuar");
        subtitleLabel.setFont(Font.font("System", 14));
        subtitleLabel.setTextFill(Color.GRAY);

        // Campos de login
        TextField loginField = new TextField();
        loginField.setPromptText("Login (Email)");
        loginField.setStyle(
            "-fx-padding: 12; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 8; " +
            "-fx-font-size: 14;"
        );

        PasswordField senhaField = new PasswordField();
        senhaField.setPromptText("Senha");
        senhaField.setStyle(
            "-fx-padding: 12; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 8; " +
            "-fx-font-size: 14;"
        );

        // Botões
        Button loginButton = new Button("Entrar");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        loginButton.setOnMouseEntered(e ->
            loginButton.setStyle(
                "-fx-background-color: #5568d3; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            )
        );
        loginButton.setOnMouseExited(e ->
            loginButton.setStyle(
                "-fx-background-color: #667eea; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            )
        );

        Button registerButton = new Button("Criar Conta");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #667eea; " +
            "-fx-font-size: 14; " +
            "-fx-padding: 12; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #667eea; " +
            "-fx-border-radius: 8; " +
            "-fx-cursor: hand;"
        );
        registerButton.setOnMouseEntered(e ->
            registerButton.setStyle(
                "-fx-background-color: #f0f0f0; " +
                "-fx-text-fill: #667eea; " +
                "-fx-font-size: 14; " +
                "-fx-padding: 12; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: #667eea; " +
                "-fx-border-radius: 8; " +
                "-fx-cursor: hand;"
            )
        );
        registerButton.setOnMouseExited(e ->
            registerButton.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: #667eea; " +
                "-fx-font-size: 14; " +
                "-fx-padding: 12; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: #667eea; " +
                "-fx-border-radius: 8; " +
                "-fx-cursor: hand;"
            )
        );

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);

        // Ação do botão de login
        loginButton.setOnAction(e -> handleLogin(loginField.getText(), senhaField.getText(), messageLabel));
        senhaField.setOnAction(e -> handleLogin(loginField.getText(), senhaField.getText(), messageLabel));

        // Ação do botão de registro
        registerButton.setOnAction(e -> showRegisterDialog());

        loginCard.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            new Separator(),
            loginField,
            senhaField,
            loginButton,
            registerButton,
            messageLabel
        );

        
        File huffFile = findBackupFile(".huff");
        File lzwFile = findBackupFile(".lzw");
        if (huffFile != null) {
            Button descompactarButton = new Button("Descompactar Huff");
            descompactarButton.setMaxWidth(Double.MAX_VALUE);
            descompactarButton.setStyle(
                "-fx-background-color: #ab4642; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            );
            descompactarButton.setOnAction(e ->decompressHuff(huffFile));
            loginCard.getChildren().add(descompactarButton);
        } else if (lzwFile != null) {
            Button descompactarButton = new Button("Descompactar LZW");
            descompactarButton.setMaxWidth(Double.MAX_VALUE);
            descompactarButton.setStyle(
                "-fx-background-color: #ab4642; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            );
            descompactarButton.setOnAction(e -> decompressLZW(lzwFile));
            loginCard.getChildren().add(descompactarButton);
        }

        root.setCenter(loginCard);

        return new Scene(root, 1000, 600);
    }

    private File findBackupFile(String extension) {
        File dataDir = new File("data");
        if (dataDir.exists() && dataDir.isDirectory()) {
            File[] files = dataDir.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(extension)
            );
            // Coalesce to first match if array is not empty
            if (files != null && files.length > 0) {
                return files[0];
            }
        }
        return null;
    }

    private void decompressHuff(File arquivoCompactado) {
        try {
            Compressor.descompactarArquivos(arquivoCompactado, new File("data"));
        
            // Refresh UI/DAOs after decompression
            mainView.reinitializeDAOs();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Huffman backup restored!");
        
            // Reload Scene
            LoginView newLogin = new LoginView(primaryStage, mainView.getArquivoUsuario(), mainView);
            primaryStage.setScene(newLogin.createLoginScene());
        
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Huffman decompression failed.");
        }
    }

    private void decompressLZW(File arquivoCompactado) {
        try {
            Compressor.descompactarArquivosLZW(arquivoCompactado, new File("data"));
        
            // Refresh UI/DAOs after decompression
            mainView.reinitializeDAOs();
            showAlert(Alert.AlertType.INFORMATION, "Success", "LZW backup restored!");
        
            // Reload Scene
            LoginView newLogin = new LoginView(primaryStage, mainView.getArquivoUsuario(), mainView);
            primaryStage.setScene(newLogin.createLoginScene());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "LZW decompression failed.");
        }
    }    

    // OLD, unused.
    private void showDecompressionDialog() {
        CompressionView cv = new CompressionView(mainView);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Recuperar Backup");
        dialog.setHeaderText("Arquivos compactados detectados. Deseja descompactar?");
        
        ScrollPane pane = (ScrollPane) cv.createView("interactive");
        pane.setPrefSize(600, 400);
        
        dialog.getDialogPane().setContent(pane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        dialog.showAndWait();

        try {
            mainView.reinitializeDAOs();
            showAlert(Alert.AlertType.INFORMATION, "Sistema Atualizado", "Sistema de arquivos recarregado.");
            primaryStage.setScene(new LoginView(primaryStage, mainView.getArquivoUsuario(), mainView).createLoginScene());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao recarregar dados: " + e.getMessage());
        }
    }

    private void handleLogin(String login, String senha, Label messageLabel) {
        if (login.isEmpty() || senha.isEmpty()) {
            messageLabel.setText("Por favor, preencha todos os campos");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        try {
            // Buscar usuários pelo nome de usuário (usando busca por padrão)
            List<Usuario> usuarios = arquivoUsuario.listarAtivos();
            Usuario usuarioEncontrado = null;

            for (Usuario u : usuarios) {
                if (u.getLogin().equalsIgnoreCase(login)) {
                    usuarioEncontrado = u;
                    break;
                }
            }

            if (usuarioEncontrado == null) {
                messageLabel.setText("Usuário não encontrado");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            // Verificar senha (descriptografar e comparar)
            String senhaCriptografada = usuarioEncontrado.getSenha();
            String senhaDescriptografada = rsaController.decryptPassword(senhaCriptografada);

            if (senha.equals(senhaDescriptografada)) {
                // Login bem-sucedido
                SessionManager.getInstance().login(usuarioEncontrado);
                mainView.showMainScreen();
            } else {
                messageLabel.setText("Senha incorreta");
                messageLabel.setTextFill(Color.RED);
            }

        } catch (Exception ex) {
            messageLabel.setText("Erro ao fazer login: " + ex.getMessage());
            messageLabel.setTextFill(Color.RED);
            ex.printStackTrace();
        }
    }

    private void showRegisterDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Criar Nova Conta");
        dialog.setHeaderText("Preencha os dados para criar sua conta");

        // Campos do formulário
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome Completo");
        TextField loginField = new TextField();
        loginField.setPromptText("Login (Email)");
        PasswordField senhaField = new PasswordField();
        senhaField.setPromptText("Senha");
        PasswordField confirmSenhaField = new PasswordField();
        confirmSenhaField.setPromptText("Confirmar Senha");

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Login:"), 0, 1);
        grid.add(loginField, 1, 1);
        grid.add(new Label("Senha:"), 0, 2);
        grid.add(senhaField, 1, 2);
        grid.add(new Label("Confirmar:"), 0, 3);
        grid.add(confirmSenhaField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String nome = nomeField.getText();
                String login = loginField.getText();
                String senha = senhaField.getText();
                String confirmSenha = confirmSenhaField.getText();

                if (nome.isEmpty() || login.isEmpty() || senha.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Erro", "Todos os campos são obrigatórios!");
                    return;
                }

                if (!senha.equals(confirmSenha)) {
                    showAlert(Alert.AlertType.ERROR, "Erro", "As senhas não coincidem!");
                    return;
                }

                try {
                    // Criptografar senha
                    String senhaCriptografada = rsaController.encryptPassword(senha);

                    // Criar usuário
                    Usuario novoUsuario = new Usuario(nome, login, senhaCriptografada);
                    int id = arquivoUsuario.create(novoUsuario);

                    showAlert(Alert.AlertType.INFORMATION, "Sucesso",
                        "Conta criada com sucesso!\nID: " + id + "\nVocê já pode fazer login.");

                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Erro",
                        "Erro ao criar conta: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
