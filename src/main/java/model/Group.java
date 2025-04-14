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

    private int x;
    private int y;

    public Group(Map<Shape, Point> shapesWithCoordinates,int width,int height) {
        this.children = new HashMap<>();
    
        for (Map.Entry<Shape, Point> entry : shapesWithCoordinates.entrySet()) {
            Shape shape = entry.getKey();
            Point relativePosition = entry.getValue();
            this.children.put(shape, relativePosition);

            shape.move(relativePosition.x, relativePosition.y);
        }

        this.width = width;
        this.height = height;
    }

    public List<Shape> getChildren() {
        return new ArrayList<>(children.keySet());
    }

    @Override
    public void draw(Graphics g) {
        int topLeftX = getX() - getWidth() / 2;
        int topLeftY = getY() - getHeight() / 2;

        for (Map.Entry<Shape, Point> entry : children.entrySet()) {
            Shape shape = entry.getKey();
            int shapeX = topLeftX + entry.getValue().x;
            int shapeY = topLeftY + entry.getValue().y;
            shape.draw(g, shapeX, shapeY);
        }
    }

    @Override
    public void draw(Graphics g, int offsetX, int offsetY) { // useless maybe
        int topLeftX = offsetX - getWidth() / 2;
        int topLeftY = offsetY - getHeight() / 2;

        for (Map.Entry<Shape, Point> entry : children.entrySet()) {
            Shape shape = entry.getKey();
            int shapeX = topLeftX + entry.getValue().x;
            int shapeY = topLeftY + entry.getValue().y;
            shape.draw(g, shapeX, shapeY);
        }
    }


    @Override
    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
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
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
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
        return new Group(copiedChildren, this.width, this.height);
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