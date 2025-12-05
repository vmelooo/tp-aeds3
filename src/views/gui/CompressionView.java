package views.gui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import models.structures.Compressor;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;

public class CompressionView {
    private MainView mainView;
    private DecimalFormat df = new DecimalFormat("#.##");

    public CompressionView(MainView mainView) {
        this.mainView = mainView;
    }

    public Node createView(String action) {
        VBox mainLayout = new VBox(25);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setStyle("-fx-background-color: #f5f7fa;");

        // Título principal com estilo
        Label title = new Label("🗜️ Centro de Compressão Avançada");
        title.setStyle(
            "-fx-font-size: 36px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #667eea;"
        );
        title.setAlignment(Pos.CENTER);

        Label subtitle = new Label("Algoritmos de Compressão: Huffman & LZW");
        subtitle.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-text-fill: #666; " +
            "-fx-font-weight: normal;"
        );
        subtitle.setAlignment(Pos.CENTER);

        VBox header = new VBox(10, title, subtitle);
        header.setAlignment(Pos.CENTER);

        // Container principal
        HBox algorithmsBox = new HBox(25);
        algorithmsBox.setAlignment(Pos.CENTER);

        // Card Huffman
        VBox huffmanCard = createAlgorithmCard(
            "Huffman",
            "Codificação de Prefixo",
            "Usa árvore binária com frequência de bytes.\nMelhor para textos e dados repetitivos.",
            "#FF6B6B"
        );

        // Card LZW
        VBox lzwCard = createAlgorithmCard(
            "LZW",
            "Lempel-Ziv-Welch",
            "Dicionário dinâmico com 12 bits por índice.\nÓtimo para imagens e dados estruturados.",
            "#4ECDC4"
        );

        algorithmsBox.getChildren().addAll(huffmanCard, lzwCard);

