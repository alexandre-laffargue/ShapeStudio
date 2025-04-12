package command;

import model.SceneModel;
import model.Shape;

public class AddShapeCommand extends AbstractCommand {
    private final Shape shape;

    public AddShapeCommand(SceneModel scene, Shape shape) {
        super(scene);
        this.shape = shape;
    }

    @Override
    public void execute() {
        scene.addShape(shape);
    }

    @Override
    public void undo() {
        scene.removeShape(shape);
    }
    
}