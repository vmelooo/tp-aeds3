package models.structures;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Huffman {

    public static HashMap<Byte, String> codifica(byte[] sequencia) {

        HashMap<Byte, Integer> freq = new HashMap<>();
        for (byte b : sequencia)
            freq.put(b, freq.getOrDefault(b, 0) + 1);

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (Byte b : freq.keySet())
            pq.add(new HuffmanNode(b, freq.get(b)));

        // Caso especial: arquivo com apenas 1 tipo de byte
        if (pq.size() == 1) {
            HuffmanNode unico = pq.poll();
            HashMap<Byte, String> codigos = new HashMap<>();
            codigos.put(unico.b, "0");
            return codigos;
        }

        while (pq.size() > 1) {
            HuffmanNode e = pq.poll();
            HuffmanNode d = pq.poll();
            HuffmanNode pai = new HuffmanNode((byte) 0, e.frequencia + d.frequencia);
            pai.esquerdo = e;
            pai.direito = d;
            pq.add(pai);
        }

        HuffmanNode raiz = pq.poll();

        HashMap<Byte, String> codigos = new HashMap<>();
        constroi(raiz, "", codigos);
        return codigos;
    }

    private static void constroi(HuffmanNode no, String codigo, HashMap<Byte, String> codigos) {
        if (no == null) return;

        if (no.isFolha()) {
            codigos.put(no.b, codigo.length() > 0 ? codigo : "0");
            return;
        }

        constroi(no.esquerdo, codigo + "0", codigos);
        constroi(no.direito, codigo + "1", codigos);
    }


    public static byte[] decodifica(String bits, HashMap<Byte, String> codigos) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StringBuilder atual = new StringBuilder();

        for (int i = 0; i < bits.length(); i++) {
            atual.append(bits.charAt(i));

            for (byte b : codigos.keySet()) {
                if (codigos.get(b).equals(atual.toString())) {
                    out.write(b);
                    atual.setLength(0);
                    break;
                }
            }
        }

        return out.toByteArray();
    }
}
