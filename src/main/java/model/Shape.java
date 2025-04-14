package model;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import visitor.ShapeVisitor;

public interface Shape extends Serializable {
    Shape copy();
    void draw(Graphics g);
    void draw(Graphics g, int shapeX, int shapeY);
    void move(int dx, int dy);
    void setColor(Color color);
    int getX();
    int getY();
    Color getColor();

    int getWidth();
    int getHeight();
    void updateFrom(Shape other);
    void accept(ShapeVisitor visitor);
}