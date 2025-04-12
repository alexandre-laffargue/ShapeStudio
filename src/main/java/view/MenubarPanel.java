package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MenubarPanel extends Panel {
    private List<MenuItem> menuItems;
    private Runnable saveAction;
    private Runnable loadAction;
    private Runnable undoAction;
    private Runnable redoAction;

    private static final int ITEM_WIDTH = 60;
    private static final int ITEM_HEIGHT = 40;
    private static final int ITEM_PADDING = 10;

    public MenubarPanel(Runnable saveAction, Runnable loadAction, Runnable undoAction, Runnable redoAction) {
        this.menuItems = new ArrayList<>();
        this.saveAction = saveAction;
        this.loadAction = loadAction;
        this.undoAction = undoAction;
        this.redoAction = redoAction;

        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(400, 50)); // Largeur ajustée pour les menus horizontaux

        addDefaultItems();
        setupEventHandlers();
    }

    private void addDefaultItems() {
        // Ajouter les éléments du menu avec des icônes
        menuItems.add(new MenuItem("Save", createSaveIcon(), saveAction));
        menuItems.add(new MenuItem("Load", createLoadIcon(), loadAction));
        menuItems.add(new MenuItem("Undo", createUndoIcon(), undoAction));
        menuItems.add(new MenuItem("Redo", createRedoIcon(), redoAction));
    }

    private void setupEventHandlers() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                MenuItem item = findItemAt(e.getX(), e.getY());
                if (item != null && item.getAction() != null) {
                    item.getAction().run();
                }
            }
        });
    }

    private MenuItem findItemAt(int x, int y) {
        int currentX = ITEM_PADDING;
        for (MenuItem item : menuItems) {
            if (x >= currentX && x < currentX + ITEM_WIDTH &&
                y >= ITEM_PADDING && y < ITEM_PADDING + ITEM_HEIGHT) {
                return item;
            }
            currentX += ITEM_WIDTH + ITEM_PADDING;
        }
        return null;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int x = ITEM_PADDING;
        for (MenuItem item : menuItems) {
            drawItem(g, item, x, ITEM_PADDING);
            x += ITEM_WIDTH + ITEM_PADDING;
        }
    }

    private void drawItem(Graphics g, MenuItem item, int x, int y) {
        // Dessiner le cadre de l'élément
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, ITEM_WIDTH, ITEM_HEIGHT);

        if (item.getIcon() != null) {
            // Dessiner l'icône centrée
            g.drawImage(item.getIcon(), x + (ITEM_WIDTH - 32) / 2, y + (ITEM_HEIGHT - 32) / 2, null);
        }
    }

    // Méthodes pour créer des icônes simples avec BufferedImage
    private Image createSaveIcon() {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(4, 4, 24, 24);
        g.setColor(Color.WHITE);
        g.drawString("S", 12, 20);
        g.dispose();
        return image;
    }

    private Image createLoadIcon() {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.GREEN);
        g.fillRect(4, 4, 24, 24);
        g.setColor(Color.WHITE);
        g.drawString("L", 12, 20);
        g.dispose();
        return image;
    }

    private Image createUndoIcon() {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.ORANGE);
        g.fillRect(4, 4, 24, 24);
        g.setColor(Color.WHITE);
        g.drawString("U", 12, 20);
        g.dispose();
        return image;
    }

    private Image createRedoIcon() {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.RED);
        g.fillRect(4, 4, 24, 24);
        g.setColor(Color.WHITE);
        g.drawString("R", 12, 20);
        g.dispose();
        return image;
    }

    // Classe interne pour représenter un élément du menu
    private static class MenuItem {
        private String name;
        private Image icon;
        private Runnable action;

        public MenuItem(String name, Image icon, Runnable action) {
            this.name = name;
            this.icon = icon;
            this.action = action;
        }

        public Image getIcon() {
            return icon;
        }

        public Runnable getAction() {
            return action;
        }
    }
}
