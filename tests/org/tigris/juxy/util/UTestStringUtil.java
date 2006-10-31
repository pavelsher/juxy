package org.tigris.juxy.util;

import junit.framework.TestCase;

public class UTestStringUtil extends TestCase {
  public void testNormalizeSpaces() {
    assertEquals("a text", StringUtil.normalizeSpaces("a text"));
    assertEquals("a text", StringUtil.normalizeSpaces("   \ta text  "));
    assertEquals("a text", StringUtil.normalizeSpaces(" a \t text"));
    assertEquals("a text", StringUtil.normalizeAll("a text  \n"));
    assertEquals("a text a text", StringUtil.normalizeAll("a text  \n  a  text"));
    assertEquals("a text", StringUtil.normalizeAll("\n\na text  \n  \0"));
  }

  public void testEscapeXMLText() {
    assertEquals(null, StringUtil.escapeXMLText(null));
    assertEquals("", StringUtil.escapeXMLText(""));
    assertEquals("&lt;", StringUtil.escapeXMLText("<"));
    assertEquals("&lt;&lt;", StringUtil.escapeXMLText("<<"));
    assertEquals("&amp;", StringUtil.escapeXMLText("&"));
    assertEquals("&amp;&amp;", StringUtil.escapeXMLText("&&"));
    assertEquals("&amp;&amp;amp;", StringUtil.escapeXMLText("&&amp;"));
    assertEquals("&amp;blabla&lt;", StringUtil.escapeXMLText("&blabla<"));
  }

  public void testReplaceCharByEntityRef() {
    assertEquals("gg&#39;g", StringUtil.replaceCharByEntityRef("gg'g", '\''));
    assertEquals("&#39;&#39;gg&#39;g", StringUtil.replaceCharByEntityRef("''gg'g", '\''));
  }

  public void testEscapeQuoteChar() {
    assertEquals("text with a couple of &quot;quotes&quot;",
        StringUtil.escapeQuoteCharacter("text with a couple of \"quotes\""));
  }
}
