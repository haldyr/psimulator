/*
 * UDELAT:
 * Spatne se pocita statistika - % paket loss HOTOVO
 */
package prikazy;

import datoveStruktury.*;
import java.util.ArrayList;
import java.util.List;
import pocitac.*;
import pocitac.apps.CommandShell.CommandShell;

/**
 * Predek vsech prikazu, ktere posilaji pakety. Definuje abstraktni metodu zpracujPaket.
 * @author Stanislav Řehák
 */
public abstract class AbstraktniPing extends AbstraktniPrikaz {

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
    protected List<Double> odezvy;
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
    

    public AbstraktniPing(AbstraktniPocitac pc, CommandShell kon, List<String> slova) {
        super(pc, kon, slova);
        odezvy = new ArrayList<Double>() {

            @Override
            public boolean add(Double e) {
                prijate++;
                return super.add(e);
            }
        };
    }

    /**
     * Vrati odkaz na pocitac, potreba pro prijimani paketu, jestli je opravdu pro me.
     * @return
     */
    public AbstraktniPocitac getPc(){
        return pc;
    }

    /**
     * Propocita min, avg, max, celkovyCas, ztrata.<br />
     * Pro spravnou funkci staci, aby konkretni pingy delali 3 veci: <br />
     * 1. pri odeslani icmp_req inkrementovat promennou odeslane <br />
     * 2. pri prijeti icmp_reply pridat do seznamu odezvy cas paketu. <br />
     * 3. pred dotazanim na statistiky zavolat tuto metodu aktualizujStatistiky() <br />
     */
    protected void aktualizujStatistiky() {
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
            ztrata = 100 - (int)( (float) prijate / (float) odeslane  * (float)100);
            uspech = 100 - ztrata;
        }
    }

    public abstract void zpracujPaket(Paket p);
}
