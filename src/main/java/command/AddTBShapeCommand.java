package command;

import model.SceneModel;
import model.Shape;

public class AddTBShapeCommand extends AbstractCommand {
    private final Shape shape;

    public AddTBShapeCommand(SceneModel scene, Shape shape) {
        super(scene);
        this.shape = shape;
    }

    @Override
    public void execute() {
        scene.addToolbarShape(shape);
        scene.removeShape(shape);
    }

    @Override
    public void undo() {
        scene.removeToolbarShape(shape);
        scene.addShape(shape);
    }
}