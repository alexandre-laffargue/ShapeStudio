package model;

import java.awt.Graphics;

import visitor.ShapeVisitor;

public interface Shape {
    Shape copy();
    void draw(Graphics g);
    void move(int dx, int dy);
    void colorize(Graphics g, int color);
    int getX();
    int getY();

    int getWidth();
    int getHeight();
    void updateFrom(Shape other);
    void accept(ShapeVisitor visitor);
}