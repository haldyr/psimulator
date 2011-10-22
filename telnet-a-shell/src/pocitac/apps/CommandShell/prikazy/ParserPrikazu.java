/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pocitac.apps.CommandShell.prikazy;

import pocitac.apps.CommandShell.prikazy.AbstraktniPrikaz;
import pocitac.apps.CommandShell.prikazy.Abstraktni;
import java.util.ArrayList;
import pocitac.*;
import pocitac.apps.CommandShell.CommandShell;

/**
 * Abstraktní parser příkazů. 
 * @author Tomáš Pitřinec & Stanislav Řehák
 */
public abstract class ParserPrikazu extends Abstraktni {

    protected String radek;
    protected AbstraktniPocitac pc;
    protected CommandShell kon;

    /**
     * Konstruktor. Kazda CommandShell si uchovava prave jeden parser. Tenhle konstruktor se tedy vola
     * v konstruktoru CommandShell.
     * @param pc
     * @param kon
     */
    public ParserPrikazu(AbstraktniPocitac pc, CommandShell kon) {
        super(new ArrayList<String>());
        this.pc = pc;
        this.kon = kon;
    }

    /**
     * Prijme a zpracuje vstupni string od klienta. Ten se pak metodou rozsekej() rozseka na jednotlivy slova.
     * Pak se testuje, jestli prvni slovo je nazev nejakyho podporovanyho prikazu, jestlize ne, tak se vypise
     * "command not found", jinak se preda rizeni tomu spravnymu prikazu.
     * @param s
     */
    public abstract void zpracujRadek(String s);

    /**
     * Tato metoda rozseka vstupni string na jednotlivy slova (jako jejich oddelovac se bere mezera)
     * a ulozi je do seznamu slova, ktery dedi od Abstraktni.
     * @autor Stanislav Řehák
     */
    protected void rozsekej() {
        slova=new ArrayList<String>();
        radek = radek.trim(); // rusim bile znaky na zacatku a na konci
        String[] bileZnaky = {" ", "\t"};
        for (int i = 0; i < bileZnaky.length; i++) { // odstraneni bylych znaku
            while (radek.contains(bileZnaky[i] + bileZnaky[i])) {
                radek = radek.replace(bileZnaky[i] + bileZnaky[i], bileZnaky[i]);
            }
        }
        String[] pole = radek.split(" ");
        for (String s : pole) {
            slova.add(s);
        }
    }

    /**
     * V teto metode je se kontroluje, zda neprisel nejaky spolecny prikaz, jako napr. save ci v budoucnu jeste jine.
     * @return vrati true, kdyz konkretni parser uz nema pokracovat dal v parsovani (tj. jednalo se o spolecny prikaz)
     * @autor Stanislav Řehák
     */
    protected boolean spolecnePrikazy(boolean debug) {
        AbstraktniPrikaz prikaz;

        if (slova.get(0).equals("uloz") || slova.get(0).equals("save")) {
            prikaz = new Uloz(pc, kon, slova);
            return true;
        }
        if (debug) {
            if (slova.get(0).equals("nat")) {
                kon.printWithDelay(pc.natTabulka.vypisZaznamyDynamicky(), 10);
                kon.printLine("______________________________________________");
                kon.printWithDelay(pc.natTabulka.vypisZaznamyCisco(), 10);
                return true;
            }
        }
        return false;
    }
}
