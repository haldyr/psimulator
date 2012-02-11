package psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.GraphBuilder;

import java.util.ArrayList;
import java.util.List;
import psimulator.AbstractNetwork.NetworkCable;
import psimulator.AbstractNetwork.NetworkDevice;
import psimulator.AbstractNetwork.NetworkInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.AbstractHwComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.Cable;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.EthInterface;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Components.HwComponent;
import psimulator.userInterface.SimulatorEditor.DrawPanel.Graph.Graph;

/**
 *
 * @author Martin
 */
public class GraphBuilder extends AbstractGraphBuilder {
    //
    private Graph graph;

    public GraphBuilder() {
        //
        this.graph = new Graph();
    }

    @Override
    public void buildDevice(NetworkDevice device) {
        // new array of interfaces
        List<EthInterface> ethInterfaces = new ArrayList<EthInterface>();

        // devices interfaces
        List<NetworkInterface> interfaces = device.getInterfaces();

        // insert new interfaces to ethInterfaces list
        for (NetworkInterface interf : interfaces) {
            // create interface
            EthInterface ethInterface = new EthInterface(interf.getID(), interf.getInterfaceName(), interf.getIpAddress(), interf.getMacAddress());
            // insert interface into list
            ethInterfaces.add(ethInterface);
        }

        // create new component
        AbstractHwComponent hwComponent = new HwComponent(device.getID(), device.getHwType(), device.getName(), 
                ethInterfaces, device.getX(), device.getY());

        // add component to graph
        graph.addHwComponent(hwComponent);
    }

    @Override
    public void buildCable(NetworkCable networkCable) {
        // get cable id
        int cableId = networkCable.getID();
        
        // get IDs
        int component1id = networkCable.getInterface1().getDevice().getID();
        int eth1id = networkCable.getInterface1().getID();
        int component2id = networkCable.getInterface2().getDevice().getID();
        int eth2id = networkCable.getInterface2().getID();
        
        // get components by ID
        AbstractHwComponent component1 = graph.getAbstractHwComponent(component1id);
        AbstractHwComponent component2 = graph.getAbstractHwComponent(component2id);
        
        // get interfaces by ID
        EthInterface eth1 = component1.getEthInterface(eth1id);
        EthInterface eth2 = component2.getEthInterface(eth2id);
        
        // create new cable
        Cable cable = new Cable(cableId, networkCable.getHwType(), component1, component2, eth1, eth2, networkCable.getDelay());
                
        // add cable to graph
        graph.addCable(cable);
        
    }

    @Override
    public Graph getResult() {
        return graph;
    }
}
