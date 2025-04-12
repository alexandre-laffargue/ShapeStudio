package command;

import model.SceneModel;
import model.Shape;

public class MoveShapeCommand extends AbstractCommand {
    private final Shape shape;
    private final int dx, dy;
    private int previousX, previousY;

    public MoveShapeCommand(SceneModel scene, Shape shape, int dx, int dy) {
        super(scene);
        this.shape = shape;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void execute() {
        previousX = shape.getX();
        previousY = shape.getY();
        shape.move(dx, dy);
    }

    @Override
    public void undo() {
        shape.move(previousX - shape.getX(), previousY - shape.getY());
    }
}