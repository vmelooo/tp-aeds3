package models.structures;

import java.util.Arrays;

public class VetorDeBits {
    private byte[] vetor;

    // --- CONSTRUTORES ---

    public VetorDeBits() {
        vetor = new byte[1];
    }

    public VetorDeBits(byte[] dados) {
        vetor = Arrays.copyOf(dados, dados.length);
    }
    
    /**
     * NOVO: Construtor para pré-alocar o vetor de bytes com base no número total de bits.
     * Necessário para a Codificação LZW.
     */
    public VetorDeBits(int totalBits) {
        // Calcula o tamanho necessário em bytes, arredondando para cima
        // (ex: 9 bits precisam de 2 bytes, (9 + 7) / 8 = 2)
        vetor = new byte[(totalBits + 7) / 8];
    }

    // --- MÉTODOS AUXILIARES ---
    
    private void garanteTamanho(int bitIndex) {
        int byteIndex = bitIndex / 8;
        if (byteIndex >= vetor.length) {
            // Se precisar de um novo tamanho, copia o array e adiciona mais um byte
            vetor = Arrays.copyOf(vetor, byteIndex + 1);
        }
    }

    // --- MÉTODOS DE MANIPULAÇÃO DE BITS ---
    
    public void set(int bitIndex) {
        garanteTamanho(bitIndex);
        // Usa a operação OR para definir o bit para 1
        vetor[bitIndex / 8] |= (1 << (7 - (bitIndex % 8)));
    }

    public void clear(int bitIndex) {
        garanteTamanho(bitIndex);
        // Usa a operação AND com o complemento do bit para definir para 0
        vetor[bitIndex / 8] &= ~(1 << (7 - (bitIndex % 8)));
    }
    
    /**
     * NOVO: Retorna o valor de um bit específico.
     * Necessário para a Decodificação LZW.
     */
    public boolean get(int bitIndex) {
        int byteIndex = bitIndex / 8;
        int bitPos = 7 - (bitIndex % 8); // Posição do bit dentro do byte (da esquerda para a direita)

        if (byteIndex >= vetor.length) {
            // Se o índice estiver fora do vetor de bytes, retorna falso
            return false;
        }

        // Verifica se o bit está setado usando AND bitwise
        return (vetor[byteIndex] & (1 << bitPos)) != 0;
    }

    /**
     * NOVO: Retorna o número total de bits que este vetor de bytes pode armazenar.
     * Necessário para a Decodificação LZW.
     */
    public int length() {
        return vetor.length * 8;
    }

    // --- MÉTODOS DE SAÍDA ---
    
    public byte[] toByteArray() {
        return vetor;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (byte b : vetor) {
            for (int i = 7; i >= 0; i--) {
                sb.append(((b >> i) & 1) == 1 ? '1' : '0');
            }
        }

        return sb.toString();
    }
}