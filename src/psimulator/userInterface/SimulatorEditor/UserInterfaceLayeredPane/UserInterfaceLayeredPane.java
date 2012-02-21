package psimulator.userInterface.SimulatorEditor.UserInterfaceLayeredPane;

import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.dataLayer.Enums.ObserverUpdateEventType;
import psimulator.dataLayer.Singletons.ZoomManagerSingleton;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.AnimationPanel.AnimationPanel;
import psimulator.userInterface.SimulatorEditor.AnimationPanel.AnimationPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanel;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelOuterInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.DrawPanelAction;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Enums.MainTool;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;
import psimulator.userInterface.SimulatorEditor.DrawPanel.MouseActionListeners.DrawPanelListenerStrategy;
import psimulator.userInterface.SimulatorEditor.UserInterfaceMainPanelInnerInterface;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public class UserInterfaceLayeredPane extends UserInterfaceLayeredPaneOuterInterface implements Observer {

    private DrawPanelOuterInterface jPanelDraw; // draw panel
    private AnimationPanelOuterInterface jPanelAnimation; // animation panel
    //
    //private ZoomManager zoomManager = new ZoomManager();
    //
    private MainWindowInnerInterface mainWindow;
    private UserInterfaceMainPanelInnerInterface userInterface;

    public UserInterfaceLayeredPane(MainWindowInnerInterface mainWindow, UserInterfaceMainPanelInnerInterface userInterface,
            DataLayerFacade dataLayer) {

        //
        this.mainWindow = mainWindow;
        this.userInterface = userInterface;

        // create draw panel
        jPanelDraw = new DrawPanel(mainWindow, userInterface, dataLayer);

        // add panel to layered pane
        this.add(jPanelDraw, 1, 0);

        // create animation panel
        jPanelAnimation = new AnimationPanel(mainWindow, userInterface, dataLayer, jPanelDraw);

        // add panel to layered pane
        this.add(jPanelAnimation, 2, 0);

        // add this as observer to zoom manager
        ZoomManagerSingleton.getInstance().addObserver((Observer)this);

        // add jPanelAnimation as observer to preferences manager
        dataLayer.addPreferencesObserver(jPanelAnimation);
    }

    @Override
    public void update(Observable o, Object o1) {
        switch ((ObserverUpdateEventType) o1) {
            case ZOOM_CHANGE:
            case GRAPH_SIZE_CHANGED:
                Dimension dim = jPanelDraw.getPreferredSize();
                jPanelAnimation.setPreferredSize(dim);
                jPanelAnimation.setSize(dim);
                break;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // Draw panel always has the desired size
        return jPanelDraw.getPreferredSize();
    }

/// from Draw panel outer interface
    @Override
    public boolean canUndo() {
        return jPanelDraw.canUndo();
    }

    @Override
    public boolean canRedo() {
        return jPanelDraw.canRedo();
    }

    @Override
    public void undo() {
        jPanelDraw.undo();
    }

    @Override
    public void redo() {
        jPanelDraw.redo();
    }

    @Override
    public AbstractAction getAbstractAction(DrawPanelAction action) {
        return jPanelDraw.getAbstractAction(action);
    }

    @Override
    public Graph removeGraph() {
        // remove this as observer from graph
        if (jPanelDraw.hasGraph()) {
            jPanelDraw.getGraph().deleteObserver(this);
        }

        jPanelAnimation.removeGraph();
        return jPanelDraw.removeGraph();
    }

    @Override
    public void setGraph(Graph graph) {
        jPanelDraw.setGraph(graph);
        jPanelAnimation.setGraph(graph);

        // observe for graph size changes
        graph.addObserver(this);
    }

    @Override
    public boolean hasGraph() {
        return jPanelDraw.hasGraph();
    }

    @Override
    public Graph getGraph() {
        return jPanelDraw.getGraph();
    }

// IMPLEMENTS DrawPanelToolChangeOuterInterface    
    @Override
    public void removeCurrentMouseListener() {
        jPanelDraw.removeCurrentMouseListener();
    }

    @Override
    public DrawPanelListenerStrategy getMouseListener(MainTool tool) {
        return jPanelDraw.getMouseListener(tool);
    }

    @Override
    public void setCurrentMouseListener(DrawPanelListenerStrategy mouseListener) {
        jPanelDraw.setCurrentMouseListener(mouseListener);
    }

    @Override
    public void setCurrentMouseListenerSimulator() {
        jPanelDraw.setCurrentMouseListenerSimulator();
    }

    @Override
    public AnimationPanelOuterInterface getAnimationPanelOuterInterface() {
        return jPanelAnimation;
    }
}
