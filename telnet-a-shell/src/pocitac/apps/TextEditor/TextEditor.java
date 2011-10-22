/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pocitac.apps.TextEditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import pocitac.AbstraktniPocitac;
import pocitac.apps.TerminalApplication;
import telnetd.io.BasicTerminalIO;
import telnetd.io.terminal.ColorHelper;
import telnetd.io.toolkit.Editarea;
import telnetd.io.toolkit.Statusbar;
import telnetd.io.toolkit.Titlebar;

/**
 *
 * @author zaltair
 */
public class TextEditor extends  TerminalApplication {

    public TextEditor(BasicTerminalIO terminalIO, AbstraktniPocitac pocitac) {
        super(terminalIO, pocitac);
    }

    @Override
    public int run() {
        try {
             terminalIO.eraseScreen();
          terminalIO.homeCursor();
          //myio.flush();
          Titlebar tb2 = new Titlebar(terminalIO, "title 1");
          tb2.setTitleText("TextEditor");
          tb2.setAlignment(Titlebar.ALIGN_LEFT);
          tb2.setBackgroundColor(ColorHelper.BLUE);
          tb2.setForegroundColor(ColorHelper.YELLOW);
          tb2.draw();

          Statusbar sb2 = new Statusbar(terminalIO, "status 1");
          sb2.setStatusText("Status bar");
          sb2.setAlignment(Statusbar.ALIGN_LEFT);
          sb2.setBackgroundColor(ColorHelper.BLUE);
          sb2.setForegroundColor(ColorHelper.YELLOW);
          sb2.draw();

          terminalIO.setCursor(2, 1);

          Editarea ea = new Editarea(terminalIO, "edit area", terminalIO.getRows() - 2, Integer.MAX_VALUE);

          ArrayList<String> lines = new ArrayList<String>(20);
          lines.add("a");
          lines.add("b");
          lines.add("c");
          lines.add("d");
          lines.add("e");
          lines.add("f");
          lines.add("g");
          lines.add("h");
          lines.add("ch");
          lines.add("i");
          lines.add("j");
          lines.add("k");
          lines.add("l");
          lines.add("m");
          lines.add("n");
          lines.add("o");
          
          ea.setValue(lines);

          ea.draw();
          terminalIO.flush();
          ea.run();

          Main.Main.debug("TextEditor return this value:" + terminalIO.CRLF + ea.getValue());


        } catch (IOException ex) {
            Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;


    }

    

  

}
