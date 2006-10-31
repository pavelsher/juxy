package org.tigris.juxy;

import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;

/**
 */
public class TestUtil {
  public static InputSource makeInputSource(String systemId, String data) {
    InputSource src = new InputSource();
    src.setSystemId(systemId);
    src.setByteStream(new ByteArrayInputStream(data.getBytes()));
    return src;
  }
}
