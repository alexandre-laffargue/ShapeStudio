package command;

import java.util.Map;
import java.awt.Point;

import model.Group;
import model.SceneModel;
import model.Shape;

public class AddTBShapeCommand extends AbstractCommand {
    private final Map<Shape, Point> shapesWithCoordinates;
    private Group group;
    private final int width;
    private final int height;

    public AddTBShapeCommand(SceneModel scene, Map<Shape, Point> shapesWithCoordinates, int width, int height) {
        super(scene);
        this.shapesWithCoordinates = shapesWithCoordinates;
        this.width = width;
        this.height = height;
    }

    @Override
    public void execute() {
        group = new Group(shapesWithCoordinates, width, height);
        scene.addToolbarShape(group);
    }

    @Override
    public void undo() {
        scene.removeToolbarShape(group);
    }
}