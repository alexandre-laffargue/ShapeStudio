package command;

import model.SceneModel;
import model.Shape;

public class RemoveShapeCommand extends AbstractCommand {
    private final Shape shape;

    public RemoveShapeCommand(SceneModel scene, Shape shape) {
        super(scene);
        this.shape = shape;
    }

    @Override
    public void execute() {
        scene.removeShape(shape);
    }

    @Override
    public void undo() {
        scene.addShape(shape);
    }
}