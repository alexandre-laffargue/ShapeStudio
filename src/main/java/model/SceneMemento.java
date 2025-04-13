package model;

import java.util.List;

public class SceneMemento {
    private final List<Shape> shapes;
    private final List<Shape> toolbarShapes;

    public SceneMemento(List<Shape> shapes, List<Shape> toolbarShapes) {
        this.shapes = shapes.stream().map(Shape::copy).toList();
        this.toolbarShapes = toolbarShapes.stream().map(Shape::copy).toList();
    }

    public List<Shape> getShapes() {
        return shapes.stream().map(Shape::copy).toList();
    }

    public List<Shape> getToolbarShapes() {
        return toolbarShapes.stream().map(Shape::copy).toList();
    }
}