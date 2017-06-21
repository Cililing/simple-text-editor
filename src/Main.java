import javax.swing.*;
import java.awt.*;

public class Main {


    // Wywoluje okno edytora w nowym wątku
    public static void main(String... args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                TextEditor main = new TextEditor();
                main.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                main.setVisible(true);
            }
        });
    }

}
