package model;

import java.awt.Color;
import java.awt.Graphics;

public class Circle implements Shape {
    private int radius;
    private int x, y;
    private Color color;

    private final int DEFAULT_RADIUS = 50;
    private final Color DEFAULT_COLOR = Color.RED;

    public Circle(int x, int y) {
        this.x = x;
        this.y = y;
        this.radius = DEFAULT_RADIUS;
        this.color = DEFAULT_COLOR;
    }

    public Circle(int x, int y, int radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        int topLeftX = x - radius;
        int topLeftY = y - radius;
        g.fillOval(topLeftX, topLeftY, radius * 2, radius * 2);
    }

    @Override
    public void draw(Graphics g, int shapeX, int shapeY) {
        g.setColor(color);
        int topLeftX = shapeX - radius;
        int topLeftY = shapeY - radius;
        g.fillOval(topLeftX, topLeftY, radius * 2, radius * 2);
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
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
    public Color getColor() {
        return color;
    }

    @Override
    public int getWidth() {
        return radius * 2;
    }

    @Override
    public int getHeight() {
        return radius * 2;
    }

    @Override
    public Shape copy() {
        return new Circle(x, y, radius, color);
    }
}
