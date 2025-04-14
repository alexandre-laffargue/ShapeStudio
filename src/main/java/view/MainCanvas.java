package view;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import model.Shape;
import model.RectangleModel;
import model.SceneModel;
import model.Group;
import model.Hexagon;
import command.AddShapeCommand;
import command.ColorChangeCommand;
import command.CommandManager;
import command.RemoveShapeCommand;

public class MainCanvas extends Canvas {

    private SceneModel model; // Modèle de la scène
    private CommandManager commandManager; // Gestionnaire de commandes

    // Liste des éléments de la toolbar
    private List<ToolbarItem> toolbarItems = new ArrayList<>();
    private ToolbarItem selectedToolbarItem; // Item actuellement sélectionné
    private ToolbarItem trashItem; // Élément de la corbeille

    private static final int ITEM_HEIGHT = 60;
    private static final int ITEM_PADDING = 10;


    // Liste des formes dans la zone de dessin
    private Shape selectedShape;
    private List<Shape> selectedShapes;
    private Point dragStart;
    private boolean isAreaSelecting;
    private java.awt.Rectangle selectionRect;
    private JPopupMenu contextMenu;

    private static final int TOOLBAR_WIDTH = 100;

    public MainCanvas(SceneModel model, CommandManager commandManager) {
        this.model = model;
        this.commandManager = commandManager;

        setBackground(Color.WHITE);
        
        this.toolbarItems = new ArrayList<>();
        // Ajouter des éléments par défaut à la toolbar
        addDefaultToolbarItems();

        this.selectedShapes = new ArrayList<>();
        this.isAreaSelecting = false;
        
        
        // Configurer les gestionnaires d'événements
        setupEventHandlersToolbar();
        setupEventHandlersDrawing();

        // Configurer le menu contextuel
        setupContextMenu();
    }

    /**
     * Ajouter des éléments par défaut à la toolbar
     */
    private void addDefaultToolbarItems() {
        Shape rectangle = new RectangleModel(0, 0);
        Shape hexagon = new Hexagon(0, 0);
        Map<Shape, Point> shapesWithCoordinates = new HashMap<>();
        shapesWithCoordinates.put(new RectangleModel(0, 0), new Point(50, 25));
        shapesWithCoordinates.put(new RectangleModel(0, 0), new Point(50, 125));
        shapesWithCoordinates.put(new RectangleModel(0, 0), new Point(250, 25));
        shapesWithCoordinates.put(new Hexagon(0, 0), new Point(50, 50));
        Shape group = new Group(shapesWithCoordinates, 300, 150);
        
        // ajout au model des formes par défaut
        model.addToolbarShape(rectangle);
        model.addToolbarShape(hexagon);
        model.addToolbarShape(group);

        // Ajouter les formes à la toolbar
        for (Shape shape : model.getToolbarShapes()) {
            addTemplate(shape, createShapeIcon(shape));
        }
        
        // Ajouter la corbeille
        trashItem = new ToolbarItem(null, createTrashIcon());
        // La corbeille sera dessinée séparément en bas de la toolbar
    }



////////////////////////////////////
    // Gestion de la toolbar
    /////////////////////////////////////


    private ToolbarItem findItemAt(int x, int y) {
        // Vérifier d'abord la corbeille (en bas)
        if (trashItem != null) {
            int trashY = getHeight() - ITEM_HEIGHT - ITEM_PADDING;
            if (y >= trashY && y < trashY + ITEM_HEIGHT &&
                x >= ITEM_PADDING && x <= TOOLBAR_WIDTH - ITEM_PADDING) {
                return trashItem;
            }
        }

        // Vérifier les templates
        int currentY = ITEM_PADDING;
        for (ToolbarItem item : toolbarItems) {
            if (y >= currentY && y < currentY + ITEM_HEIGHT &&
                x >= ITEM_PADDING && x <= TOOLBAR_WIDTH - ITEM_PADDING) {
                return item;
            }
            currentY += ITEM_HEIGHT + ITEM_PADDING;
        }
        return null;
    }

    public Shape getSelectedShape() {
        return selectedToolbarItem != null && selectedToolbarItem != trashItem ? selectedToolbarItem.getShape() : null;
    }

    public boolean isTrashSelected() {
        return selectedToolbarItem == trashItem;
    }

    public void clearSelectedItem() {
        selectedToolbarItem = null;
    }

    /**
     * Ajouter un élément à la toolbar
     * @param shape La forme à ajouter
     * @param icon L'icône de la forme
     */
    public void addTemplate(Shape shape, Image icon) {
        toolbarItems.add(new ToolbarItem(shape.copy(), icon));
        repaint();
    }

