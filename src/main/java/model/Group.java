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

    public Group() {
        this.children = new HashMap<>();
    }

    public void add(Shape shape) {
        // Calculer les coordonnées relatives de la forme par rapport au groupe
        int relativeX = shape.getX() - this.getX();
        int relativeY = shape.getY() - this.getY();
        children.put(shape, new Point(relativeX, relativeY));
    }

    public void remove(Shape shape) {
        children.remove(shape);
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
        if (children.isEmpty()) return 0;

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;

        for (Shape child : children.keySet()) {
            int childX = child.getX();
            int childWidth = child.getWidth();

            minX = Math.min(minX, childX);
            maxX = Math.max(maxX, childX + childWidth);
        }

        return maxX - minX;
    }

    @Override
    public int getHeight() {
        if (children.isEmpty()) return 0;

        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Shape child : children.keySet()) {
            int childY = child.getY();
            int childHeight = child.getHeight();

            minY = Math.min(minY, childY);
            maxY = Math.max(maxY, childY + childHeight);
        }

        return maxY - minY;
    }

    @Override
    public Shape copy() {
        Group copiedGroup = new Group();
        for (Shape shape : children.keySet()) {
            copiedGroup.add(shape.copy());
        }
        return copiedGroup;
    }

    @Override
    public void updateFrom(Shape other) {
        if (other instanceof Group) {
            Group otherGroup = (Group) other;
            this.children.clear();
            for (Shape child : otherGroup.getChildren()) {
                this.add(child.copy());
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