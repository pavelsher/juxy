package org.tigris.juxy;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * $Id: Tracer.java,v 1.1 2005-08-17 17:54:52 pavelsher Exp $
 * <p/>
 * @author Pavel Sher
 */
public class Tracer {
    private String currentSystemid;
    private int lastLine = -1;
    private StringBuffer buf = new StringBuffer(20);
    private PrintStream traceOs;

    public Tracer(OutputStream traceOs) {
        this.traceOs = new PrintStream(traceOs);
    }

    public void trace(int line, int level, String systemId, String statement) {
        boolean sameSystemId = systemId.equals(currentSystemid);
        if (!sameSystemId) {
            traceOs.println("Started tracing of the stylesheet: " + systemId);
            lastLine = -1;
        }

        if (line == lastLine)
            traceOs.print(unescapeMessage(statement));
        else {
            if (lastLine != -1)
                traceOs.println();
            traceOs.print(messageAndLocation(line, level, unescapeMessage(statement)));
        }

        currentSystemid = systemId;
        lastLine = line;
    }

    private String unescapeMessage(String message) {
        return message.replaceAll("&#39;", "'");
    }

    public void endLogging() {
        traceOs.println();
    }

    private String messageAndLocation(int line, int level, String message) {
        buf.delete(0, buf.length());
        buf.append(line).append(":\t");
        for (int i=0; i<level*TABSTOP; i++) {
            buf.append(' ');
        }

        buf.append(message);
        return buf.toString();
    }

    private final static int TABSTOP = 4;
}