    /**
     * Dessiner un élément de la toolbar
     * @param g Le contexte graphique
     * @param item L'élément à dessiner
     * @param y La position verticale de l'élément
     * @param isSelected Indique si l'élément est sélectionné
     */
    private void drawItem(Graphics g, ToolbarItem item, int y, boolean isSelected) {
        int centerX = TOOLBAR_WIDTH / 2;

        // Dessiner le cadre de l'élément
        g.setColor(isSelected ? Color.ORANGE : Color.DARK_GRAY);
        g.drawRect(ITEM_PADDING, y, TOOLBAR_WIDTH - 2 * ITEM_PADDING, ITEM_HEIGHT);

        if (item.getIcon() != null) {
            // Dessiner l'icône centrée
            g.drawImage(item.getIcon(), centerX - 16, y + (ITEM_HEIGHT - 32) / 2, null);
        } else if (item.getShape() != null) {
            // Dessiner la forme centrée
            Graphics g2d = (Graphics) g.create();
            g2d.translate(centerX - item.getShape().getX(),
                         y + ITEM_HEIGHT / 2 - item.getShape().getY());
            item.getShape().draw((Graphics2D) g2d);
            g2d.dispose();
        }
    }

    /**
     * Créer une icône pour une forme donnée
     * @param shape La forme à dessiner
     * @return Image de l'icône de la forme
     */
    private Image createShapeIcon(Shape shape) {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Effacer l'arrière-plan
        g2d.setColor(new Color(0, 0, 0, 0)); // Transparent
        g2d.fillRect(0, 0, 32, 32);

        // Définir la couleur de la forme
        g2d.setColor(shape.getColor());

        // Calculer le facteur d'échelle pour ajuster la forme à l'icône
        double scaleX = 32.0 / shape.getWidth(); // Réduire légèrement pour éviter les coupures
        double scaleY = 32.0 / shape.getHeight();
        double scale = Math.min(scaleX, scaleY);

        // Centrer la forme dans l'icône
        int centerX = 16; // Centre de l'icône (32x32)
        int centerY = 16;
        g2d.translate(centerX, centerY);
        g2d.scale(scale, scale);

        // Dessiner la forme centrée
        shape.draw(g2d);

        g2d.dispose();
        return image;
    }

    /**
     * Créer une icône de corbeille simple
     * @return Image de la corbeille
     */
    private Image createTrashIcon() {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.RED);

        // Dessiner une poubelle simple
        g.drawRect(8, 8, 16, 20);
        g.drawLine(12, 8, 12, 4);
        g.drawLine(20, 8, 20, 4);
        g.drawLine(12, 4, 20, 4);
        g.drawLine(10, 12, 10, 24);
        g.drawLine(16, 12, 16, 24);
        g.drawLine(22, 12, 22, 24);

        g.dispose();
        return image;
    }

/////////////////////////////////////
/////////////////////////////////////
    // Gestion des événements de la souris
    /////////////////////////////////////
    /////////////////////////////////////

    private void setupEventHandlersToolbar() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getX() < TOOLBAR_WIDTH) {
                    ToolbarItem item = findItemAt(e.getX(), e.getY());
                    if (item != null) {
                        if (item == selectedToolbarItem) {
                            // Désélectionner si déjà sélectionné
                            selectedToolbarItem = null;
                            System.out.println("Désélectionné");
                        } else {
                            // Sélectionner un nouvel item
                            selectedToolbarItem = item;
                            System.out.println("Sélectionné : " + (item.getShape() != null ? item.getShape() : "Trash"));
                        }
                    }
                    repaint();
                }
            }
        });
    }

    /**
     * Configure les gestionnaires d'événements pour le panneau de dessin.
     */
    private void setupEventHandlersDrawing() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getX() > TOOLBAR_WIDTH) { // Vérifie si le clic est dans la zone de dessin
                    if (e.isPopupTrigger()) { // Vérifie si c'est un clic droit
                        Shape shapeUnderClick = findShapeAt(e.getX(), e.getY());
                        // sélectionne la forme sous le clic et ouvre le menu contextuel
                        if (shapeUnderClick != null) {
                            selectedShapes.clear();
                            selectedShapes.add(shapeUnderClick);
                            selectedShape = shapeUnderClick; // Synchroniser selectedShape
                            dragStart = e.getPoint(); // Enregistrer le point de départ du déplacement
                            repaint(); // affiche le contour bleu
                            contextMenu.show(MainCanvas.this, e.getX(), e.getY());
                        }
                    } else {
                        // Vérifier si un item est sélectionné dans la ToolbarPanel
                        if (isTrashSelected()) { // Pour supprimer une forme
                            // Supprimer la forme sous le clic
                            Shape shapeToDelete = findShapeAt(e.getX(), e.getY());
                            if (shapeToDelete != null) {
                                removeShape(shapeToDelete);
                            }
                        } else {
                            Shape tbSelectedShape = getSelectedShape();
                            if (tbSelectedShape != null) { // Vérifier si un item est sélectionné pour ajouter une forme
                                // Ajouter une nouvelle forme au DrawingPanel
                                addShape(tbSelectedShape, e.getX(), e.getY());
                            } else {
                                // Si aucun item n'est sélectionné dans la ToolbarPanel, sélectionner une forme
                                Shape shapeUnderClick = findShapeAt(e.getX(), e.getY());
                                if (shapeUnderClick != null) {
                                    if (e.isControlDown()) {  // Vérifier si Ctrl est maintenu
                                        if (!selectedShapes.contains(shapeUnderClick)) {
                                            selectedShapes.add(shapeUnderClick);
                                        } else {
                                            selectedShapes.remove(shapeUnderClick);
                                        }
                                    } else { // Sélectionner une nouvelle forme seule
                                        selectedShapes.clear();
                                        selectedShapes.add(shapeUnderClick);
                                        selectedShape = shapeUnderClick; // Synchroniser selectedShape
                                        dragStart = e.getPoint(); // Enregistrer le point de départ du déplacement
                                    }
                                    System.out.println("Forme sélectionnée : " + shapeUnderClick);
                                } else { // Sélectionner une zone
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
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isAreaSelecting) {
                    // Finaliser la sélection de zone
                    finishAreaSelection();
                    isAreaSelecting = false;
                    selectionRect = null;
                }
                repaint();
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
            }
        });
    }

