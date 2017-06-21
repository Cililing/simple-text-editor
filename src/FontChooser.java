import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;


/**
 * Klasa odpowiada za wybieranie czcionki.
 */
public class FontChooser {

    // Ustawienia domyślne czcionki
    // static - obiekt należy do klasy nie do instancji (tzn. tworzony jest tylko raz, niezależnie od ilości instancji klasy.
    // final - pole będzie stałe w czasie
    private static final Font DEFAUL_FONT = new Font("Times new Roman", Font.PLAIN, 12);
    private static final int DEFAULT_SIZE = 12;


    // Singleton (tworzymy maksymalnie jeden obiekt tej klasy)
    private static FontChooser instance = null;

    // Zmienne programu
    private FontChooserDialog dialog;
    private Font[] fonts;
    private Font currentFont;
    private float currentSize = DEFAULT_SIZE;
    OnSetFontListener listener;

    // prywatny konstruktor
    private FontChooser() {

    }

    /**
     * Funckcja zwracająca (lub tworząca) instancję klasy
     * Jeżeli instancja istieje, zmieniany jest listener
     *
     * @param listener listener nasluchujący zmianę czcionki w programie
     * @return instancja klasy
     */
    public static FontChooser getInstance(OnSetFontListener listener) {
        if (instance == null) {
            instance = new FontChooser();
            instance.listener = listener;
            instance.getSystemFonts();
            instance.currentFont = DEFAUL_FONT;
            instance.dialog = instance.new FontChooserDialog(instance.fonts, instance);
            instance.dialog.setVisible(false);
        } else {
            instance.listener = listener;
        }

        return instance;
    }

    /**
     * Pokazuje okno z wyborem czcionki.
     */
    public void showDialogForAnswer() {
        dialog.setVisible(true);

    }

    /**
     * Laduje czcionki systemowe do pamieci
     */
    private void getSystemFonts() {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fonts = e.getAllFonts();
    }


    /**
     * Klasa z oknem wyboru czcionki
     */
    class FontChooserDialog extends JFrame {

        // Zmienne
        FontChooser parent; // Klasa wywolujaca obiekt

        // Zmienne komponentow GUI
        // Wszystkie komponenty wykorzystywane w GUI
        // Metody wywolywane na tych obiektach nalezy szukac:
        // https://docs.oracle.com/javase/7/docs/api/java/awt/Component.html
        // Oficjalna dokumentacja Javy
        JList<Font> fonts;
        JScrollPane fontsPanel = new JScrollPane();
        JPanel buttons = new JPanel();
        JLabel sizeLabel = new JLabel("Rozmiar:");
        JFormattedTextField sizeInput; //init at runtime
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Anuluj");

        // Ustawiony font
        Font newFont = FontChooser.DEFAUL_FONT;
        float newSize = FontChooser.DEFAULT_SIZE;

        // Konstruktor
        public FontChooserDialog(Font[] fonts, FontChooser parent) {
            super();
            setDefaultCloseOperation(HIDE_ON_CLOSE);
            this.fonts = new JList<>(fonts);
            this.parent = parent;

            init();
        }


        private void init() {

            setTitle("Wybierz czcionkę");
            fontsPanel.setViewportView(fonts);


            // Listener -> dodaje reakcje obiektu na dane zdarzenie
            fonts.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    newFont = fonts.getSelectedValue();
                }
            });


            add(fontsPanel, BorderLayout.CENTER);


            //format size....
            NumberFormat format = NumberFormat.getInstance();
            NumberFormatter formatter = new NumberFormatter(format);
            formatter.setValueClass(Integer.class);
            formatter.setMinimum(8);
            formatter.setMaximum(72);
            formatter.setAllowsInvalid(true);
            formatter.setCommitsOnValidEdit(true);
            sizeInput = new JFormattedTextField(formatter);
            sizeInput.setColumns(2);

            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        newSize = Float.parseFloat(sizeInput.getText());
                    } catch (NumberFormatException ignored) {
                        newSize = DEFAULT_SIZE;
                    }

                    newFont = newFont.deriveFont(newSize);

                    parent.currentFont = newFont;
                    parent.currentSize = newSize;

                    listener.onOkClick(currentFont);
                    setVisible(false);
                }
            });


            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });

            buttons.add(sizeLabel);
            buttons.add(sizeInput);
            buttons.add(okButton);
            buttons.add(cancelButton);

            add(buttons, BorderLayout.SOUTH);

            pack();
        }


    }
}
