package visitor;

import model.Group;
import model.Hexagon;
import model.RectangleModel;

public interface ShapeVisitor {
    void visit(RectangleModel rectangle);
    void visit(Group group);
    void visit(Hexagon polygon);
}