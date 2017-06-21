import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class TextEditor extends JFrame {


    // Zmienne statyczne, finalne (domyslne ustawienia programu)
    private static final int ROWS = 30;
    private static final int COLUMNS = 120;
    private static final int DEFAULT_WIDTH = 1500;
    private static final int DEFAULT_HEIGHT = 800;


    // Zmienne GUI
    private JTextArea jArea = new JTextArea(ROWS, COLUMNS);
    private JScrollPane jScrollPane = new JScrollPane(
            jArea,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    private JMenuBar jMenuBar = new JMenuBar();

    // Zmienne GUI - Menu
    private JMenu jFile = new JMenu("Plik");
    private JMenuItem jNewItem = new JMenuItem("Nowy");
    private JMenuItem jOpenItem = new JMenuItem("Otwórz");
    private JMenuItem jSaveItem = new JMenuItem("Zapisz");
    private JMenuItem jSaveAsItem = new JMenuItem("Zapisz jako");
    private JMenuItem jExitItem = new JMenuItem("Wyjdź");
    private JMenu jEdit = new JMenu("Edycja");
    private JMenuItem jFontItem = new JMenuItem("Zmień Czcionke");

    // Domyslny FileChooser
    private JFileChooser fileChooserDialog = new JFileChooser(System.getProperty("user.dir"));


    // Zmienne programu (nazwa pliku, flaga zmiany, obiekt odpowiadajacy za zmiane czcionki)
    private String currentFile = "Nowy";
    private boolean hasChanged = false;
    FontChooser fontChooser;

    // Obiekt listenera który odpowiada za zmiane czcionki na ekranie
    OnSetFontListener onSetFontListener = new OnSetFontListener() {
        @Override
        public void onOkClick(Font font) {
            jArea.setFont(font);
        }
    };


    // Konstruktor
    public TextEditor() {
        super();
        init();
        initMenus();
    }


    // Wystepujace metody gui, jak: add, setTitle etc. są dziedziczone z klasy JFrame
    // Ich opisu należy szukać w dokumentacji
    private void init() {

        fontChooser = FontChooser.getInstance(onSetFontListener);

        add(jScrollPane, BorderLayout.CENTER);
        setTitle(currentFile);

        // listener odpowiadajacy za zmiane flagi "hasChanged" (reaguje na zmiany wpisanego tekstu)
        jArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                hasChanged = true;
                jSaveItem.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                hasChanged = true;
                jSaveItem.setEnabled(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                hasChanged = true;
                jSaveItem.setEnabled(true);
            }
        });

        // adapter reagujacy na przycisk zamknij (prosi uzytkownika o potwierdzenie)
        WindowAdapter closingAdapter = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (actionAfterChanges() == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        };

        addWindowListener(closingAdapter);
        setSize(getPreferredSize());
    }

    // Inituje menu
    private void initMenus() {

        setJMenuBar(jMenuBar);
        jMenuBar.add(jFile);
        initFileBar();
        jMenuBar.add(jEdit);
        initEditBar();

    }

    // Inituje podmenu - edycja
    private void initEditBar() {

        jFontItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fontChooser.showDialogForAnswer();
            }
        });
        jEdit.add(jFontItem);

    }

    // Inituje podmenu - plik
    private void initFileBar() {
        jNewItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hasChanged) {
                    if (actionAfterChanges() == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
                else {
                    jArea.setText("");
                    hasChanged = false;
                    jSaveItem.setEnabled(false);
                }
            }
        });
        jFile.add(jNewItem);

        // Listenery....
        jOpenItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hasChanged) {
                    // Spytaj usera czy chce porzucic zmiany...
                    if (actionAfterChanges() == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

                // Otworz dialog z z wybraniem pliku...
                if (fileChooserDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    openFile(fileChooserDialog.getSelectedFile().getAbsolutePath());
                }
            }
        });
        jFile.add(jOpenItem);



        jFile.add(jSaveItem);
        jSaveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile(currentFile);
            }
        });
        jSaveItem.setEnabled(false);


        jSaveAsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileChooserDialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    saveFile(fileChooserDialog.getSelectedFile().getAbsolutePath());
                }
            }
        });
        jFile.add(jSaveAsItem);


        jExitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hasChanged) {
                    if (actionAfterChanges() == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
                System.exit(0);
            }
        });
        jFile.add(jExitItem);
    }

    //SAVE AMD OPEN FILE
    private void openFile(String path) {

        if (hasChanged) {
            if (actionAfterChanges() == JOptionPane.NO_OPTION) {
                return;
            }
        }

        File file = new File(path);
        try {
            FileReader reader = new FileReader(file);
            jArea.read(reader, null);
            currentFile = path;
            setTitle(path);
            hasChanged = false;
            jSaveItem.setEnabled(false);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Niepowodzenie podczas odczytywania pliku");
        }
    }

    private void saveFile(String path) {
        File file = new File(path);
        try {
            FileWriter writer = new FileWriter(file);
            jArea.write(writer);
            currentFile = path;
            setTitle(path);
            hasChanged = false;
            jSaveItem.setEnabled(false);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Niepowodzenie podczas zapisywania pliku");
        }
    }

    private int actionAfterChanges() {
        return JOptionPane.showConfirmDialog(null, "Dana akcja spowoduje porzucenie zmian wprowadzonych w pliku. Czy jesteś pewny?");

    }


    // private void
    // Zwraca domyslny rozmiar okna
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
