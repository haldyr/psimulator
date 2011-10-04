/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac.apps.CommandShell.prikazy;

import pocitac.apps.CommandShell.prikazy.AbstraktniPing;
import datoveStruktury.IpAdresa;
import datoveStruktury.Paket;
import java.util.List;
import pocitac.AbstraktniPocitac;
import pocitac.apps.CommandShell.CommandShell;

/**
 * Předek traceroute na linuxu i ciscu.
 * @author Tomáš Pitřinec
 */
public abstract class AbstraktniTraceroute extends AbstraktniPing {

    protected IpAdresa adr;
    protected int navrKod = 0;
    protected int maxTtl = 30; //to je to, co se vypisuje jako hops max
    protected double interval = 0.1; //interval mezi odesilanim v sekudach
    /**
     * Stav vykonavani prikazu:
     * 0 - ceka se na pakety<br />
     * 1 - vratil se paket od cilovyho pocitace - skoncit<br />
     * 2 - byl timeout - vypisovat hvezdicky a skoncit<br />
     * 3 - vratilo se host unreachable nebo net unreachable<br />
     */
    protected int stavKonani = 0;

    public AbstraktniTraceroute(AbstraktniPocitac pc, CommandShell kon, List<String> slova) {
        super(pc, kon, slova);
    }

    protected abstract void parsujPrikaz();

    @Override
    public abstract void zpracujPaket(Paket p);

    @Override
    protected abstract void vykonejPrikaz();

    /**
     * Zarovnava zleva mezerami do maximalni dylky.
     * Kdyz je ret delsi nez dylka, tak vrati nezmenenej retezec.
     * @param ret
     * @param dylka
     * @return
     */
    protected static String zarovnejZLeva(String ret, int dylka) {
        //if (ret.length() >= dylka) return ret;
        int dorovnat = dylka - ret.length();
        String s = "";
        for(int i=0;i<dorovnat;i++){
            s += " ";
        }
        return s+ret;
    }
}
