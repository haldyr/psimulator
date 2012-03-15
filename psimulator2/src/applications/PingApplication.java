/*
 * created 8.3.2012
 */

package applications;

import commands.ApplicationNotifiable;
import dataStructures.IcmpPacket;
import dataStructures.IpPacket;
import dataStructures.ipAddresses.IpAddress;
import device.Device;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import logging.Logger;
import logging.LoggingCategory;
import psimulator2.Psimulator;
import utils.Util;
import utils.Wakeable;

/**
 * Represents abstract Ping application. <br />
 *
 * @author Stanislav Rehak <rehaksta@fit.cvut.cz>
 * @author Tomas Pitrinec
 */
public abstract class PingApplication extends TwoThreadApplication implements Wakeable{

	protected IpAddress target;
	protected int count = 0;
	protected int size = 56; // default linux size (without header)
	protected int timeout = 10_000; // zrejme tedy v milisekundach
	protected Stats stats = new Stats();
	protected final ApplicationNotifiable command;

	/**
	 * kdyz nebude zadan, tak se pouzije vychozi systemova hodnota ze sitoveho modulu
	 */
	protected Integer ttl = null;
	/**
	 * Time to wait between sending to pings.
	 */
	protected int waitTime = 1_000;
	/**
	 * Key - seq <br />
	 * Value - timestamp in ms
	 */
	protected Map<Integer, Long> timestamps = new HashMap<>();

	protected boolean [] sent;
	protected boolean [] recieved;

	private boolean cekaSeNaBudik = false;	// mezi odeslanim a prijetim posledniho paketu je to true, jinak false
	private boolean atExitSkoncilo = false;






	public PingApplication(Device device, ApplicationNotifiable command) {
		super("ping", device);
		this.command = command;
	}

// metody pro predavani informaci z jinejch vlaken -------------------------------------------------------------------

	@Override
	public void wake() {
		if (!die) {
			Logger.log(this, Logger.DEBUG, LoggingCategory.PING_APPLICATION, "Byl jsem probuzen budikem a jdu zavolat svuj worker.", null);
			worker.wake();
		} else {
			Logger.log(this, Logger.DEBUG, LoggingCategory.PING_APPLICATION, "Byl jsem probuzen budikem ale nevolam svuj worker, protoze mam bejt mrtvej.", null);
		}
	}

	//pak je tady jeste receivePacket, ktera je podedena od Application



// metody na vyrizovani sitovejch pozadavku: --------------------------------------------------------------------------

	/**
	 * Dela totez co jinde u ruznejch modulu, tzn kontroluje buffery a vyrizuje je. Poprve je spustena k vyrizeni prvniho pozadavku nejakym jinym vlaknem.
	 */
	@Override
	public void doMyWork() {

		Logger.log(this, Logger.DEBUG, LoggingCategory.PING_APPLICATION, "Spustena metoda doMyWork.", null);

		IcmpPacket packet;

		while (!buffer.isEmpty()) {
			IpPacket p = buffer.remove(0);

			// zkouseni, jestli je ten paket spravnej:
			if (! (p.data instanceof IcmpPacket)) {
				Logger.log(this, Logger.WARNING, LoggingCategory.PING_APPLICATION, "Dropping packet, because PingApplication recieved non ICMP packet", p);
				continue;
			}

			// parovani k odeslanymu paketu, reseni duplikaci:
			packet = (IcmpPacket) p.data;
			Long sendTime = timestamps.get(packet.seq);
			timestamps.remove(packet.seq); // odstranim uz ulozeny
			// TODO: resit nejak lip duplikace paketu, zatim se to loguje:
			if (sendTime == null) {
				Logger.log(this, Logger.WARNING, LoggingCategory.PING_APPLICATION, "Dropping packet, because PingApplication doesn't expect such a PING reply "
						+ "(IcmpPacket with this seq="+packet.seq+" was never send OR it was served in a past)", p);
				continue;
			}

			// vsechno v poradku, paket se zpracuje:
			long delay = System.currentTimeMillis() - sendTime;
			if (delay <= timeout) { // ok, paket dorazil vcas
				Logger.log(this, Logger.DEBUG, LoggingCategory.PING_APPLICATION, "Dorazil mi ping.", packet);
				stats.odezvy.add(delay);
				stats.prijate++;
				handleIncommingPacket(p, packet, delay);
				recieved[packet.seq - 1] = true;	// prida se do prijatejch
			} else {
				Logger.log(this, Logger.DEBUG, LoggingCategory.PING_APPLICATION, "Dorazil mi ping, ale vyprsel timeout.", packet);
			}

			// reseni posledniho paketu:
			if(recieved[recieved.length - 1]){
				exit();
				cekaSeNaBudik = false;	// po prijmuti posledniho paketu se na budik neceka
			}
		}

		//
		if(cekaSeNaBudik){
			exit();
			cekaSeNaBudik = false;
		}
		Logger.log(this, Logger.DEBUG, LoggingCategory.PING_APPLICATION, "Opustena metoda doMyWork.", null);

	}



// metody na delani vlastni prace: ------------------------------------------------------------------------------------

