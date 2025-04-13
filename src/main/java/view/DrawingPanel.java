package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import command.AddShapeCommand;
import command.ColorChangeCommand;
import command.CommandManager;
import command.MoveShapeCommand;
import command.RemoveShapeCommand;
import model.SceneModel;
import model.Shape;

public class DrawingPanel extends Panel {
    private SceneModel model;
    private CommandManager commandManager;
    private ToolbarPanel toolbarPanel;
    private Shape selectedShape;
    private List<Shape> selectedShapes;
    private Point dragStart;
    private boolean isDragging;
    private boolean isAreaSelecting;
    private Rectangle selectionRect;
    private JPopupMenu contextMenu;

    public DrawingPanel(SceneModel model, CommandManager commandManager, ToolbarPanel toolbarPanel) {
        this.model = model;
        this.commandManager = commandManager;
        this.toolbarPanel = toolbarPanel;
        this.selectedShapes = new ArrayList<>();
        this.isDragging = false;
        this.isAreaSelecting = false;

        setBackground(Color.WHITE);
        setupEventHandlers();
        setupContextMenu();
    }

    private void setupEventHandlers() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) { // Vérifie si c'est un clic droit
                    Shape shapeUnderClick = findShapeAt(e.getX(), e.getY());
                    if (shapeUnderClick != null && selectedShapes.contains(shapeUnderClick)) {
                        contextMenu.show(DrawingPanel.this, e.getX(), e.getY());
                    }
                } else {
                    // Vérifier si un item est sélectionné dans la ToolbarPanel
                    if (toolbarPanel.isTrashSelected()) {
                        // Supprimer la forme sous le clic
                        Shape shapeToDelete = findShapeAt(e.getX(), e.getY());
                        if (shapeToDelete != null) {
                            removeShape(shapeToDelete);
                        }
                    } else {
                        Shape tbSelectedShape = toolbarPanel.getSelectedShape();
                        if (tbSelectedShape != null) {
                            // Ajouter une nouvelle forme au DrawingPanel
                            addShape(tbSelectedShape, e.getX(), e.getY());
                        } else {
                            // Si aucun item n'est sélectionné dans la ToolbarPanel, sélectionner une forme
                            Shape shapeUnderClick = findShapeAt(e.getX(), e.getY());

                            if (shapeUnderClick != null) {
                                if (e.isControlDown()) {
                                    // Ajouter à la sélection si Ctrl est maintenu
                                    if (!selectedShapes.contains(shapeUnderClick)) {
                                        selectedShapes.add(shapeUnderClick);
                                    } else {
                                        // Désélectionner si déjà sélectionné
                                        selectedShapes.remove(shapeUnderClick);
                                    }
                                } else {
                                    // Remplacer la sélection actuelle
                                    selectedShapes.clear();
                                    selectedShapes.add(shapeUnderClick);
                                    selectedShape = shapeUnderClick; // Synchroniser selectedShape
                                    dragStart = e.getPoint(); // Enregistrer le point de départ du déplacement
                                }
                                System.out.println("Forme sélectionnée : " + shapeUnderClick);
                            } else {
                                // Si aucune forme n'est trouvée, commencer une sélection de zone
                                selectedShapes.clear();
                                selectedShape = null;
                                dragStart = e.getPoint();
                                isAreaSelecting = true;
                                selectionRect = new Rectangle(dragStart.x, dragStart.y, 0, 0);
                            }
                            repaint();
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Shape shapeUnderClick = findShapeAt(e.getX(), e.getY());
                    if (shapeUnderClick != null && selectedShapes.contains(shapeUnderClick)) {
                        contextMenu.show(DrawingPanel.this, e.getX(), e.getY());
                    }
                } else {
                    if (isAreaSelecting) {
                        // Finaliser la sélection de zone
                        finishAreaSelection();
                        isAreaSelecting = false;
                        selectionRect = null;
                    }
                    // Arrêter le déplacement
                    else if (isDragging) {
                        int dx = e.getX() - dragStart.x;
                        int dy = e.getY() - dragStart.y;

                        if (!selectedShapes.isEmpty()) {
                            // Exécuter une commande de déplacement pour chaque forme sélectionnée
                            for (Shape shape : selectedShapes) {
                                commandManager.executeCommand(
                                    new MoveShapeCommand(model, shape, dx, dy)
                                );
                            }
                        }
                        isDragging = false;
                    }
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isAreaSelecting) {
                    // Mettre à jour le rectangle de sélection
                    updateSelectionRect(e.getPoint());
                    repaint();
                }
                else if (!selectedShapes.isEmpty()) {
                    isDragging = true;

                    // Calculer le déplacement
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;

                    // Déplacer toutes les formes sélectionnées
                    for (Shape shape : selectedShapes) {
                        shape.move(dx, dy);
                    }

                    dragStart = e.getPoint(); // Mettre à jour le point de départ
                    repaint();
                }
            }
        });
    }

    private void setupContextMenu() {
        contextMenu = new JPopupMenu();
        JMenuItem changeColorItem = new JMenuItem("Changer la couleur");

        // Action : Change Color
        changeColorItem.addActionListener(e -> {
            if (selectedShape != null) {
                Color newColor = JColorChooser.showDialog(this, "Choisir une couleur", Color.BLACK);
                if (newColor != null) {
                    commandManager.executeCommand(
                                    new ColorChangeCommand(model, selectedShape, newColor)
                                );
                    repaint();
                }
            }
        });

        contextMenu.add(changeColorItem);
    }

    private void updateSelectionRect(Point currentPoint) {
        int x = Math.min(dragStart.x, currentPoint.x);
        int y = Math.min(dragStart.y, currentPoint.y);
        int width = Math.abs(currentPoint.x - dragStart.x);
        int height = Math.abs(currentPoint.y - dragStart.y);
        selectionRect = new Rectangle(x, y, width, height);
    }

    private void finishAreaSelection() {
        if (selectionRect != null) {
            // Sélectionner toutes les formes qui intersectent avec le rectangle de sélection
            List<Shape> shapes = model.getShapes();
            for (Shape shape : shapes) {
                // Créer un rectangle pour représenter la hitbox de la forme
                Rectangle shapeBounds;

                shapeBounds = new Rectangle(
                    shape.getX() - shape.getWidth()/2,
                    shape.getY() - shape.getHeight()/2,
                    shape.getWidth(),
                    shape.getHeight()
                );

                // Si le rectangle de sélection intersecte avec la hitbox de la forme
                if (selectionRect.intersects(shapeBounds)) {
                    selectedShapes.add(shape);
                    System.out.println("Forme sélectionnée par la zone : " + shape);
                }
            }

            // Si une seule forme est sélectionnée, la définir comme forme sélectionnée principale
            if (selectedShapes.size() == 1) {
                selectedShape = selectedShapes.get(0);
            }
        }
    }

    private Shape findShapeAt(int x, int y) {
        List<Shape> shapes = model.getShapes();
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape shape = shapes.get(i);

            // Pour les autres formes, vérifier par rapport au centre
            int halfWidth = shape.getWidth() / 2;
            int halfHeight = shape.getHeight() / 2;

            if (x >= shape.getX() - halfWidth && x <= shape.getX() + halfWidth &&
                y >= shape.getY() - halfHeight && y <= shape.getY() + halfHeight) {
                return shape;
            }

        }
        return null;
    }

    public void addShape(Shape shape, int x, int y) {
        Shape newShape = shape.copy();
        newShape.move(x - newShape.getX(), y - newShape.getY());

        commandManager.executeCommand(
            new AddShapeCommand(model, newShape)
        );

        repaint();
    }

    public void removeShape(Shape shape) {

        model.getShapes().remove(shape);
        System.out.println("Forme supprimée : " + shape);

        commandManager.executeCommand(
            new RemoveShapeCommand(model, shape)
        );

        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        for (Shape shape : model.getShapes()) {
            shape.draw(g);
        }

        // Dessiner les indicateurs de sélection pour les formes sélectionnées
        if (!selectedShapes.isEmpty()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));

            for (Shape shape : selectedShapes) {
                int[] corners = new int[8]; // Pour stocker les coordonnées des coins

                int halfWidth = shape.getWidth() / 2;
                int halfHeight = shape.getHeight() / 2;

                g2d.drawRect(
                    shape.getX() - halfWidth,
                    shape.getY() - halfHeight,
                    shape.getWidth(),
                    shape.getHeight()
                );

                // Points de contrôle aux coins
                corners[0] = shape.getX() - halfWidth;
                corners[1] = shape.getY() - halfHeight;
                corners[2] = shape.getX() + halfWidth;
                corners[3] = shape.getY() - halfHeight;
                corners[4] = shape.getX() - halfWidth;
                corners[5] = shape.getY() + halfHeight;
                corners[6] = shape.getX() + halfWidth;
                corners[7] = shape.getY() + halfHeight;

                // Dessiner les points de contrôle
                for (int i = 0; i < corners.length; i += 2) {
                    g2d.fillRect(corners[i] - 3, corners[i+1] - 3, 6, 6);
                }
            }

            g2d.dispose();
        }

        // Dessiner le rectangle de sélection en cours
        if (isAreaSelecting && selectionRect != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(0, 0, 255, 50));  // Bleu semi-transparent
            g2d.fill(selectionRect);
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
            g2d.draw(selectionRect);
            g2d.dispose();
        }
    }
}