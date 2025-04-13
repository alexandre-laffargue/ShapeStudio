package model;

import java.awt.Color;
import java.awt.Graphics;

import visitor.ShapeVisitor;

public class Hexagon implements Shape {
    private int sides;
    private int sideLength;
    private int x, y; 
    private double rotation; 
    private Color color;
    
    private final Color DEFAULT_COLOR = Color.GREEN;
    private final int DEFAULT_SIDES = 6; 
    private final int DEFAULT_SIZE = 30;
    
    public Hexagon(int x, int y) {
        this.x = x;
        this.y = y;
        this.sides = DEFAULT_SIDES;
        this.sideLength = DEFAULT_SIZE;
        this.rotation = 0;
        this.color = DEFAULT_COLOR;
    }
    
    public Hexagon(int x, int y, int sides, int sideLength) {
        this.x = x;
        this.y = y;
        this.sides = sides;
        this.sideLength = sideLength;
        this.rotation = 0;
        this.color = DEFAULT_COLOR;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);

        // Calculer les coordonnées des sommets
        int[] xPoints = new int[sides];
        int[] yPoints = new int[sides];

        // Rayon basé sur la longueur des côtés
        double radius = sideLength / (2 * Math.sin(Math.PI / sides));

        for (int i = 0; i < sides; i++) {
            double angle = 2 * Math.PI * i / sides + Math.toRadians(rotation);
            xPoints[i] = (int) (x + radius * Math.cos(angle));
            yPoints[i] = (int) (y + radius * Math.sin(angle));
        }

        // Dessiner et remplir l'hexagone
        g.fillPolygon(xPoints, yPoints, sides);
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
        // Calculate the width based on the radius and number of sides
        double radius = sideLength / (2 * Math.sin(Math.PI / sides));
        return (int)(radius * 2);
    }

    @Override
    public int getHeight() {
        // For a regular polygon, height is typically the same as width
        return getWidth();
    }

    @Override
    public Shape copy() {
        Hexagon copy = new Hexagon(x, y, sides, sideLength);
        copy.rotation = this.rotation;
        copy.color = this.color;
        return copy;
    }

    @Override
    public void updateFrom(Shape other) {
        if (other instanceof Hexagon) {
            Hexagon polygon = (Hexagon) other;
            this.x = polygon.x;
            this.y = polygon.y;
            this.sides = polygon.sides;
            this.sideLength = polygon.sideLength;
            this.rotation = polygon.rotation;
            this.color = polygon.color;
        }
    }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }

    public int getSides() {
        return sides;
    }
    
}