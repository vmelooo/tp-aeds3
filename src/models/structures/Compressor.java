package models.structures;

import java.io.*;
import java.util.*;

public class Compressor {


    public static void compactarArquivos(List<File> arquivos, File arquivoSaida) throws Exception {

        ByteArrayOutputStream allBytes = new ByteArrayOutputStream();

        for (File f : arquivos) {
            byte[] conteudo = java.nio.file.Files.readAllBytes(f.toPath());

            allBytes.write(("#FILE:" + f.getName() + "#SIZE:" + conteudo.length + "#\n").getBytes("UTF-8"));
            allBytes.write(conteudo);
            allBytes.write("\n#END#\n".getBytes("UTF-8"));
        }

        byte[] dadosBrutos = allBytes.toByteArray();

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

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivoSaida));
        oos.writeObject(codigos);
        oos.writeObject(vb.toByteArray());
        oos.close();

        // apagar arquivos originais
        for (File f : arquivos) if (f.exists()) f.delete();
    }



    public static void descompactarArquivos(File arquivoCompactado, File ignorado) throws Exception {

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivoCompactado));
        HashMap<Byte, String> codigos = (HashMap<Byte, String>) ois.readObject();
        byte[] vetorBytes = (byte[]) ois.readObject();
        ois.close();

        byte[] dados = Huffman.decodifica(new VetorDeBits(vetorBytes).toString(), codigos);

        ByteArrayInputStream bais = new ByteArrayInputStream(dados);

        File pastaSaida = arquivoCompactado.getParentFile();

        while (true) {

            // Ler header "#FILE:xxx#SIZE:yyy#\n"
            StringBuilder sb = new StringBuilder();
            int ch;

            while ((ch = bais.read()) != -1) {
                if (ch == '\n') break;
                sb.append((char) ch);
            }

            String linha = sb.toString();
            if (!linha.startsWith("#FILE:")) break; // acabou

            String nome = linha.substring(6, linha.indexOf("#SIZE:"));
            int tamanho = Integer.parseInt(
                linha.substring(
                    linha.indexOf("#SIZE:") + 6,
                    linha.lastIndexOf("#")
                )
            );

            // Ler exatamente os bytes do arquivo original
            byte[] conteudo = bais.readNBytes(tamanho);

            // Salvar arquivo restaurado
            FileOutputStream fos = new FileOutputStream(new File(pastaSaida, nome));
            fos.write(conteudo);
            fos.close();

            // pular '\n#END#\n'
            bais.readNBytes(7);
        }

        // Apaga o arquivo compactado
        arquivoCompactado.delete();
    }
}
