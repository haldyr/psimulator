package psimulator.AbstractNetwork;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Martin
 */
@XmlRootElement
public class Network implements Serializable {
    //private int ID;
    //private String name;

    private NetworkCounter counter;
    private LinkedHashMap<Integer, NetworkCable> cables;
    private LinkedHashMap<Integer, NetworkDevice> devices;
    // needed for Graph restore from Network - fast lookup
    private LinkedHashMap<Integer, NetworkInterface> interfacesMap;

    public Network(/*
             * int ID, String name
             */) {
        //this.ID = ID;
        //this.name = name;

        this.cables = new LinkedHashMap<Integer, NetworkCable>();
        this.devices = new LinkedHashMap<Integer, NetworkDevice>();

        this.interfacesMap = new LinkedHashMap<Integer, NetworkInterface>();
    }

    public void save(String fileName) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Network.class);

        File file = new File(fileName);

        Marshaller marsh = context.createMarshaller();

        System.out.println(this.counter.getNextID());

        // nastavení formátování
        marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        marsh.marshal(this, file);

    }

    public static Network load(String fileName) throws JAXBException {

        File file = new File(fileName);

        Network network = null;

        JAXBContext context = JAXBContext.newInstance(Network.class);
        
        Unmarshaller unmarsh = context.createUnmarshaller();

        network = (Network) unmarsh.unmarshal(file);

        return network;


    }

    public void addDevice(NetworkDevice device) {
        devices.put(device.getID(), device);
    }

    public void addCable(NetworkCable cable) {
        cables.put(cable.getID(), cable);
    }

    public void addNetworkInterface(NetworkInterface networkInterface) {
        interfacesMap.put(networkInterface.getID(), networkInterface);
    }

    public NetworkInterface getNetworkInterface(int id) {
        return interfacesMap.get(id);
    }

    @XmlElement(name = "cable")
    public LinkedHashMap<Integer, NetworkCable> getCables() {
        return cables;
    }

    @XmlElement(name = "device")
    public LinkedHashMap<Integer, NetworkDevice> getDevices() {
        return devices;
    }

    @XmlElement(name = "counter")
    public NetworkCounter getCounter() {
        return counter;
    }

    public void setCounter(NetworkCounter counter) {
        this.counter = counter;
    }

    // ---------------------------------------------------------------
    // Martin Svihlik tyto metody nepotrebuje:
    public void setCables(LinkedHashMap<Integer, NetworkCable> cables) {
        this.cables = cables;
    }

    public void setDevices(LinkedHashMap<Integer, NetworkDevice> devices) {
        this.devices = devices;
    }
    /*
     * public int getID() { return ID; }
     *
     * public void setID(int ID) { this.ID = ID; }
     *
     * public String getName() { return name; }
     *
     * public void setName(String name) { this.name = name; }
     */
}
