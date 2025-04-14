package command;

import model.SceneModel;
import model.Shape;

public class RemoveShapeCommand extends AbstractCommand {
    private final Shape shape;
    private final int previousX, previousY;

    public RemoveShapeCommand(SceneModel scene, Shape shape, int previousX, int previousY) {
        super(scene);
        this.shape = shape;
        this.previousX = previousX;
        this.previousY = previousY;
    }

    @Override
    public void execute() {
        shape.move(previousX - shape.getX(), previousY - shape.getY());
        scene.removeShape(shape);
    }

    @Override
    public void undo() {
        scene.addShape(shape);
    }
}