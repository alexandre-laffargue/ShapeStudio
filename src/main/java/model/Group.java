package model;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import visitor.ShapeVisitor ;

public class Group implements Shape {
    // TODO : add coordinates for children
    private final List<Shape> children;

    public Group() {
        this.children = new ArrayList<>();
    }

    public void add(Shape shape) {
        children.add(shape);
    }

    public void remove(Shape shape) {
        children.remove(shape);
    }

    public List<Shape> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public void draw(Graphics g) {
        for (Shape shape : children) {
            shape.draw(g);
        }
    }

    @Override
    public void move(int dx, int dy) {
        for (Shape shape : children) {
            shape.move(dx, dy); 
        }
    }

    @Override
    public void colorize(Graphics g, int color) {
        for (Shape shape : children) {
            shape.colorize(g, color);
        }
    }

    @Override
    public int getX() {
        return children.isEmpty() ? 0 : children.get(0).getX();
    }

    @Override
    public int getY() {
        return children.isEmpty() ? 0 : children.get(0).getY();
    }

    @Override
    public Shape copy() {
        Group copiedGroup = new Group();
        for (Shape shape : children) {
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
                this.children.add(child.copy());
            }
        }
    }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
        for (Shape shape : children) {
            shape.accept(visitor);
        }
    }
}