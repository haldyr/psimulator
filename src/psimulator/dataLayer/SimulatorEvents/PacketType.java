package psimulator.dataLayer.SimulatorEvents;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
 */
public enum PacketType {
    TCP,        // green
    UDP,        // blue
    ICMP,       // gray
    ARP,        // yellow
    GENERIC;    // pink
}
