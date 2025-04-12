package command;

import java.util.List;

import model.Group;
import model.SceneModel;
import model.Shape;

public class GroupCommand extends AbstractCommand { // TODO : add coordinates of children
    private final List<Shape> shapesToGroup;
    private Group group;

    public GroupCommand(SceneModel scene, List<Shape> shapesToGroup) {
        super(scene);
        this.shapesToGroup = shapesToGroup;
    }

    @Override
    public void execute() {
        group = new Group();
        for (Shape shape : shapesToGroup) {
            group.add(shape);
            scene.removeShape(shape);
        }
        scene.addShape(group);
    }

    @Override
    public void undo() {
        scene.removeShape(group);
        for (Shape shape : shapesToGroup) {
            scene.addShape(shape);
        }
    }
}