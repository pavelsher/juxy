package org.tigris.juxy.builder;

import junit.framework.TestCase;

import javax.xml.transform.TransformerFactory;
import java.io.File;

public abstract class BaseTestTemplatesBuilder extends TestCase
{
    protected TemplatesBuilderImpl builder = null;
    private static TransformerFactory trFactory = TransformerFactory.newInstance();

    public void setUp()
    {
        builder = new TemplatesBuilderImpl(trFactory);
    }

    protected String getTestingXsltSystemId(String filePath)
    {
        return new File(filePath).toURI().toString();
    }
}
