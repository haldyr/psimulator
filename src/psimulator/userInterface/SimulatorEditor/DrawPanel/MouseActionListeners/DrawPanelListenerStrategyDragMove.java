package psimulator.userInterface.SimulatorEditor.DrawPanel.MouseActionListeners;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.undo.UndoManager;
import psimulator.dataLayer.DataLayerFacade;
import psimulator.userInterface.MainWindowInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.DrawPanelInnerInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.ZoomManager;
import psimulator.userInterface.SimulatorEditor.Tools.AbstractTool;
import psimulator.userInterface.SimulatorEditor.Tools.ManipulationTool;

/**
 *
 * @author Martin
 */
public class DrawPanelListenerStrategyDragMove extends DrawPanelListenerStrategy {

    private ManipulationTool manipulationTool;
    //
    private final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private final Cursor hndCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private final Point pp = new Point();
    //

    public DrawPanelListenerStrategyDragMove(DrawPanelInnerInterface drawPanel, UndoManager undoManager,
            ZoomManager zoomManager, MainWindowInnerInterface mainWindow, DataLayerFacade dataLayer) {
        super(drawPanel, undoManager, zoomManager, mainWindow, dataLayer);
    }

    @Override
    public void deInitialize() {
        drawPanel.repaint();
    }

    @Override
    public void setTool(AbstractTool tool) {
        this.manipulationTool = (ManipulationTool) tool;
    }

    @Override
    public void mousePressedLeft(MouseEvent e) {
        drawPanel.setCursor(hndCursor);
        pp.setLocation(e.getPoint());
    }

    @Override
    public void mouseDraggedLeft(MouseEvent e) {
        
        Point cp = e.getPoint();
        Point vp = vport.getViewPosition();
        //= SwingUtilities.convertPoint(vport,0,0,label);
        vp.translate(pp.x - cp.x, pp.y - cp.y);
        
        //if(vport)
        comp.scrollRectToVisible(new Rectangle(vp, vport.getSize()));

        //vport.setViewPosition(vp);
        pp.setLocation(cp);
    }

    @Override
    public void mouseReleasedLeft(MouseEvent e) {
        drawPanel.setCursor(defCursor);
        drawPanel.repaint();
    }
}