	/**
	 * Tahleta metoda bezi ve vlastnim javovskym vlakne ty aplikace. Posila pingy.
	 */
	@Override
	public void run() {
		Logger.log(this, Logger.DEBUG, LoggingCategory.PING_APPLICATION, "Spustena metoda run.", null);
		int i = 0;
		while (i < count && !die) {	// jakmile bylo die nastaveno na true, tak uz se nic dalsiho neodesle
			int seq = i + 1;
			Logger.log(this, Logger.DEBUG, LoggingCategory.PING_APPLICATION, getName() + " posilam ping seq=" + seq, null);
			timestamps.put(seq, System.currentTimeMillis());
			sent[i] = true;
			transportLayer.icmphandler.sendRequest(target, ttl, seq, port);
			stats.odeslane++;

			if (seq != count) {	// po poslednim odeslanym paketu uz se neceka
				Util.sleep(waitTime);	// cekani
			} else {	// ale nastavi se budik:
				Psimulator.getPsimulator().budik.registerWake(this, timeout);
				cekaSeNaBudik = true;
			}
			i++;
		}
		Logger.log(this, Logger.DEBUG, LoggingCategory.PING_APPLICATION, "Konci metoda run.", null);
	}

// abstraktni metody, ktery je potreba doimplementovat v konkretnich pingach: ----------------------------------------

	/**
	 * Print stats and exits application.
	 */
	public abstract void printStats();

	/**
	 * Handles incomming packet: REPLY, TIME_EXCEEDED, UNDELIVERED.
	 *
	 * @param packet
	 * @param delay delay in miliseconds
	 */
	protected abstract void handleIncommingPacket(IpPacket p, IcmpPacket packet, long delay);

	/**
	 * Slouzi na hlasku o tom kolik ceho a kam posilam..
	 */
	protected abstract void startMessage();






// metody spousteny pri startovani a ukoncovani aplikace: -------------------------------------------------------------


	@Override
	protected synchronized void atExit() {
		Logger.log(this, Logger.DEBUG, LoggingCategory.PING_APPLICATION, "Zavolana metoda atExit. ", null);
		stats.countStats();
		printStats();
		Logger.log(this, Logger.DEBUG, LoggingCategory.PING_APPLICATION, "Ukoncena metoda atExit. ", null);
	}

	@Override
	protected void atKill(){
		command.applicationFinished();
	}


	@Override
	protected void atStart() {
		//kontrola cilovy adresy:
		if (target == null) {
			Logger.log(this, Logger.WARNING, LoggingCategory.GENERIC_APPLICATION, "PingApplication has no target! Exiting..", null);
			kill();
		}

		//kontrola, jestli mam port:
		if (getPort() == null) {
			Logger.log(this, Logger.WARNING, LoggingCategory.GENERIC_APPLICATION, "PingApplication has no port assigned! Exiting..", null);
			kill();
		}

		// inicialisovani poli (nemuze bejt v konstruktoru, protoze Standa nastavuje count az dyl):
		if (count > 0) {
			this.sent = new boolean[count];
			this.recieved = new boolean[count];
		}

		Logger.log(this, Logger.DEBUG, LoggingCategory.PING_APPLICATION, getName()+" atStart()", null);
		startMessage();

	}


// ostatni public metody, povetsinou gettry a settry: ----------------------------------------------------------------

	public void setCount(int count) {
		this.count = count;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setTarget(IpAddress target) {
		this.target = target;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public String toString(){
		return "ping_app "+PID;
	}




// statistiky: -------------------------------------------------------------------------------------------------------

	/**
	 * Class encapsulating packets statistics.
	 */
	public class Stats {

		/**
		 * pocet odeslanych paketu
		 */
		protected int odeslane = 0;
		/**
		 * pocet prijatych paketu
		 */
		protected int prijate = 0;
		/**
		 * Seznam odezev vsech prijatych icmp_reply.
		 */
		protected List<Long> odezvy = new ArrayList<>();
		/**
		 * Ztrata v procentech.
		 */
		protected int ztrata;
		/**
		 * Uspesnost v procentech.
		 */
		protected int uspech;
		/**
		 * pocet vracenejch paketu o chybach (tzn. typy 3 a 11)
		 */
		protected int errors;
		protected double min;
		protected double max;
		protected double avg;
		protected double celkovyCas; //soucet vsech milisekund

		/**
		 * Propocita min, avg, max, celkovyCas, ztrata.<br />
		 * Pro spravnou funkci staci, aby konkretni pingy delali 3 veci: <br />
		 * 1. pri odeslani icmp_req inkrementovat promennou odeslane <br />
		 * 2. pri prijeti icmp_reply pridat do seznamu odezvy cas paketu. <br />
		 * 3. pred dotazanim na statistiky zavolat tuto metodu countStats() <br />
		 */
		protected void countStats() {
			if (odezvy.size() >= 1) {
				min = odezvy.get(0);
				max = odezvy.get(0);

				double sum = 0;
				for (double d : odezvy) {
					if (d < min) {
						min = d;
					}
					if (d > max) {
						max = d;
					}
					sum += d;
				}

				avg = sum / odezvy.size();
				celkovyCas = sum;
			}
			if (odeslane > 0) {
				ztrata = 100 - (int) ((float) prijate / (float) odeslane * (float) 100);
				uspech = 100 - ztrata;
			}
		}
	}
}
