package models.structures;

import java.io.*;
import java.util.*;

public class Compressor {

    // --- MÉTODOS HUFFMAN (EXISTENTES) ---

    public static void compactarArquivos(List<File> arquivos, File arquivoSaida) throws Exception {

        ByteArrayOutputStream allBytes = new ByteArrayOutputStream();

        for (File f : arquivos) {
            byte[] conteudo = java.nio.file.Files.readAllBytes(f.toPath());

            // Header: #FILE:nome#SIZE:tamanho#\n
            allBytes.write(("#FILE:" + f.getName() + "#SIZE:" + conteudo.length + "#\n").getBytes("UTF-8"));
            allBytes.write(conteudo);
            allBytes.write("\n#END#\n".getBytes("UTF-8"));
        }

        byte[] dadosBrutos = allBytes.toByteArray();

        // 1. Codificação Huffman
        HashMap<Byte, String> codigos = Huffman.codifica(dadosBrutos);

        VetorDeBits vb = new VetorDeBits();
        int pos = 0;

        for (byte b : dadosBrutos) {
            String codigo = codigos.get(b);
            if (codigo == null)
                throw new Exception("ERRO: byte não encontrado na árvore de Huffman → " + b);

            for (char c : codigo.toCharArray()) {
                if (c == '1') vb.set(pos++);
                else vb.clear(pos++);
            }
        }

        arquivoSaida.getParentFile().mkdirs();

        // 2. Gravação do Mapa de Códigos e dos Bytes Compactados
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivoSaida));
        oos.writeObject(codigos);
        oos.writeObject(vb.toByteArray());
        oos.close();

        // 3. Apagar arquivos originais
        // for (File f : arquivos) if (f.exists()) f.delete();
    }

    static void deleteFiles(File path) throws IOException {
        if (path == null || !path.exists()) {
            throw new IOException("Directory does not exist: " + path);
        }

        try {
            File[] arqs = path.listFiles((d, n) -> 
                n.endsWith(".db") || n.endsWith(".hash") || n.endsWith(".idx"));

            if (arqs == null) {
                throw new IOException("Failed to list files. Path might not be a directory or IO error occurred.");
            }

            for (File f : arqs) {
                boolean success = f.delete();
                if (!success) {
                    throw new IOException("Failed to delete: " + f.getName());
                }
            }
        } catch (SecurityException e) {
            throw new IOException("Permission denied while accessing files.", e);
        }
    }

    public static void descompactarArquivos(File arquivoCompactado, File pastaSaida) throws Exception {

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivoCompactado));
        HashMap<Byte, String> codigos = (HashMap<Byte, String>) ois.readObject();
        byte[] vetorBytes = (byte[]) ois.readObject();
        ois.close();

        // 1. Decodificação Huffman
        // Nota: Assumindo que VetorDeBits.toString() está ok para alimentar Huffman.decodifica
        byte[] dados = Huffman.decodifica(new VetorDeBits(vetorBytes).toString(), codigos);

        deleteFiles(pastaSaida);
        reconstruirArquivos(dados, arquivoCompactado, pastaSaida);

        // Apaga o arquivo compactado
        arquivoCompactado.delete();
    }

    // --- MÉTODOS LZW (NOVOS) ---

    public static void compactarArquivosLZW(List<File> arquivos, File arquivoSaida) throws Exception {

        ByteArrayOutputStream allBytes = new ByteArrayOutputStream();

        // 1. Agrupa o conteúdo de todos os arquivos com seus headers
        for (File f : arquivos) {
            byte[] conteudo = java.nio.file.Files.readAllBytes(f.toPath());
            
            // Header: #FILE:nome#SIZE:tamanho#\n
            allBytes.write(("#FILE:" + f.getName() + "#SIZE:" + conteudo.length + "#\n").getBytes("UTF-8"));
            allBytes.write(conteudo);
            allBytes.write("\n#END#\n".getBytes("UTF-8"));
        }

        byte[] dadosBrutos = allBytes.toByteArray();

        // 2. Codificação LZW
        byte[] dadosCodificados = LZW.codifica(dadosBrutos); // Vetor de bytes de índices LZW

        arquivoSaida.getParentFile().mkdirs();

        // 3. Grava o resultado no arquivo de saída
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivoSaida));
        oos.writeObject(dadosCodificados);
        oos.close();

        // 4. Apagar arquivos originais
        // for (File f : arquivos) if (f.exists()) f.delete();
    }

    public static void descompactarArquivosLZW(File arquivoCompactado, File pastaSaida) throws Exception {

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivoCompactado));
        byte[] dadosCodificados = (byte[]) ois.readObject();
        ois.close();

        // 1. Decodificação LZW
        byte[] dados = LZW.decodifica(dadosCodificados);

        deleteFiles(pastaSaida);
        reconstruirArquivos(dados, arquivoCompactado, pastaSaida);

        // Apaga o arquivo compactado
        arquivoCompactado.delete();
    }


    // --- MÉTODO AUXILIAR PARA RECONSTRUÇÃO (USADO POR HUFFMAN E LZW) ---

    private static void reconstruirArquivos(byte[] dadosDescompactados, File arquivoCompactado, File pastaSaida) throws IOException, NumberFormatException {
        ByteArrayInputStream bais = new ByteArrayInputStream(dadosDescompactados);

        // File pastaSaida = arquivoCompactado.getParentFile(); // Já recebido como parâmetro, mas útil se fosse diferente

        while (true) {

            // Ler header "#FILE:xxx#SIZE:yyy#\n"
            StringBuilder sb = new StringBuilder();
            int ch;

            // Lê até encontrar uma quebra de linha ou o fim do stream
            while ((ch = bais.read()) != -1) {
                if (ch == '\n') break;
                sb.append((char) ch);
            }

            String linha = sb.toString();
            if (!linha.startsWith("#FILE:")) break; // acabou ou encontrou o fim

            // Extrai nome
            String nome = linha.substring(6, linha.indexOf("#SIZE:"));
            
            // Extrai tamanho
            int tamanho = Integer.parseInt(
                linha.substring(
                    linha.indexOf("#SIZE:") + 6,
                    linha.lastIndexOf("#")
                )
            );

            // 2. Ler exatamente os bytes do arquivo original
            byte[] conteudo = bais.readNBytes(tamanho);

            // 3. Salvar arquivo restaurado
            FileOutputStream fos = new FileOutputStream(new File(pastaSaida, nome));
            fos.write(conteudo);
            fos.close();

            // 4. Pular "\n#END#\n" (7 caracteres)
            if(bais.available() >= 7) {
                 bais.readNBytes(7);
            } else {
                 break; // Sai se não houver mais dados suficientes para o footer
            }
        }
    }
}
