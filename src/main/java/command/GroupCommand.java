package command;

import java.util.Map;
import java.awt.Point;

import model.Group;
import model.SceneModel;
import model.Shape;

public class GroupCommand extends AbstractCommand {
    private final Map<Shape, Point> shapesWithCoordinates;
    private Group group;

    public GroupCommand(SceneModel scene, Map<Shape, Point> shapesWithCoordinates) {
        super(scene);
        this.shapesWithCoordinates = shapesWithCoordinates;
    }

    @Override
    public void execute() {
        group = new Group(shapesWithCoordinates);
        scene.addShape(group);
    }

    @Override
    public void undo() {
        scene.removeShape(group);
    }
}