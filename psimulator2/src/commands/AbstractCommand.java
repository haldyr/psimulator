/*
 * created 6.3.2012
 */

package commands;

import device.Device;

/**
 *
 * @author Stanislav Rehak <rehaksta@fit.cvut.cz>
 */
public abstract class AbstractCommand {

	public final AbstractCommandParser parser;

	/**
	 * Konstruktor prikazu. Nedavat zadny slozity veci / na to je metoda
	 * @param parser
	 */
	public AbstractCommand(AbstractCommandParser parser) {
		this.parser = parser;
	}

	/**
	 * Samotny spusteni prikazu, nebude se to vsechno uz delat v konstruktoru.
	 * @return
	 */
	public abstract void runCommand();

	/**
	 * Predavani uzivatelskyho vstupu prave bezicimu commandu.
	 * @param input
	 */
	public abstract void catchUserInput(String input);

	public String nextWord() {
		return parser.nextWord();
	}

	public String nextWordPeek() {
		return parser.nextWordPeek();
	}

	protected int getRef() {
		return parser.ref;
	}

	protected Device getDevice(){
		return parser.device;
	}

	/**
	 * Zkratka pro vypisovani do shellu.
	 */
	protected void printLine(String s) {
		parser.getShell().printLine(s);
	}

	/**
	 * Zkratka pro vypisovani do shellu.
	 */
	protected void print(String s) {
		parser.getShell().print(s);
	}
//
//	protected TcpIpNetMod getNetMod(){
//		return parser.device.getNetworkModule();
//	}

}
