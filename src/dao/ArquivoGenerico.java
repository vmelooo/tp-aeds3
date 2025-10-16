package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ArquivoGenerico<T extends Serializable> {

    protected final String arquivo;

    public ArquivoGenerico(String arquivo) {
        this.arquivo = arquivo;
    }

    @SuppressWarnings("unchecked")
    protected List<T> readAll() throws IOException, ClassNotFoundException {
        File f = new File(arquivo);
        
        if (!f.exists() || f.length() == 0) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<T>) ois.readObject();
        } catch (StreamCorruptedException e) {
            // 3. SE OCORRER O ERRO StreamCorruptedException, isso significa que
            // o arquivo existe mas não é um objeto serializado válido.
            System.err.println("Aviso: Arquivo de persistência '" + arquivo + "' corrompido. Iniciando com dados vazios.");
            // Opcionalmente, renomeie o arquivo corrompido aqui.
            return new ArrayList<>();
        }
    }

    // O método salvar(List<T> lista) permanece o mesmo.
    protected void salvar(List<T> lista) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            oos.writeObject(lista);
        }
    }
}