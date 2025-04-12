package view;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import command.CommandManager;
import model.SceneModel;

public class MainFrame extends Frame {
    private SceneModel model;
    private CommandManager commandManager;
    private MenubarPanel menubarPanel;
    private ToolbarPanel toolbarPanel;
    private DrawingPanel drawingPanel;

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
        toolbarPanel = new ToolbarPanel();
        drawingPanel = new DrawingPanel(model, commandManager, toolbarPanel);
        
  
        menubarPanel.setDrawingPanel(drawingPanel);

        add(menubarPanel, BorderLayout.NORTH);
        add(toolbarPanel, BorderLayout.WEST);
        add(drawingPanel, BorderLayout.CENTER);

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
            System.out.println("Sauvegarde du document vers: " + path);
            // Implémenter la sauvegarde du document
        }
    }

    private void loadDocument() {
        FileDialog dialog = new FileDialog(this, "Charger un document", FileDialog.LOAD);
        dialog.setVisible(true);

        String filename = dialog.getFile();
        if (filename != null) {
            String path = dialog.getDirectory() + filename;
            System.out.println("Chargement du document depuis: " + path);
            // Implémenter le chargement du document
            drawingPanel.repaint();
        }
    }




}