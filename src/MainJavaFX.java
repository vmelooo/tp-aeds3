import javafx.application.Application;
import views.gui.MainView;

/**
 * Ponto de entrada para a aplicação JavaFX
 * Esta classe inicializa a interface gráfica do sistema
 */
public class MainJavaFX {

    public static void main(String[] args) {
        // Lança a aplicação JavaFX
        Application.launch(MainView.class, args);
    }
}
