package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class MenubarPanel extends Panel {
    private List<MenuItem> menuItems;
    private Runnable saveAction;
    private Runnable loadAction;
    private Runnable undoAction;
    private Runnable redoAction;
    private DrawingPanel drawingPanel; // Add reference to the drawing panel

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
        setPreferredSize(new Dimension(400, 60)); // Largeur ajustée pour les menus horizontaux

        addDefaultItems();
        setupEventHandlers();
    }
    
    // Add method to set the drawing panel reference
    public void setDrawingPanel(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    private void addDefaultItems() {
        // Now we'll wrap the undo/redo actions to include a repaint
        Runnable wrappedUndoAction = () -> {
            undoAction.run();
            if (drawingPanel != null) {
                drawingPanel.repaint();
            }
        };
        
        Runnable wrappedRedoAction = () -> {
            redoAction.run();
            if (drawingPanel != null) {
                drawingPanel.repaint();
            }
        };

        menuItems.add(new MenuItem("Save", loadAndResizeIcon("/icons/save.png", 32), saveAction));
        menuItems.add(new MenuItem("Load", loadAndResizeIcon("/icons/load.png", 32), loadAction));
        menuItems.add(new MenuItem("Undo", loadAndResizeIcon("/icons/undo.png", 32), wrappedUndoAction));
        menuItems.add(new MenuItem("Redo", loadAndResizeIcon("/icons/redo.png", 32), wrappedRedoAction));
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

    // Updated method to handle SVG and other image files
    private Image loadIcon(String path) {
        try {
            // Try multiple ways to load the resource
            InputStream inputStream = getClass().getResourceAsStream(path);
            
            if (inputStream == null) {
                // Try with class loader directly
                inputStream = getClass().getClassLoader().getResourceAsStream(path.startsWith("/") ? path.substring(1) : path);
            }
            
            if (inputStream != null) {
                BufferedImage img = ImageIO.read(inputStream);
                if (img != null) {
                    System.out.println("Successfully loaded icon: " + path);
                    return img;
                } else {
                    System.out.println("Failed to decode image from: " + path);
                }
            } else {
                System.out.println("Resource not found: " + path);
            }
            
            // Fallback - create a basic icon if the image isn't found
            return createFallbackIcon(path);
        } catch (IOException e) {
            System.out.println("Error loading icon " + path + ": " + e.getMessage());
            e.printStackTrace();
            return createFallbackIcon(path);
        }
    }
    
    
    private Image loadAndResizeIcon(String path, int targetSize) {
        Image originalIcon = loadIcon(path);
        
        BufferedImage resizedIcon = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
        Graphics g = resizedIcon.createGraphics();
        
        g.drawImage(originalIcon, 0, 0, targetSize, targetSize, null);
        g.dispose();
        
        return resizedIcon;
    }
    
    private Image createFallbackIcon(String iconPath) {
        System.out.println("Creating fallback icon for: " + iconPath);
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.GRAY);
        g.fillRect(4, 4, 24, 24);
        g.setColor(Color.WHITE);
        
        // Utiliser la première lettre du nom du fichier comme identifiant
        String letter = iconPath.substring(iconPath.lastIndexOf('/') + 1, iconPath.lastIndexOf('/') + 2).toUpperCase();
        g.drawString(letter, 12, 20);
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
