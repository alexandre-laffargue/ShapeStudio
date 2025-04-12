package model;

import java.util.ArrayList;
import java.util.List;

public class SceneModel {

    private List<Shape> shapes;

    public SceneModel() {
        this.shapes = new ArrayList<>();
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    public void removeShape(Shape shape) {
        shapes.remove(shape);
    }

    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }

    public void clear() {
        shapes.clear();
    }

    public SceneMemento save() {
        return new SceneMemento(new ArrayList<>(shapes));
    }
    
    public void restore(SceneMemento memento) {
        this.shapes = memento.getShapes();
    }


    public void replaceShapes(List<Shape> toRemove, Shape toAdd) {
        shapes.removeAll(toRemove);
        shapes.add(toAdd);
    }

    public List<Shape> cloneShapes() {
        List<Shape> copy = new ArrayList<>();
        for (Shape s : shapes) {
            copy.add(s.copy());
        }
        return copy;
    }


}
