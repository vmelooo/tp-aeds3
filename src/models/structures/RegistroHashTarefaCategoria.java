package models.structures;

import java.io.*;

/**
 * Registro para Hash Extensível de TarefaCategoria
 * Armazena a chave composta (idTarefa, idCategoria) e a posição no arquivo heap
 */
public class RegistroHashTarefaCategoria implements RegistroHash<RegistroHashTarefaCategoria> {

    private int idTarefa;
    private int idCategoria;
    private long ponteiro;  // Posição no arquivo heap onde está o registro completo

    public RegistroHashTarefaCategoria() {
        this(-1, -1, -1);
    }

    public RegistroHashTarefaCategoria(int idTarefa, int idCategoria, long ponteiro) {
        this.idTarefa = idTarefa;
        this.idCategoria = idCategoria;
        this.ponteiro = ponteiro;
    }

    public int getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(int idTarefa) {
        this.idTarefa = idTarefa;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public long getPonteiro() {
        return ponteiro;
    }

    public void setPonteiro(long ponteiro) {
        this.ponteiro = ponteiro;
    }

    @Override
    public int hashCode() {
        // Combina os dois IDs para gerar um hash único
        // Usa um número primo para evitar colisões
        return idTarefa * 31 + idCategoria;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        RegistroHashTarefaCategoria other = (RegistroHashTarefaCategoria) obj;
        return this.idTarefa == other.idTarefa && this.idCategoria == other.idCategoria;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idTarefa);
        dos.writeInt(idCategoria);
        dos.writeLong(ponteiro);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        this.idTarefa = dis.readInt();
        this.idCategoria = dis.readInt();
        this.ponteiro = dis.readLong();
    }

    @Override
    public int size() {
        // 4 bytes (int) + 4 bytes (int) + 8 bytes (long) = 16 bytes
        return 16;
    }

    @Override
    public String toString() {
        return String.format("TarefaCategoria[Tarefa: %d, Categoria: %d, Pos: %d]",
            idTarefa, idCategoria, ponteiro);
    }
}
