package command;

import model.SceneModel;
import model.Shape;

public class EditCommand extends AbstractCommand {
    private final Shape shape;
    private final Shape newState;
    private Shape previousState;

    public EditCommand(SceneModel scene, Shape shape, Shape newState) {
        super(scene);
        this.shape = shape;
        this.newState = newState;
    }

    @Override
    public void execute() {
        previousState = shape.copy();
        shape.updateFrom(newState);  
    }

    @Override
    public void undo() {
        shape.updateFrom(previousState);
    }
}