package models;
import java.io.IOException;

public interface ArquivoBinario {
    public int getId();
    public void setId(int id);
    public byte[] toByteArray() throws IOException;
    public void fromByteArray(byte[] ba) throws IOException;
}
