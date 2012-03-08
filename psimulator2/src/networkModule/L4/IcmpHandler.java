/*
 * created 5.3.2012
 *
 * TODO: dodelat seq
 */

package networkModule.L4;

import dataStructures.IcmpPacket;
import dataStructures.IcmpPacket.Code;
import dataStructures.IcmpPacket.Type;
import dataStructures.IpPacket;
import dataStructures.ipAddresses.IpAddress;
import logging.Loggable;
import logging.Logger;
import logging.LoggingCategory;
import networkModule.L3.IPLayer;
import networkModule.TcpIpNetMod;

/**
 * Handles creating and sending ICMP packets.
 *
 * @author Stanislav Rehak <rehaksta@fit.cvut.cz>
 */
public class IcmpHandler implements Loggable {

	private final TcpIpNetMod netMod;
	private final IPLayer ipLayer;
	private final TransportLayer transportLayer;

	public IcmpHandler(TcpIpNetMod netMod) {
		this.netMod = netMod;
		this.ipLayer = netMod.ipLayer;
		this.transportLayer = netMod.transportLayer;
	}

	public void handleReceivedIcmpPacket(IpPacket packet) {
		// tady
		IcmpPacket p = (IcmpPacket) packet.data;

		switch (p.type) {
			case REQUEST:
				// odpovedet
				IcmpPacket reply = new IcmpPacket(IcmpPacket.Type.REPLY, IcmpPacket.Code.DEFAULT, p.id, p.seq);
				ipLayer.handleSendPacket(reply, packet.src);

				break;
			case REPLY:
			case TIME_EXCEEDED:
			case UNDELIVERED:
				// predat aplikacim
				transportLayer.forwardPacketToApplication(packet, p.id);
				break;
			default:
				Logger.log(this, Logger.WARNING, LoggingCategory.TRANSPORT, "Neznamy typ ICMP paketu:", packet);
		}
	}

	/**
	 * Sends ICMP message TimeToLiveExceeded to a given IpAddress.
	 * @param dst message target
	 * @param packet for some additional information only.
	 */
	public void sendTimeToLiveExceeded(IpAddress dst, IpPacket packet) {
		send(packet, dst, IcmpPacket.Type.TIME_EXCEEDED, IcmpPacket.Code.DEFAULT);
	}

	/**
	 * Sends ICMP message Destination Host Unreachable to a given IpAddress.
	 * @param dst message target
	 * @param packet for some additional information only.
	 */
	public void sendDestinationHostUnreachable(IpAddress dst, IpPacket packet) {
		send(packet, dst, IcmpPacket.Type.UNDELIVERED, IcmpPacket.Code.HOST_UNREACHABLE);
	}

	/**
	 * Sends ICMP message Destination Network Unreachable to a given IpAddress.
	 * @param dst message target
	 * @param packet for some additional information only.
	 */
	public void sendDestinationNetworkUnreachable(IpAddress dst, IpPacket packet) {
		send(packet, dst, IcmpPacket.Type.UNDELIVERED, IcmpPacket.Code.NETWORK_UNREACHABLE);
	}

	@Override
	public String getDescription() {
		return netMod.getDevice().getName() + ": IcmpHandler";
	}

	/**
	 * Returns L4 data of given packet or null.
	 * @param packet
	 * @return
	 */
	private IcmpPacket getIcmpPacket(IpPacket packet) {
		if (packet.data != null) {
			return (IcmpPacket) packet.data;
		}
		return null;
	}

	/**
	 * Sends ICMP packet with given type, code to dst. <br />
	 *
	 * @param packet
	 * @param dst
	 * @param type
	 * @param code
	 */
	private void send(IpPacket packet, IpAddress dst, Type type, Code code) {
		IcmpPacket icmp = getIcmpPacket(packet);
		IcmpPacket p;
		if (icmp != null) {
			p = new IcmpPacket(type, code, icmp.id, icmp.seq);
		} else {
			p = new IcmpPacket(type, code);
		}
		Logger.log(this, Logger.INFO, LoggingCategory.NET, "Posilam "+type+" "+code+" na: "+dst, p);
		ipLayer.handleSendPacket(p, dst);
	}
}


