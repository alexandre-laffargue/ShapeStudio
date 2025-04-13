package view;

import java.awt.Image;

import model.Shape;

public class ToolbarItem {
    private Shape shape;
    private Image icon;

    public ToolbarItem(Shape shape, Image icon) {
        this.shape = shape;
        this.icon = icon;
    }

    public Shape getShape() {
        return shape;
    }

    public Image getIcon() {
        return icon;
    }
}
