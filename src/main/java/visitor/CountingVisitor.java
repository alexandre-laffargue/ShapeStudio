package visitor;

import model.Group;
import model.Rectangle;
import model.Hexagon;

public class CountingVisitor implements ShapeVisitor {
    private int rectangleCount = 0;
    private int groupCount = 0;
    private int totalCount = 0;
    
    @Override
    public void visit(Rectangle rectangle) {
        rectangleCount++;
        totalCount++;
    }
    @Override
    public void visit(Group group) {
        groupCount++;
        totalCount++;
        
        for (model.Shape shape : group.getChildren()) {
            shape.accept(this);
        }
    }
    
    @Override
    public void visit(Hexagon regularPolygon) {
        totalCount++;
    }
    
    
    public int getRectangleCount() {
        return rectangleCount;
    }
    
    public int getGroupCount() {
        return groupCount;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
}