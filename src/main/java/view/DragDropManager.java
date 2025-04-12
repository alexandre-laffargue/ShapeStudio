package view;

import java.awt.Component;
import java.awt.Point;

import model.Shape;

public class DragDropManager {
    private Component source;
    private Shape draggedShape;
    private Point dragOffset;
    private ToolbarPanel toolbarPanel;
    private DrawingPanel drawingPanel;
    
    public DragDropManager(ToolbarPanel toolbarPanel, DrawingPanel drawingPanel) {
        this.toolbarPanel = toolbarPanel;
        this.drawingPanel = drawingPanel;
        
        // Connecter les panels avec ce gestionnaire
        toolbarPanel.setDragDropManager(this);
    }
    
    public void startDrag(Component source, Shape shape, Point point) {
        this.source = source;
        this.draggedShape = shape;
        this.dragOffset = point;
        System.out.println("Début du drag de "+ shape.toString() + " depuis " + source.getClass().getSimpleName());
    }
    
    public void drag(Point point) {
        // Cette méthode pourrait afficher une preview de la forme pendant le drag
        // Si vous implémentez une fonctionnalité de drag visuel
    }
    
    public void drop(Component target, Point point) {
        if (draggedShape == null) return;
        
        System.out.println("Drop sur " + target.getClass().getSimpleName());
        
        if (source == toolbarPanel && target == drawingPanel) {
            // Créer une nouvelle forme de la toolbar vers le dessin
            System.out.println("Ajouter une forme au dessin");
            drawingPanel.addShape(draggedShape, point.x, point.y);
        } else if (source == drawingPanel && target == toolbarPanel) {
            System.out.println("Ajouter une forme à la toolbardsqdsqdsqdsqdsqd");
            // Ajouter une forme du dessin vers la toolbar
            toolbarPanel.addTemplate(draggedShape);
        }
        drawingPanel.addShape(draggedShape, point.x, point.y);

        
        draggedShape = null;
        source = null;
    }
}