        mainLayout.getChildren().addAll(header, algorithmsBox);

        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f7fa; -fx-background-color: #f5f7fa;");
        return scrollPane;
    }

    private VBox createAlgorithmCard(String nome, String subtitulo, String descricao, String cor) {
        VBox card = new VBox(20);
        card.setPrefWidth(450);
        card.setMinHeight(600);
        card.setPadding(new Insets(30));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 5);"
        );

        // Cabeçalho do card
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);

        Label nomeLabel = new Label(nome);
        nomeLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + cor + ";");

        Label subLabel = new Label(subtitulo);
        subLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-font-style: italic;");

        // Barra de cor
        Rectangle colorBar = new Rectangle(400, 4);
        colorBar.setFill(Color.web(cor));
        colorBar.setArcWidth(4);
        colorBar.setArcHeight(4);

        headerBox.getChildren().addAll(nomeLabel, subLabel, colorBar);

        // Descrição
        Label descLabel = new Label(descricao);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(400);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-text-fill: #555; " +
            "-fx-line-spacing: 3px; " +
            "-fx-padding: 10 0;"
        );

        Separator sep = new Separator();

        // Botões de ação
        VBox botoesBox = new VBox(15);
        botoesBox.setAlignment(Pos.CENTER);

        Button compressBtn = createGradientButton("Compactar", cor, "#FF4757");
        compressBtn.setOnAction(e -> showCompressionInterface(nome, true));

        Button decompressBtn = createGradientButton("Descompactar", "#5f27cd", "#341f97");
        decompressBtn.setOnAction(e -> showCompressionInterface(nome, false));

        botoesBox.getChildren().addAll(compressBtn, decompressBtn);

        card.getChildren().addAll(headerBox, descLabel, sep, botoesBox);

        // Animação de hover
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), card);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), card);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        card.setOnMouseEntered(e -> scaleUp.playFromStart());
        card.setOnMouseExited(e -> scaleDown.playFromStart());

        return card;
    }

    private Button createGradientButton(String text, String cor1, String cor2) {
        Button btn = new Button(text);
        btn.setPrefWidth(300);
        btn.setPrefHeight(50);
        btn.setStyle(
            "-fx-background-color: " + cor1 + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );

        btn.setOnMouseEntered(e -> btn.setOpacity(0.8));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));

        return btn;
    }

    private void showCompressionInterface(String algoritmo, boolean isCompress) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(isCompress ? "Compactação" : "Descompactação");
        dialog.setHeaderText((isCompress ? "Compactar" : "Descompactar") + " com " + algoritmo);

        BorderPane mainPane = new BorderPane();
        mainPane.setPrefWidth(900);
        mainPane.setPrefHeight(600);
        mainPane.setStyle("-fx-background-color: #f5f7fa;");

        VBox centerBox = new VBox(20);
        centerBox.setPadding(new Insets(30));
        centerBox.setAlignment(Pos.TOP_CENTER);

        // Informações do processo
        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPadding(new Insets(20));
        infoBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label statusLabel = new Label("Aguardando início...");
        statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #667eea;");

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(700);
        progressBar.setPrefHeight(30);
        progressBar.setStyle("-fx-accent: #667eea;");

        Label detailsLabel = new Label("");
        detailsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        detailsLabel.setWrapText(true);
        detailsLabel.setMaxWidth(700);

        infoBox.getChildren().addAll(statusLabel, progressBar, detailsLabel);

        // Estatísticas
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(15);
        statsGrid.setAlignment(Pos.CENTER);
        statsGrid.setPadding(new Insets(20));
        statsGrid.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        VBox[] statCards = new VBox[4];
        String[] statTitles = {"Tamanho Original", "Tamanho Compactado", "Taxa de Compressão", "Tempo Decorrido"};
        String[] statColors = {"#FF6B6B", "#4ECDC4", "#45B7D1", "#FFA07A"};

        for (int i = 0; i < 4; i++) {
            statCards[i] = createStatCard(statTitles[i], "-", statColors[i]);
            statsGrid.add(statCards[i], i % 2, i / 2);
        }

        // Gráfico de barras comparativo (será preenchido após a compressão)
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Tamanho (KB)");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Comparação de Tamanhos");
        chart.setPrefHeight(250);
        chart.setLegendVisible(false);
        chart.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Button startBtn = createGradientButton(
            isCompress ? "Iniciar Compactação" : "Iniciar Descompactação",
            "#667eea", "#764ba2"
        );

        startBtn.setOnAction(e -> {
            startBtn.setDisable(true);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            statusLabel.setText("Processando...");
            detailsLabel.setText("Analisando arquivos...");

            new Thread(() -> {
                try {
                    File pasta = new File("data");
                    long startTime = System.currentTimeMillis();
                   
                    if (isCompress) {

                        File[] arqs = pasta.listFiles((d, n) -> n.endsWith(".db") || n.endsWith(".idx"));

                        if (arqs == null || arqs.length == 0) {
                            javafx.application.Platform.runLater(() -> {
                                statusLabel.setText("❌ Nenhum arquivo encontrado!");
                                statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #f44336;");
                                progressBar.setVisible(false);
                                startBtn.setDisable(false);
                            });
                            return;
                        }

                        long tamanhoOriginal = 0;
                        for (File f : arqs) {
                            tamanhoOriginal += f.length();
                        }

                        final long tamanhoOriginalFinal = tamanhoOriginal;

                        javafx.application.Platform.runLater(() -> {
                            detailsLabel.setText(String.format(
                                "Encontrados %d arquivos | Tamanho total: %s",
                                arqs.length, formatBytes(tamanhoOriginalFinal)
                            ));
                            updateStatCard(statCards[0], formatBytes(tamanhoOriginalFinal));
                        });

                        Thread.sleep(500); // Pausa dramática
                        
                        // COMPRESSÃO
                        File saida = new File("data/backup." + (algoritmo.equals("Huffman") ? "huff" : "lzw"));

                        javafx.application.Platform.runLater(() -> {
                            statusLabel.setText("🗜️ Compactando com " + algoritmo + "...");
                            detailsLabel.setText("Aplicando algoritmo de compressão...");
                        });

                        if (algoritmo.equals("Huffman")) {
                            Compressor.compactarArquivos(Arrays.asList(arqs), saida);
                        } else {
                            Compressor.compactarArquivosLZW(Arrays.asList(arqs), saida);
                        }

                        long tamanhoCompactado = saida.length();
                        long endTime = System.currentTimeMillis();
                        double taxaCompressao = 100.0 * (1.0 - ((double) tamanhoCompactado / tamanhoOriginal));
                        long tempoDecorrido = endTime - startTime;

                        // Variáveis finais para uso na lambda
                        final long tamanhoOriginalFinal2 = tamanhoOriginal;
                        final long tamanhoCompactadoFinal = tamanhoCompactado;
                        final double taxaCompressaoFinal = taxaCompressao;
                        final long tempoDecorridoFinal = tempoDecorrido;

                        // Atualizar UI com resultados
                        javafx.application.Platform.runLater(() -> {
                            progressBar.setProgress(1.0);
                            statusLabel.setText("✅ Compactação Concluída com Sucesso!");
                            statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

                            updateStatCard(statCards[1], formatBytes(tamanhoCompactadoFinal));
                            updateStatCard(statCards[2], df.format(taxaCompressaoFinal) + "%");
                            updateStatCard(statCards[3], tempoDecorridoFinal + " ms");

                            detailsLabel.setText(String.format(
                                "Arquivo compactado: %s | Economia de espaço: %s (%s%%)",
                                saida.getName(),
                                formatBytes(tamanhoOriginalFinal2 - tamanhoCompactadoFinal),
                                df.format(taxaCompressaoFinal)
                            ));

                            // Atualizar gráfico
                            XYChart.Series<String, Number> series = new XYChart.Series<>();
                            series.getData().add(new XYChart.Data<>("Original", tamanhoOriginalFinal2 / 1024.0));
                            series.getData().add(new XYChart.Data<>("Compactado", tamanhoCompactadoFinal / 1024.0));
                            chart.getData().clear();
                            chart.getData().add(series);

                            startBtn.setDisable(false);
                            startBtn.setText("Compactar Novamente");
                        });

                    } else {
                        // DESCOMPRESSÃO
                        String filename = "backup." + (algoritmo.equals("Huffman") ? "huff" : "lzw");
                        File arquivoCompactado = new File("data/" + filename);

                        if (!arquivoCompactado.exists()) {
                            javafx.application.Platform.runLater(() -> {
                                statusLabel.setText("❌ Arquivo compactado não encontrado!");
                                statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #f44336;");
                                detailsLabel.setText("Por favor, execute a compactação primeiro.");
                                progressBar.setVisible(false);
                                startBtn.setDisable(false);
                            });
                            return;
                        }

                        long tamanhoCompactado = arquivoCompactado.length();

                        javafx.application.Platform.runLater(() -> {
                            statusLabel.setText("📦 Descompactando com " + algoritmo + "...");
                            detailsLabel.setText("Reconstruindo arquivos originais...");
                            updateStatCard(statCards[1], formatBytes(tamanhoCompactado));
                        });

                        if (algoritmo.equals("Huffman")) {
                            Compressor.descompactarArquivos(arquivoCompactado, new File("data"));
                        } else {
                            Compressor.descompactarArquivosLZW(arquivoCompactado, new File("data"));
                        }

                        long endTime = System.currentTimeMillis();
                        long tempoDecorrido = endTime - startTime;

                        // Calcular novo tamanho total
                        File[] arqsNovos = pasta.listFiles((d, n) -> n.endsWith(".db") || n.endsWith(".idx"));
                        long tamanhoNovo = 0;
                        if (arqsNovos != null) {
                            for (File f : arqsNovos) {
                                tamanhoNovo += f.length();
                            }
                        }

                        final long tamanhoNovoFinal = tamanhoNovo;

                        javafx.application.Platform.runLater(() -> {
                            progressBar.setProgress(1.0);
                            statusLabel.setText("✅ Descompactação Concluída com Sucesso!");
                            statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

                            updateStatCard(statCards[0], formatBytes(tamanhoNovoFinal));
                            updateStatCard(statCards[3], tempoDecorrido + " ms");

                            detailsLabel.setText(String.format(
                                "Arquivos restaurados na pasta 'data' | Total de arquivos: %d",
                                arqsNovos != null ? arqsNovos.length : 0
                            ));

                            // Atualizar gráfico
                            XYChart.Series<String, Number> series = new XYChart.Series<>();
                            series.getData().add(new XYChart.Data<>("Compactado", tamanhoCompactado / 1024.0));
                            series.getData().add(new XYChart.Data<>("Restaurado", tamanhoNovoFinal / 1024.0));
                            chart.getData().clear();
                            chart.getData().add(series);

                            startBtn.setDisable(false);
                            startBtn.setText("Descompactar Novamente");
                        });
                    }

                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        statusLabel.setText("❌ Erro durante o processo!");
                        statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #f44336;");
                        detailsLabel.setText("Erro: " + ex.getMessage());
                        progressBar.setVisible(false);
                        startBtn.setDisable(false);

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erro");
                        alert.setHeaderText("Erro durante o processo");
                        alert.setContentText(ex.getMessage());
                        alert.showAndWait();
                    });
                    ex.printStackTrace();
                }
            }).start();
        });

        centerBox.getChildren().addAll(infoBox, statsGrid, chart, startBtn);

        mainPane.setCenter(centerBox);

        dialog.getDialogPane().setContent(mainPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    private VBox createStatCard(String titulo, String valor, String cor) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
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
        tituloLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-weight: bold;");
        tituloLabel.setWrapText(true);
        tituloLabel.setMaxWidth(180);
        tituloLabel.setAlignment(Pos.CENTER);

        Label valorLabel = new Label(valor);
        valorLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + cor + ";");

        card.getChildren().addAll(tituloLabel, valorLabel);

        return card;
    }

    private void updateStatCard(VBox card, String novoValor) {
        Label valorLabel = (Label) card.getChildren().get(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), valorLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), valorLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeOut.setOnFinished(e -> {
            valorLabel.setText(novoValor);
            fadeIn.play();
        });

        fadeOut.play();
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return df.format(bytes / 1024.0) + " KB";
        return df.format(bytes / (1024.0 * 1024.0)) + " MB";
    }
}
