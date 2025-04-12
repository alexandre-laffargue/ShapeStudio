package command;

import model.SceneModel;
import model.Shape;
import model.Group;

import java.util.List;

public class UngroupCommand extends AbstractCommand {
    private final Group group;
    private List<Shape> ungroupedShapes;

    public UngroupCommand(SceneModel scene, Group group) {
        super(scene);
        this.group = group;
    }

    @Override
    public void execute() {
        ungroupedShapes = group.getChildren();
        scene.removeShape(group);
        for (Shape shape : ungroupedShapes) {
            scene.addShape(shape);
        }
    }

    @Override
    public void undo() {
        for (Shape shape : ungroupedShapes) {
            scene.removeShape(shape);
        }
        scene.addShape(group);
    }
}