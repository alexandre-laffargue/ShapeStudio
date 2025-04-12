package command;

import model.SceneModel;

public abstract class AbstractCommand implements Command {
    protected SceneModel scene;

    public AbstractCommand(SceneModel scene) {
        this.scene = scene;
    }


}