/////////////////////////////////////////
/////////////////////////////////////////
    //        Gestion du drawing
    /////////////////////////////////////
    /////////////////////////////////////
    

    /**
     * Configure le menu contextuel pour changer la couleur de la forme sélectionnée.
     */
    private void setupContextMenu() {
        contextMenu = new JPopupMenu();
        JMenuItem changeColorItem = new JMenuItem("Changer la couleur");

        // Action pour changer la couleur de la forme sélectionnée
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
    
    /**
     * Met à jour le rectangle de sélection en fonction du point actuel.
     * @param currentPoint Le point actuel de la souris.
     */
    private void updateSelectionRect(Point currentPoint) {
        int x = Math.min(dragStart.x, currentPoint.x);
        int y = Math.min(dragStart.y, currentPoint.y);
        int width = Math.abs(currentPoint.x - dragStart.x);
        int height = Math.abs(currentPoint.y - dragStart.y);
        selectionRect = new java.awt.Rectangle(x, y, width, height);
    }

    /**
     * Finalise la sélection de zone en ajoutant toutes les formes qui intersectent avec le rectangle de sélection.
     */
    private void finishAreaSelection() {
        if (selectionRect != null) {
            // Sélectionner toutes les formes qui intersectent avec le rectangle de sélection
            List<Shape> shapes = model.getShapes();
            for (Shape shape : shapes) {
                // Créer un rectangle pour représenter la hitbox de la forme
                java.awt.Rectangle shapeBounds;

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

    /**
     * Trouve la forme à la position (x, y) dans le modèle.
     * @param x La coordonnée x.
     * @param y La coordonnée y.
     * @return La forme trouvée ou null si aucune forme n'est trouvée.
     */
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

    /**
     * Ajoute une nouvelle forme au modèle et à l'affichage.
     * @param shape La forme à ajouter.
     * @param x La position x de la forme.
     * @param y La position y de la forme.
     */
    public void addShape(Shape shape, int x, int y) {
        Shape newShape = shape.copy();
        newShape.move(x - newShape.getX(), y - newShape.getY());

        commandManager.executeCommand(
            new AddShapeCommand(model, newShape)
        );

        repaint();
    }

    /**
     * Supprime une forme du modèle et de l'affichage.
     * @param shape La forme à supprimer.
     */
    public void removeShape(Shape shape) {

        model.getShapes().remove(shape);
        System.out.println("Forme supprimée : " + shape);

        commandManager.executeCommand(
            new RemoveShapeCommand(model, shape)
        );

        repaint();
    }

    /**
     * Dessine toutes les formes sur le panneau.
     * Si une forme est sélectionnée, elle est dessinée avec un contour bleu.
     * Les formes sélectionnées sont dessinées avec un contour bleu et des points de contrôle.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Partie TOOLBAR


        // Dessiner les templates
        int y = ITEM_PADDING;
        for (ToolbarItem item : toolbarItems) {
            drawItem(g, item, y, item == selectedToolbarItem);
            y += ITEM_HEIGHT + ITEM_PADDING;
        }

        // Dessiner la corbeille en bas
        if (trashItem != null) {
            drawItem(g, trashItem, getHeight() - ITEM_HEIGHT - ITEM_PADDING, trashItem == selectedToolbarItem);
        }

        // Partie ZONE DE DESSIN

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
