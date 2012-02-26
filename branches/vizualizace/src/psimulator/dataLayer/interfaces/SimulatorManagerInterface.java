package psimulator.dataLayer.interfaces;

import psimulator.dataLayer.Enums.SimulatorPlayerCommand;
import psimulator.dataLayer.Simulator.EventTableModel;
import psimulator.dataLayer.Simulator.SimulatorEvent;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public interface SimulatorManagerInterface {

    public void addSimulatorEvent(SimulatorEvent simulatorEvent);
    public void deleteAllSimulatorEvents();

    public void doConnect();
    public void doDisconnect();
    
    public void connected();
    public void disconnected();
    public void connectingFailed();
    public void connectionFailed();
    
    // -------------------- SETTERS --------------------------
    public void setPlayerSpeed(int speed);
    
    public void setPlayerFunctionActivated(SimulatorPlayerCommand simulatorPlayerState);
    public void setConcreteRawSelected(int row);
    
    public void setRecordingActivated();
    public void setRecordingDeactivated();
    
    public void setRealtimeActivated();
    public void setRealtimeDeactivated();
    
    public void setPlayingActivated();
    public void setPlayingStopped();
    
    public void setPacketDetails(boolean activated);
    public void setNamesOfDevices(boolean activated);
    
    public void setNewPacketRecieved();
    
    // -------------------- GETTERS --------------------------
    public EventTableModel getEventTableModel();
    public boolean isConnectedToServer();
    //
    public int getSimulatorPlayerSpeed();
    public boolean isRecording();
    public boolean isPlaying();
    public boolean isRealtime();
    
    public int getCurrentPositionInList();
    public int getListSize();
    
    public boolean hasEvents();
    
    public void moveToNextEvent();
    public void moveToEvent(final int index);
    
    public SimulatorEvent getSimulatorEventAtCurrentPosition();
     
    public boolean isTimeReset();
    
}