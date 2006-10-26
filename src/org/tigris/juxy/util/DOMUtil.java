package org.tigris.juxy.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Contains utility methods simplifying DOM creation and transformation.
 *
 * @author Pavel Sher
 */
public class DOMUtil
{
    /**
     * Creates new empty document.
     * @return new empty document.
    */
    public static Document newDocument()
    {
        createDocumentBuilderFactory();
        try {
            return docBuilderFactory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException ex) {
            logger.fatal("Failed to create document builder", ex);
        }

        return null;
    }

    /**
     * Parses specified document content to DOM.
     * @param documentContent content of a document to parse
     * @return parsed document
     * @throws SAXException
     */
    public static Document parse(String documentContent) throws SAXException
    {
        createDocumentBuilderFactory();
        ByteArrayInputStream bas = new ByteArrayInputStream(documentContent.getBytes());
        try
        {
            return docBuilderFactory.newDocumentBuilder().parse(bas);
        }
        catch (IOException ex) {
            logger.fatal("Unexpected error occured", ex);
        } catch (ParserConfigurationException ex) {
            logger.fatal("Failed to create document builder", ex);
        }

        return null;
    }

    /**
     * Prints DOM to output stream. Uses XSLT identity transform for conversion of DOM to string.
     * @param root root node of the document or document itself
     * @param os stream to output
     * @throws TransformerException
     */
    public static void printDOM(Node root, OutputStream os) throws TransformerException
    {
        identityTransform(root, new StreamResult(os));
    }

    /**
     * Logs document with specified title if current logging level is DEBUG.
     * @param title title of the document, used to identify document printed in debug
     * @param root root node of the document or document itself
     */
    public static void logDocument(String title, Node root)
    {
        if (logger.isDebugEnabled())
        {
            if (root == null)
            {
                logger.error("Attempt to debug null document, title = " + title);
                return;
            }

            ByteArrayOutputStream buf = new ByteArrayOutputStream(100);
            try
            {
                DOMUtil.printDOM(root, buf);
                logger.debug(title + "\n" + buf.toString());
            }
            catch (TransformerException e)
            {
                logger.debug("Failed to debug document", e);
            }
        }
    }

    /**
     * Returns inner node text.
     * @param node node which text to normalize
     * @return
     */
    public static String innerText(Node node) {
        StringBuffer result = new StringBuffer(200);

        DOMIterator it = new DOMIterator(node);
        while (it.hasNext()) {
            Node next = (Node) it.next();
            if (next.getNodeType() == Node.TEXT_NODE)
                result.append(next.getNodeValue());
        }

        return result.toString();
    }

    private static void identityTransform(Node root, Result res) throws TransformerException
    {
        createTransformerFactory();
        Transformer tr = trFactory.newTransformer();
        tr.setOutputProperty(OutputKeys.METHOD, "xml");
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        tr.transform(new DOMSource(root), res);
    }

    private static void createTransformerFactory()
    {
        if (trFactory == null)
            trFactory = TransformerFactory.newInstance();
    }

    private static void createDocumentBuilderFactory()
    {
        if (docBuilderFactory != null)
            return;

        try
        {
            logger.debug("creating document builder factory");
            docBuilderFactory = DocumentBuilderFactory.newInstance();
        }
        catch (FactoryConfigurationError er)
        {
            logger.fatal("Failed to create document builder factory");
            throw er;
        }

        docBuilderFactory.setNamespaceAware(true);
        docBuilderFactory.setValidating(false);
    }

    private static DocumentBuilderFactory docBuilderFactory = null;
    private static final Log logger = LogFactory.getLog(DOMUtil.class);
    private static TransformerFactory trFactory = null;
}
