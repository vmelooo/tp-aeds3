package models.structures;

import java.io.IOException;

/**
 * Interface para registros que podem ser armazenados em Hash Extensível
 * Similar a RegistroArvoreBMais, mas para estrutura de hash
 */
public interface RegistroHash<T> {

    /**
     * Retorna a chave de hash como inteiro
     * Para chaves compostas, deve combinar os valores em um único int
     */
    int hashCode();

    /**
     * Serializa o registro para array de bytes
     */
    byte[] toByteArray() throws IOException;

    /**
     * Deserializa o registro de array de bytes
     */
    void fromByteArray(byte[] ba) throws IOException;

    /**
     * Retorna o tamanho em bytes do registro
     */
    int size();

    /**
     * Compara este registro com outro para verificar igualdade
     */
    boolean equals(Object obj);
}
