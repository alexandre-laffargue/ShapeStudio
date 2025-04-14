package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SceneModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // Ajoutez un UID pour la sérialisation
    private final List<Shape> shapes;
    private final List<Shape> toolbarShapes; // Liste pour les shapes de la toolbar

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

}
