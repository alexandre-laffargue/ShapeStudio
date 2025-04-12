package model;

import java.awt.Color;
import java.awt.Graphics;

import visitor.ShapeVisitor;

public class Rectangle implements Shape {

    private int length;
    private int width;
    private int x, y;

    private final int DEFAULT_LENGTH = 50;
    private final int DEFAULT_WIDTH = 100;
    private final Color DEFAULT_COLOR = Color.BLUE;
    

    public Rectangle(int x, int y) {
        this.x = x;
        this.y = y;
        this.length = DEFAULT_LENGTH;
        this.width = DEFAULT_WIDTH;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(DEFAULT_COLOR);
        g.drawRect(x, y, width, length);
        g.fillRect(x, y, width, length);
    }


    @Override
    public void colorize(Graphics g, int color) {
        g.setColor(new Color(color));
        g.fillRect(x, y, width, length);
    }

    @Override
    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }


    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return length;
    }

    @Override
    public Shape copy() {
        return new Rectangle(x, y);
    }
    
    @Override
    public void updateFrom(Shape other) {
        if (other instanceof Rectangle) {
            Rectangle rect = (Rectangle) other;
            this.x = rect.x;
            this.y = rect.y;
            this.width = rect.width;
            this.length = rect.length;
        }
    }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }
}
