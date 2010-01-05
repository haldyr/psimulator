/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author neiss
 */
public class Konsole extends Thread{
    Socket s;
    AbstractPocitac pocitac;
    ParserPrikazu parser;
    int cislo; //poradove cislo vlakna, jak je v tom listu, spis pro ladeni

    public Konsole(Socket s,AbstractPocitac pc, int cislo){
        this.s=s;
        pocitac=pc;
        parser=pocitac.parser;
        this.cislo=cislo;
        this.start();
    }


    /**
     * metoda nacita z proudu dokud neprijde \ret\n
     * Prevzata z KarelServer, ale pak jsem ji stejne celou prepsal.
     * @param in
     * @return celej radek do \r\n jako string. kterej to \r\n uz ale neobsahuje
     */
    public String ctiRadek(BufferedReader in){
        String ret = ""; //radek nacitany
        char z;
        for(;;){
            try {
                z = (char) in.read();
                ret+=z;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if(ret.length()>=2){ //tzn. uz je to dost dlouhy na to, aby tam mohlo bejt \r\n
                if ( ret.charAt(ret.length()-2)=='\r' && ret.charAt(ret.length()-1)=='\n'){ //kdyz uz jsou ukoncovaci znaky
                    ret=ret.substring(0, ret.length()-2);
                    break;
                }
            }

        }
        return ret;
    }

    /**
     * Metoda na posilani do produ vystupniho a zaroven na standartni vystup
     * Prevzata z KarelServer
     * @param out
     * @param ret
     * @throws java.io.IOException
     */
    public void posli(OutputStream out,String ret) throws IOException{
        out.write((ret + "\r\n").getBytes());
        System.out.println("(socket c. "+cislo+" posilam): "+ret);
    }

    @Override
    public void run(){
        OutputStream out = null;
        BufferedReader in = null;
        String radek;
        boolean ukoncit;
        System.out.println("vlakno c. "+cislo+" startuje");

        try {//vsechno je hozeny do ochrannyho bloku
            in = new BufferedReader(new InputStreamReader(s.getInputStream( ) ) );
            out = s.getOutputStream();
            ukoncit=false;
            posli(out,"prompt");
            while(! ukoncit ) {
                radek = ctiRadek(in);
                System.out.println("(klient c. "+cislo+" poslal): " + radek);
                System.out.println("dylka predchoziho radku: "+radek.length());
                //posli(out,radek);
            }

        } catch ( Exception ex ) {
            ex.printStackTrace();
            System.err.println( "nastala nejaka chyba" );
        } finally {
            System.out.println("Ukoncuji vlakno a socket c. "+cislo);
            try { //ten socket sice urcite existuje, ale java to jinak nedovoli
                s.close();
            } catch (IOException ex) { ex.printStackTrace();}
        }

    }

}