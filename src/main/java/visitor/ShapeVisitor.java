package visitor;

import model.Group;
import model.RegularPolygon;
import model.Rectangle;

public interface ShapeVisitor {
    void visit(Rectangle rectangle);
    void visit(Group group);
    void visit(RegularPolygon polygon);
}