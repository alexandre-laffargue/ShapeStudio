package command;

import java.util.Map;
import java.awt.Point;

import model.Group;
import model.SceneModel;
import model.Shape;

public class AddGroupShapeCommand extends AbstractCommand {
    private final Map<Shape, Point> shapesWithCoordinates;
    private Group group;
    private final int width;
    private final int height;
    private final int x;
    private final int y;

    public AddGroupShapeCommand(SceneModel scene, Map<Shape, Point> shapesWithCoordinates, int width, int height, int x, int y) {
        super(scene);
        this.shapesWithCoordinates = shapesWithCoordinates;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute() {
        group = new Group(shapesWithCoordinates, width, height);
        group.move(x, y);
        scene.addShape(group);
        for (Shape shape : shapesWithCoordinates.keySet()) {
            scene.removeShape(shape);
        }
    }

    @Override
    public void undo() {
        for (Shape shape : shapesWithCoordinates.keySet()) {
            scene.addShape(shape);
        }
        scene.removeShape(group);
    }
}