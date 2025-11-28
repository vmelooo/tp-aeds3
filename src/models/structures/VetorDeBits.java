package models.structures;

import java.util.Arrays;

public class VetorDeBits {
    private byte[] vetor;

    public VetorDeBits() {
        vetor = new byte[1];
    }

    public VetorDeBits(byte[] dados) {
        vetor = Arrays.copyOf(dados, dados.length);
    }

    private void garanteTamanho(int bitIndex) {
        int byteIndex = bitIndex / 8;
        if (byteIndex >= vetor.length) {
            vetor = Arrays.copyOf(vetor, byteIndex + 1);
        }
    }

    public void set(int bitIndex) {
        garanteTamanho(bitIndex);
        vetor[bitIndex / 8] |= (1 << (7 - (bitIndex % 8)));
    }

    public void clear(int bitIndex) {
        garanteTamanho(bitIndex);
        vetor[bitIndex / 8] &= ~(1 << (7 - (bitIndex % 8)));
    }

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
