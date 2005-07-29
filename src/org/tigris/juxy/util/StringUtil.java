package org.tigris.juxy.util;

/**
 * Various utility methods.
 *
 * @version $Revision: 1.1 $
 * @author Pavel Sher
 */
public class StringUtil
{
    /**
     * Method normalizes all spaces in the string including new line characters:
     * it replaces '\r' and '\n' chars with space char, then collapses sequences
     * of space characters to one character and finally trims spaces.
     * @param text the string to normalize
     * @return normalized string
     */
    public static String normalizeAll(String text)
    {
        if (text == null || text.length() == 0)
            return text;

        return collapseSpaces(text, SPACE_AND_CARRIAGE_CHARS).trim();
    }

    /**
     * Method collapses spaces: converts series of spaces to one space character and finally trims the specified string.
     * @param text text to collapse spaces in
     * @return text with collapsed spaces
     */
    public static String normalizeSpaces(String text) {
        if (text == null || text.length() == 0)
            return text;

        return collapseSpaces(text, SPACE_CHARS).trim();
    }

    private static String collapseSpaces(String text, char[] spaceCharacters)
    {
        if (text == null || text.length() == 0)
            return text;

        StringBuffer result = new StringBuffer();
        char[] textChars = text.toCharArray();

        boolean wasSpace = false;
        for (int i=0; i<textChars.length; i++)
        {
            if (!isCharacterInArray(textChars[i], spaceCharacters))
            {
                result.append(textChars[i]);
                wasSpace = false;
            }
            else if (!wasSpace)
            {
                result.append(' ');
                wasSpace = true;
            }
        }

        return result.toString();
    }

    /**
     * Method converts line feeds to Unix style.
     * The sequences of characters are processed as follows:
     * \rc -> \nc
     * \r\rc -> \n\nc
     * \r\nc -> \nc
     * \n\rc -> \nc
     * where 'c' is a non line feed character
     */
/*
    public static String toUnixLineFeeds(String text)
    {
        StringBuffer buf = new StringBuffer(30);
        boolean wasCR = false;
        boolean wasLN = false;

        char[] textChars = text.toCharArray();
        for (int i = 0; i<textChars.length; i++)
        {
            char current = textChars[i];
            boolean isCR = current == '\r';
            boolean isLN = current == '\n';
            if (isCR && !wasLN)
            {
                buf.append('\n');
                wasCR = true;
            }
            else if (isLN && !wasCR)
            {
                buf.append('\n');
                wasCR = false;
                wasLN = true;
            }
            else if (!isCR && !isLN)
            {
                buf.append(current);
                wasCR = false;
                wasLN = false;
            }
        }

        return buf.toString();
    }
*/

    /**
     * Trims spaces on each line of text. For example, result of the trimming of a text:
     * <code>"a text line   \n  a line of text \n"</code> will be <code>"a text line\na line of text\n"</code>.
     * <br>It is supposed that line feeds are in Unix style: \n.
     * @param text text to trim
     * @return trimmed string
     */
/*
    public static String trimSpacesOnEachLine(String text)
    {
        if (text == null || text.length() == 0)
            return text;

        StringBuffer result = new StringBuffer(50);
        StringBuffer tmpbuf = new StringBuffer(10);
        boolean startLine = true;
        char[] textChars = text.toCharArray();

        // trim spaces at start of each line
        for (int i=0; i<textChars.length; i++)
        {
            char textChar = textChars[i];
            if (!isCharacterInArray(textChar, SPACE_CHARS))
            {
                startLine = textChar == '\n';
                if (!startLine && tmpbuf.length() > 0)
                    result.append( tmpbuf.toString() );

                tmpbuf.setLength(0);
                result.append(textChar);
            }
            else if (!startLine)
            {
                tmpbuf.append(textChar);
            }
        }

        return result.toString();
    }
*/

/*
    private static String normalizeSpaces(String text)
    {
        StringBuffer buf = new StringBuffer(30);

        boolean spaceNeeded = false;
        boolean lastCharWasLineFeed = false;

        char[] textChars = text.toCharArray();
        for (int i=0; i<textChars.length; i++)
        {
            char current = textChars[i];

            if (!isCharacterInArray(current, SPACE_AND_CARRIAGE_CHARS))
            {
                boolean isLineFeed = current == '\n';
                if (spaceNeeded)
                {
                    // we should add space if current char is not line feed
                    // and buf length > 0
                    if (buf.length() > 0 && !isLineFeed)
                    {
                        buf.append(' ');
                    }
                }

                buf.append(current);
                lastCharWasLineFeed = isLineFeed;
                spaceNeeded = false;
            }
            else
            {
                spaceNeeded = !lastCharWasLineFeed;
            }
        }

        return buf.toString();
    }
*/


    /**
     * That method escapes symbols '<' and '&' in source string be replacing them with '&lt;' and '&amp;' correspondingly
     * @param source - the string being escaped
     * @return new string with replaced chars
     */
    public static String escapeXMLText(String source)
    {
        if (source == null || source.length() == 0)
            return source;

        // TODO: make this method more performance effective
        return replaceChar(replaceChar(source, '&', "&amp;"), '<', "&lt;");
    }

    public static String replaceCharByEntityRef(String source, char charToReplace)
    {
        return replaceChar(source, charToReplace, "&#" + (int)charToReplace + ";");
    }

    public static String escapeQuoteCharacter(String source) {
        return replaceChar(source, '"', "&quot;");
    }

    /**
     * Replaces all occurences of a character with specified string
     * @param source the string to replace within
     * @param charToReplace the character to replace
     * @param stringToReplaceWith the string to replace character with
     * @return string with all occurences of a character replaced by specified string
     */
    private static String replaceChar(String source, char charToReplace, String stringToReplaceWith)
    {
        StringBuffer output = new StringBuffer(source.length());
        char[] sourceChars = source.toCharArray();
        for (int i=0; i<sourceChars.length; i++)
        {
            if (sourceChars[i] != charToReplace)
                output.append(sourceChars[i]);
            else
                output.append(stringToReplaceWith);
        }

        return output.toString();
    }

    private static boolean isCharacterInArray(char c, char[] characters)
    {
        for (int i=0; i<characters.length; i++)
        {
            if (c == characters[i])
                return true;
        }

        return false;
    }

    public final static char[] SPACE_AND_CARRIAGE_CHARS = " \t\n\r\0".toCharArray();
    public final static char[] SPACE_CHARS = " \t".toCharArray();
}
