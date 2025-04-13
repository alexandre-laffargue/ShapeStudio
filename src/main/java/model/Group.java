package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import visitor.ShapeVisitor;

public class Group implements Shape {
    private final Map<Shape, Point> children; // Associer chaque forme à ses coordonnées relatives
    private int width;
    private int height;

    public Group(Map<Shape, Point> shapesWithCoordinates) {
        this.children = new HashMap<>();
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
    
        for (Map.Entry<Shape, Point> entry : shapesWithCoordinates.entrySet()) {
            Shape shape = entry.getKey();
            Point relativePosition = entry.getValue();
            this.children.put(shape, relativePosition);
    
            // Calculer les limites pour déterminer les dimensions du groupe
            int shapeX = relativePosition.x;
            int shapeY = relativePosition.y;
            int shapeWidth = shape.getWidth();
            int shapeHeight = shape.getHeight();
    
            minX = Math.min(minX, shapeX);
            minY = Math.min(minY, shapeY);
            maxX = Math.max(maxX, shapeX + shapeWidth);
            maxY = Math.max(maxY, shapeY + shapeHeight);
        }
    
        // Calculer les dimensions du groupe
        this.width = maxX - minX;
        this.height = maxY - minY;
    }

    public List<Shape> getChildren() {
        return new ArrayList<>(children.keySet());
    }

    public void updateRelativePositions() {
        for (Shape shape : children.keySet()) {
            int relativeX = shape.getX() - this.getX();
            int relativeY = shape.getY() - this.getY();
            children.put(shape, new Point(relativeX, relativeY));
        }
    }

    @Override
    public void draw(Graphics g) {
        for (Map.Entry<Shape, Point> entry : children.entrySet()) {
            Shape shape = entry.getKey();
            Point relativePosition = entry.getValue();

            // Sauvegarder l'état actuel de Graphics
            Graphics gCopy = g.create();

            // Déplacer le contexte graphique pour dessiner la forme à sa position relative
            gCopy.translate(this.getX() + relativePosition.x, this.getY() + relativePosition.y);
            shape.draw(gCopy);

            // Libérer le contexte graphique temporaire
            gCopy.dispose();
        }
    }

    @Override
    public void move(int dx, int dy) {
        for (Shape shape : children.keySet()) {
            shape.move(dx, dy);
        }
    }

    @Override
    public void setColor(Color color) {
        for (Shape shape : children.keySet()) {
            shape.setColor(color);
        }
    }

    @Override
    public int getX() {
        return children.isEmpty() ? 0 : children.keySet().iterator().next().getX();
    }

    @Override
    public int getY() {
        return children.isEmpty() ? 0 : children.keySet().iterator().next().getY();
    }

    @Override
    public Color getColor() {
        return children.isEmpty() ? Color.BLACK : children.keySet().iterator().next().getColor();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Shape copy() {
        Map<Shape, Point> copiedChildren = new HashMap<>();
        for (Map.Entry<Shape, Point> entry : children.entrySet()) {
            Shape shapeCopy = entry.getKey().copy();
            Point relativePosition = entry.getValue();
            copiedChildren.put(shapeCopy, new Point(relativePosition));
        }
        return new Group(copiedChildren);
    }

    @Override
    public void updateFrom(Shape other) {
        if (other instanceof Group) {
            Group otherGroup = (Group) other;
            this.children.clear();
            for (Shape child : otherGroup.getChildren()) {
                //this.add(child.copy());
            }
        }
    }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
        for (Shape shape : children.keySet()) {
            shape.accept(visitor);
        }
    }
}