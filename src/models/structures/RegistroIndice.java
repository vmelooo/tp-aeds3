package models.structures;

import java.io.*;

public class RegistroIndice implements RegistroArvoreBMais<RegistroIndice> {

    private int id;
    private long ponteiro;

    public RegistroIndice() {
        this(-1, -1);
    }

    public RegistroIndice(int id, long ponteiro) {
        this.id = id;
        this.ponteiro = ponteiro;
    }

    public int getId() {
        return this.id;
    }

    public long getPonteiro() {
        return this.ponteiro;
    }

    @Override
    public int compareTo(RegistroIndice other) {
        return Integer.compare(this.id, other.id);
    }

    // --- Implementation of RegistroArvoreBMais ---

    @Override
    public short size() {
        // The record size is fixed: an int (4 bytes) + a long (8 bytes)
        return 12;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeLong(ponteiro);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.ponteiro = dis.readLong();
    }
    
    @Override
    public RegistroIndice clone() {
        return new RegistroIndice(this.id, this.ponteiro);
    }

    @Override
    public String toString() {
        return "Indice(ID: " + id + ", Ptr: " + ponteiro + ")";
    }
}
