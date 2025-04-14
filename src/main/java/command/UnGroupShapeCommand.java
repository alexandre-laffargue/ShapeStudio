package command;
 
import model.SceneModel;
import model.Shape;
import model.Group;

import java.util.List;

public class UnGroupShapeCommand extends AbstractCommand {
    private final Group group;
    private List<Shape> ungroupedShapes;

    public UnGroupShapeCommand(SceneModel scene, Group group) {
        super(scene);
        this.group = group;
    }

    @Override
    public void execute() {
        ungroupedShapes = group.getChildren();
        scene.removeShape(group);
    for (Shape shape : ungroupedShapes) {
        scene.addShape(shape);
        shape.move(group.getX() - shape.getX(), group.getY() - shape.getY());
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