package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import model.Group;
import model.Hexagon;
import model.RectangleModel;
import model.SceneModel;
import model.Shape;

public class ToolbarPanel extends Panel {
    private SceneModel model;
    private List<ToolbarItem> toolbarItems;
    private ToolbarItem selectedItem; // Item actuellement sélectionné
    private ToolbarItem trashItem;

    private static final int ITEM_HEIGHT = 60;
    private static final int ITEM_PADDING = 10;

    private static final int TOOLBAR_WIDTH = 100;

    public ToolbarPanel(SceneModel model) {
        this.toolbarItems = new ArrayList<>();
        this.model = model;
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(100, 300));

        // Ajouter les formes par défaut
        addDefaultItems();

        // Configurer les gestionnaires d'événements
        setupEventHandlers();
    }

    private void addDefaultItems() {

        Shape rectangle = new RectangleModel(0, 0);
        Shape hexagon = new Hexagon(0, 0);
        Map<Shape, Point> shapesWithCoordinates = new HashMap<>();
        shapesWithCoordinates.put(new RectangleModel(0, 0), new Point(50, 25));
        shapesWithCoordinates.put(new RectangleModel(0, 0), new Point(50, 125));
        shapesWithCoordinates.put(new RectangleModel(0, 0), new Point(250, 25));
        shapesWithCoordinates.put(new Hexagon(0, 0), new Point(50, 50));
        Shape group = new Group(shapesWithCoordinates, 300, 150);
        
        // ajout au model des formes par défaut
        model.addToolbarShape(rectangle);
        model.addToolbarShape(hexagon);
        model.addToolbarShape(group);

        // Ajouter les formes à la toolbar
        for (Shape shape : model.getToolbarShapes()) {
            addTemplate(shape, createShapeIcon(shape));
        }
        
        // Ajouter la corbeille
        trashItem = new ToolbarItem(null, createTrashIcon());
        // La corbeille sera dessinée séparément en bas de la toolbar
    }

    private void setupEventHandlers() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ToolbarItem item = findItemAt(e.getX(), e.getY());
                if (item != null) {
                    if (item == selectedItem) {
                        // Désélectionner si déjà sélectionné
                        selectedItem = null;
                        System.out.println("Désélectionné");
                    } else {
                        // Sélectionner un nouvel item
                        selectedItem = item;
                        System.out.println("Sélectionné : " + (item.getShape() != null ? item.getShape() : "Trash"));
                    }
                }
                repaint();
            }
        });
    }

    private ToolbarItem findItemAt(int x, int y) {
        // Vérifier d'abord la corbeille (en bas)
        if (trashItem != null) {
            int trashY = getHeight() - ITEM_HEIGHT - ITEM_PADDING;
            if (y >= trashY && y < trashY + ITEM_HEIGHT &&
                x >= ITEM_PADDING && x <= TOOLBAR_WIDTH - ITEM_PADDING) {
                return trashItem;
            }
        }

        // Vérifier les templates
        int currentY = ITEM_PADDING;
        for (ToolbarItem item : toolbarItems) {
            if (y >= currentY && y < currentY + ITEM_HEIGHT &&
                x >= ITEM_PADDING && x <= TOOLBAR_WIDTH - ITEM_PADDING) {
                return item;
            }
            currentY += ITEM_HEIGHT + ITEM_PADDING;
        }
        return null;
    }

    public Shape getSelectedShape() {
        return selectedItem != null && selectedItem != trashItem ? selectedItem.getShape() : null;
    }

    public boolean isTrashSelected() {
        return selectedItem == trashItem;
    }

    public void clearSelectedItem() {
        selectedItem = null;
    }

    public void addTemplate(Shape shape, Image icon) {
        toolbarItems.add(new ToolbarItem(shape.copy(), icon));
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Dessiner les templates
        int y = ITEM_PADDING;
        for (ToolbarItem item : toolbarItems) {
            drawItem(g, item, y, item == selectedItem);
            y += ITEM_HEIGHT + ITEM_PADDING;
        }

        // Dessiner la corbeille en bas
        if (trashItem != null) {
            drawItem(g, trashItem, getHeight() - ITEM_HEIGHT - ITEM_PADDING, trashItem == selectedItem);
        }
    }

    private void drawItem(Graphics g, ToolbarItem item, int y, boolean isSelected) {
        int centerX = TOOLBAR_WIDTH / 2;

        // Dessiner le cadre de l'élément
        g.setColor(isSelected ? Color.ORANGE : Color.DARK_GRAY);
        g.drawRect(ITEM_PADDING, y, TOOLBAR_WIDTH - 2 * ITEM_PADDING, ITEM_HEIGHT);

        if (item.getIcon() != null) {
            // Dessiner l'icône centrée
            g.drawImage(item.getIcon(), centerX - 16, y + (ITEM_HEIGHT - 32) / 2, null);
        } else if (item.getShape() != null) {
            // Dessiner la forme centrée
            Graphics g2d = (Graphics) g.create();
            g2d.translate(centerX - item.getShape().getX(),
                         y + ITEM_HEIGHT / 2 - item.getShape().getY());
            item.getShape().draw((Graphics2D) g2d);
            g2d.dispose();
        }
    }

    private Image createShapeIcon(Shape shape) {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Effacer l'arrière-plan
        g2d.setColor(new Color(0, 0, 0, 0)); // Transparent
        g2d.fillRect(0, 0, 32, 32);

        // Définir la couleur de la forme
        g2d.setColor(shape.getColor());

        // Calculer le facteur d'échelle pour ajuster la forme à l'icône
        double scaleX = 32.0 / shape.getWidth(); // Réduire légèrement pour éviter les coupures
        double scaleY = 32.0 / shape.getHeight();
        double scale = Math.min(scaleX, scaleY);

        // Centrer la forme dans l'icône
        int centerX = 16; // Centre de l'icône (32x32)
        int centerY = 16;
        g2d.translate(centerX, centerY);
        g2d.scale(scale, scale);

        // Dessiner la forme centrée
        shape.draw(g2d);

        g2d.dispose();
        return image;
    }

    private Image createTrashIcon() {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.RED);

        // Dessiner une poubelle simple
        g.drawRect(8, 8, 16, 20);
        g.drawLine(12, 8, 12, 4);
        g.drawLine(20, 8, 20, 4);
        g.drawLine(12, 4, 20, 4);
        g.drawLine(10, 12, 10, 24);
        g.drawLine(16, 12, 16, 24);
        g.drawLine(22, 12, 22, 24);

        g.dispose();
        return image;
    }

}