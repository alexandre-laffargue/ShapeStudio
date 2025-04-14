package command;

import model.SceneModel;
import model.Shape;

public class MoveShapeCommand extends AbstractCommand {
    private final Shape shape;
    private final int dx, dy;
    private final int previousX, previousY;

    public MoveShapeCommand(SceneModel scene, Shape shape, int dx, int dy, int previousX, int previousY) {
        super(scene);
        this.shape = shape;
        this.dx = dx;
        this.dy = dy;
        this.previousX = previousX;
        this.previousY = previousY;
    }

    @Override
    public void execute() {
        shape.move(dx, dy);
    }

    @Override
    public void undo() {
        shape.move(previousX - shape.getX(), previousY - shape.getY());
    }
}