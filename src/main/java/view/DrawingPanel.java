package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import command.AddShapeCommand;
import command.CommandManager;
import command.MoveShapeCommand;
import model.SceneModel;
import model.Shape;

public class DrawingPanel extends Panel {
    private SceneModel model;
    private CommandManager commandManager;
    private Shape selectedShape;
    private List<Shape> selectedShapes;
    private Point dragStart;
    private boolean isDragging;
    
    public DrawingPanel(SceneModel model, CommandManager commandManager) {
        this.model = model;
        this.commandManager = commandManager;
        this.selectedShapes = new ArrayList<>();
        this.isDragging = false;
        
        setBackground(Color.WHITE);
        setupEventHandlers();
    }
    
    private void setupEventHandlers() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
                selectedShape = findShapeAt(e.getX(), e.getY());
                
                if (!e.isControlDown()) {
                    selectedShapes.clear();
                }
                
                if (selectedShape != null && !selectedShapes.contains(selectedShape)) {
                    selectedShapes.add(selectedShape);
                }
                
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isDragging && selectedShape != null) {
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;
                    
                    commandManager.executeCommand(
                        new MoveShapeCommand(model, selectedShape, dx, dy)
                    );
                    isDragging = false;
                }
                repaint();
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedShape != null) {
                    isDragging = true;
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;
                    
                    selectedShape.move(dx, dy);
                    dragStart = e.getPoint();
                    repaint();
                }
            }
        });
    }
    
    private Shape findShapeAt(int x, int y) {
        List<Shape> shapes = model.getShapes();
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape shape = shapes.get(i);
            if (Math.abs(shape.getX() - x) < 50 && Math.abs(shape.getY() - y) < 50) {
                return shape;
            }
        }
        return null;
    }
    
    public void addShape(Shape shape, int x, int y) {
        Shape newShape = shape.copy();
        newShape.move(x - newShape.getX(), y - newShape.getY());
        
        commandManager.executeCommand(
            new AddShapeCommand(model, newShape)
        );
        
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        

        for (Shape shape : model.getShapes()) {
            shape.draw(g);
        }
        

        if (!selectedShapes.isEmpty()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, 
                BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
            
            for (Shape shape : selectedShapes) {

                int x = shape.getX() - 5;
                int y = shape.getY() - 5;
                g2d.drawRect(x, y, 60, 60); 
            }
            
            g2d.dispose();
        }
    }
}