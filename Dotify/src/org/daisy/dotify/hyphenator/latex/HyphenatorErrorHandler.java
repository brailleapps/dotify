package org.daisy.dotify.hyphenator.latex;

import net.davidashen.util.ErrorHandler;

public class HyphenatorErrorHandler implements ErrorHandler {

    String tableFileName;
    public HyphenatorErrorHandler() {
        super();
        // TODO Auto-generated constructor stub
    }
    public HyphenatorErrorHandler(String fileName) {
        super();
        this.tableFileName = fileName; 
    }

    public void debug(String arg0, String msg) {
        System.err.println("HyphenatorErrorHandler#debug: "+msg+ " in "+this.tableFileName);

    }

    public void info(String msg) {
        System.err.println("HyphenatorErrorHandler#info: "+msg+ " in "+this.tableFileName);

    }

    public void warning(String msg) {
        System.err.println("HyphenatorErrorHandler#warning: "+msg+ " in "+this.tableFileName);

    }

    public void error(String msg) {
        System.err.println("HyphenatorErrorHandler#error: "+msg+ " in "+this.tableFileName);

    }

    public void exception(String arg0, Exception arg1) {
        // TODO Auto-generated method stub

    }

    public boolean isDebugged(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
