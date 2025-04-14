package view;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import command.CommandManager;
import model.SceneModel;

public class MainFrame extends Frame {
    private SceneModel model;
    private CommandManager commandManager;
    private MenubarPanel menubarPanel;
    private MainCanvas canvas;

    public MainFrame(String title) {
        super(title);

        this.model = new SceneModel();
        this.commandManager = new CommandManager();

        setupUI();
        setupWindowListener();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        menubarPanel = new MenubarPanel(
            this::saveDocument,
            this::loadDocument,
            commandManager::undo,
            commandManager::redo
        );

        canvas = new MainCanvas(model, commandManager);
        menubarPanel.setMainCanvas(canvas);

        add(menubarPanel, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);

        setSize(1024, 768);
    }

    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private void saveDocument() {
        FileDialog dialog = new FileDialog(this, "Sauvegarder le document", FileDialog.SAVE);
        dialog.setVisible(true);

        String filename = dialog.getFile();
        if (filename != null) {
            String path = dialog.getDirectory() + filename;
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
                oos.writeObject(model); // Sérialiser le modèle
                System.out.println("Document sauvegardé avec succès : " + path);
            } catch (IOException e) {
                System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
            }
        }
    }

    private void loadDocument() {
        FileDialog dialog = new FileDialog(this, "Charger un document", FileDialog.LOAD);
        dialog.setVisible(true);

        String filename = dialog.getFile();
        if (filename != null) {
            String path = dialog.getDirectory() + filename;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
                SceneModel loadedModel = (SceneModel) ois.readObject(); // Désérialiser le modèle
                model = loadedModel; // Remplacer le modèle actuel
                canvas.setModel(model); // Mettre à jour le canvas avec le nouveau modèle
                canvas.repaint();
                System.out.println("Document chargé avec succès : " + path);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Erreur lors du chargement : " + e.getMessage());
            }
        }
    }
}