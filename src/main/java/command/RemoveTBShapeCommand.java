package command;

import model.SceneModel;
import model.Shape;

public class RemoveTBShapeCommand extends AbstractCommand {
    private final Shape shape;

    public RemoveTBShapeCommand(SceneModel scene, Shape shape) {
        super(scene);
        this.shape = shape;
    }

    @Override
    public void execute() {
        scene.removeToolbarShape(shape);
    }

    @Override
    public void undo() {
        scene.addToolbarShape(shape);
    }
    
}
