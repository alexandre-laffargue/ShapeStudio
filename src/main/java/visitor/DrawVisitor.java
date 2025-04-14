package visitor;

import java.awt.Graphics;

import model.Group;
import model.RectangleModel;
import model.Hexagon;

public class DrawVisitor implements ShapeVisitor {
    private Graphics graphics;
    
    public DrawVisitor(Graphics graphics) {
        this.graphics = graphics;
    }
    
    @Override
    public void visit(RectangleModel rectangle) {
        rectangle.draw(graphics);
    }
    
    @Override
    public void visit(Group group) {
        for (model.Shape shape : group.getChildren()) {
            shape.accept(this);
        }
    }
    
    @Override
    public void visit(Hexagon polygon) {
        polygon.draw(graphics);
    }
}