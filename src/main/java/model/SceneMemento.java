package model;

import java.util.List;

public class SceneMemento {
    private final List<Shape> shapes;

    public SceneMemento(List<Shape> shapes) {
        this.shapes = shapes.stream().map(Shape::copy).toList();
    }

    public List<Shape> getShapes() {
        return shapes.stream().map(Shape::copy).toList();
    }
}