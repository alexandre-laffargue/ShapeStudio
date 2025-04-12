
package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import model.Rectangle;
import model.RegularPolygon;
import model.Shape;

public class ToolbarPanel extends Panel {
    private List<ToolbarItem> items;
    private Shape selectedShape;
    private DragDropManager dragDropManager;
    private ToolbarItem trashItem;
    
    private static final int ITEM_HEIGHT = 60;
    private static final int ITEM_PADDING = 10;
    
    public ToolbarPanel() {
        this.items = new ArrayList<>();
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(100, 300));
        
        // Ajouter les formes par défaut
        addDefaultItems();
        
        // Configurer les gestionnaires d'événements
        setupEventHandlers();
    }
    
    public void setDragDropManager(DragDropManager manager) {
        this.dragDropManager = manager;
    }
    
    private void addDefaultItems() {
        // Ajouter un rectangle
        addTemplate(new Rectangle(0, 0), createRectangleIcon());
        
        // Ajouter un polygone régulier
        addTemplate(new RegularPolygon(0, 0), createPolygonIcon());
        
        // Ajouter la corbeille
        trashItem = new ToolbarItem(null, createTrashIcon());
        // La corbeille sera dessinée séparément en bas de la toolbar
    }
    
    private void setupEventHandlers() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ToolbarItem item = findItemAt(e.getX(), e.getY());
                if (item != null && dragDropManager != null && item != trashItem) {
                    selectedShape = item.getShape();
                    dragDropManager.startDrag(ToolbarPanel.this, selectedShape, e.getPoint());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragDropManager != null && selectedShape != null) {
                    // Vérifier si le drop est sur la corbeille
                    ToolbarItem item = findItemAt(e.getX(), e.getY());
                    if (item == trashItem) {
                        // Supprimer l'élément (à implémenter)
                        System.out.println("Élément déposé dans la corbeille");
                    }
                    
                    // Notifier le gestionnaire de drag & drop
                    dragDropManager.drop(ToolbarPanel.this, e.getPoint());
                }
                selectedShape = null;
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragDropManager != null && selectedShape != null) {
                    dragDropManager.drag(e.getPoint());
                }
            }
        });
    }
    
    private ToolbarItem findItemAt(int x, int y) {
        // Vérifier d'abord la corbeille (en bas)
        if (trashItem != null) {
            int trashY = getHeight() - ITEM_HEIGHT - ITEM_PADDING;
            if (y >= trashY && y < trashY + ITEM_HEIGHT && 
                x >= ITEM_PADDING && x <= getWidth() - ITEM_PADDING) {
                return trashItem;
            }
        }
        
        // Vérifier les templates
        int currentY = ITEM_PADDING;
        for (ToolbarItem item : items) {
            if (y >= currentY && y < currentY + ITEM_HEIGHT &&
                x >= ITEM_PADDING && x <= getWidth() - ITEM_PADDING) {
                return item;
            }
            currentY += ITEM_HEIGHT + ITEM_PADDING;
        }
        
        return null;
    }
    
    public void addTemplate(Shape shape) {
        // Version sans icône
        items.add(new ToolbarItem(shape.copy(), null));
        repaint();
    }
    
    public void addTemplate(Shape shape, Image icon) {
        items.add(new ToolbarItem(shape.copy(), icon));
        repaint();
    }
    
    public void removeTemplate(Shape shape) {
        items.removeIf(item -> {
            if (item.getShape() == null) return false;
            return item.getShape().equals(shape);
        });
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        // Dessiner les templates
        int y = ITEM_PADDING;
        for (ToolbarItem item : items) {
            drawItem(g, item, y);
            y += ITEM_HEIGHT + ITEM_PADDING;
        }
        
        // Dessiner la corbeille en bas
        if (trashItem != null) {
            drawItem(g, trashItem, getHeight() - ITEM_HEIGHT - ITEM_PADDING);
        }
    }
    
    private void drawItem(Graphics g, ToolbarItem item, int y) {
        int centerX = getWidth() / 2;
        
        // Dessiner le cadre de l'élément
        g.setColor(Color.DARK_GRAY);
        g.drawRect(ITEM_PADDING, y, getWidth() - 2 * ITEM_PADDING, ITEM_HEIGHT);
        
        if (item.getIcon() != null) {
            // Dessiner l'icône centrée
            g.drawImage(item.getIcon(), centerX - 16, y + (ITEM_HEIGHT - 32) / 2, null);
        } else if (item.getShape() != null) {
            // Dessiner la forme centrée
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.translate(centerX - item.getShape().getX(), 
                         y + ITEM_HEIGHT / 2 - item.getShape().getY());
            item.getShape().draw(g2d);
            g2d.dispose();
        }
    }
    
    // Méthodes pour créer des icônes simples avec BufferedImage
    private Image createRectangleIcon() {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(4, 4, 24, 24);
        g.setColor(Color.BLACK);
        g.drawRect(4, 4, 24, 24);
        g.dispose();
        return image;
    }
    
    private Image createPolygonIcon() {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.GREEN);
        
        int[] xPoints = {16, 28, 24, 8, 4};
        int[] yPoints = {4, 12, 24, 24, 12};
        g.fillPolygon(xPoints, yPoints, 5);
        g.setColor(Color.BLACK);
        g.drawPolygon(xPoints, yPoints, 5);
        g.dispose();
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
    
    // Classe interne pour représenter un élément de la toolbar
    private static class ToolbarItem {
        private Shape shape;
        private Image icon;
        
        public ToolbarItem(Shape shape, Image icon) {
            this.shape = shape;
            this.icon = icon;
        }
        
        public Shape getShape() {
            return shape;
        }
        
        public Image getIcon() {
            return icon;
        }
    }
}