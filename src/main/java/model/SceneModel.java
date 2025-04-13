package model;

import java.util.ArrayList;
import java.util.List;

public class SceneModel {

    private List<Shape> shapes;
    private List<Shape> toolbarShapes; // Liste pour les shapes de la toolbar

    public SceneModel() {
        this.shapes = new ArrayList<>();
        this.toolbarShapes = new ArrayList<>(); // Initialisation de la liste de la toolbar
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

    // Méthodes pour gérer les shapes de la toolbar
    public void addToolbarShape(Shape shape) {
        toolbarShapes.add(shape);
    }

    public void removeToolbarShape(Shape shape) {
        toolbarShapes.remove(shape);
    }

    public List<Shape> getToolbarShapes() {
        return new ArrayList<>(toolbarShapes);
    }

    public void clearToolbarShapes() {
        toolbarShapes.clear();
    }

    public SceneMemento save() {
        return new SceneMemento(new ArrayList<>(shapes), new ArrayList<>(toolbarShapes));
    }

    public void restore(SceneMemento memento) {
        this.shapes = memento.getShapes();
        this.toolbarShapes = memento.getToolbarShapes();
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
