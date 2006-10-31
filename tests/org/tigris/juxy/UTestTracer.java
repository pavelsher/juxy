package org.tigris.juxy;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * $Id: UTestTracer.java,v 1.2 2006-10-31 11:01:22 pavelsher Exp $
 *
 * @author Pavel Sher
 */
public class UTestTracer extends TestCase {
  private ByteArrayOutputStream byteStream;

  public void testTraceLevels() {
    Tracer tracer = new Tracer(stream());
    tracer.trace(1, 0, "systemId", "statement1");
    tracer.trace(2, 1, "systemId", "statement2");
    tracer.trace(3, 2, "systemId", "statement3");
    String[] lines = splitLines();
    assertEquals("1:\tstatement1", lines[1].trim());
    assertEquals("2:\t    statement2", lines[2].trim());
    assertEquals("3:\t        statement3", lines[3].trim());
  }

  public void testSameLine() {
    Tracer tracer = new Tracer(stream());
    tracer.trace(1, 1, "systemId", "instr1");
    tracer.trace(1, 1, "systemId", "instr2");
    tracer.trace(2, 1, "systemId", "instr3");
    String[] lines = splitLines();
    assertEquals("1:\t    instr1instr2", lines[1].trim());
    assertEquals("2:\t    instr3", lines[2].trim());
  }

  private String[] splitLines() {
    return byteStream.toString().split("\n");
  }

  private PrintStream stream() {
    byteStream = new ByteArrayOutputStream();
    return new PrintStream(byteStream);
  }
}
