package command;

import java.awt.Color;

import model.SceneModel;
import model.Shape;

public class ColorChangeCommand extends AbstractCommand {
    private final Shape shape;
    private final Color newColor;
    private Color previousColor;

    public ColorChangeCommand(SceneModel scene, Shape shape, Color newColor) {
        super(scene);
        this.shape = shape;
        this.newColor = newColor;
    }

    @Override
    public void execute() {
        previousColor = shape.getColor();
        shape.setColor(newColor);
    }

    @Override
    public void undo() {
        shape.setColor(previousColor);
    }
}