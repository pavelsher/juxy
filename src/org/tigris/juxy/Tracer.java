package org.tigris.juxy;

import java.io.PrintStream;

/**
 * <p/>
 * @author Pavel Sher
 */
public class Tracer {
    private String currentSystemid;
    private int lastLine;
    private StringBuffer buf;
    private PrintStream traceOs;

    public Tracer(PrintStream traceOs) {
        this.traceOs = traceOs;
        this.buf = new StringBuffer(20);
        this.lastLine = -1;
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

    /**
     * Should be called when transformation is completed
     */
    public void stopTracing() {
        traceOs.println();
    }

    private String unescapeMessage(String message) {
        return message.replaceAll("&#39;", "'");
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
