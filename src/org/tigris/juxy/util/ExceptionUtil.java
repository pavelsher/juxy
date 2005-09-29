package org.tigris.juxy.util;

import org.xml.sax.SAXParseException;

import javax.xml.transform.TransformerException;
import javax.xml.transform.SourceLocator;

/**
 */
public class ExceptionUtil {
    public static String exceptionToString(TransformerException exception, boolean showSystemId) {
        StringBuffer message = new StringBuffer(20);
        if (exception.getLocator() != null) {
            SourceLocator locator = exception.getLocator();
            appendLocation(message, showSystemId ? locator.getSystemId() : null, locator.getLineNumber(), locator.getColumnNumber());
        }

        message.append(exception.getMessage());
        return message.toString();
    }

    public static String exceptionToString(SAXParseException exception, boolean showSystemId) {
        StringBuffer message = new StringBuffer(20);
        appendLocation(message, showSystemId ? exception.getSystemId() : null, exception.getLineNumber(), exception.getColumnNumber());

        message.append(exception.getMessage());
        return message.toString();
    }

    private static void appendLocation(StringBuffer message, String systemId, int lineNum, int colNum) {
        if (systemId != null)
            message.append(systemId);
        String line = "";
        String column = "";
        if (lineNum > 0)
            line = "line#: " + lineNum;
        if (colNum > 0)
            column = "col#: " + colNum;

        if (line.length() > 0 && systemId != null)
            message.append(", ");
        message.append(line);
        if (column.length() > 0)
            message.append(", ");
        message.append(column);

        if (systemId != null || lineNum > 0 || colNum > 0)
            message.append("; ");
    }
}
