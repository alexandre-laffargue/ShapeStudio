package model;

import java.awt.Color;
import java.awt.Graphics;

public class RectangleModel implements Shape {

    private int height;
    private int width;
    private int x, y;
    private Color color;

    private final int DEFAULT_WIDTH = 100;
    private final int DEFAULT_HEIGHT = 50;
    private final Color DEFAULT_COLOR = Color.BLUE;
    

    public RectangleModel(int x, int y) {
        this.x = x;
        this.y = y;
        this.height = DEFAULT_HEIGHT;
        this.width = DEFAULT_WIDTH;
        this.color = DEFAULT_COLOR;
    }

    @Override
    public void draw(Graphics g) {
        int topLeftX = getX() - getWidth() / 2;
        int topLeftY = getY() - getHeight() / 2;

        // Dessiner le rectangle
        g.setColor(color);
        g.fillRect(topLeftX, topLeftY, getWidth(), getHeight());
    }

    @Override
    public void draw(Graphics g, int shapeX, int shapeY) {
        int topLeftX = shapeX - getWidth() / 2;
        int topLeftY = shapeY - getHeight() / 2;

        // Dessiner le rectangle
        g.setColor(color);
        g.fillRect(topLeftX, topLeftY, getWidth(), getHeight());
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
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Shape copy() {
        RectangleModel copy = new RectangleModel(this.x, this.y);
        copy.width = this.width;
        copy.height = this.height;
        copy.color = this.color;
        return copy;
    }
}
