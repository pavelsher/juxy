Juxy is a library for unit testing XSLT stylesheets from Java. It is best suited for the projects where
both Java and XSLT are used simultaneously.

Juxy features include:
* Ability to invoke individual xsl templates and pass them any parameters (you can either call or apply templates with and without modes).
* Ability to setup stylesheet parameters and global variables.
* Ability to setup current node before transformation.
* Integration with JUnit (Juxy provides its own TestCase class simplifying testing with JUnit, however, JUnit is not required for writing tests, you can use any testing framework that you like).
* Transparent support for document fragments returned as transformation result (result of the called template can have more than one root node, Juxy will process such cases transparently for you).
* Easy result verification with sophisticated XML assertion mechanism.
* Easy validation using W3C XML Schema or a number of XPath assertions (you can use either Jaxen or JAXP XPath         engine, Juxy will detect their presence automatically).
* Ability to trace execution of the sylesheets instructions.
* Support for custom URIResolver.
* Ant task for batch verification of XSLT stylesheets.

## Examples

Please refer to [javadoc](http://teamcity.jetbrains.com/repository/download/bt24/.lastPinned/doc/javadoc/index.html?guest=1) for detailed API description.

You can use Juxy alone, in conjunction with JUnit, or with any other testing framework that you like.
To become familiar with Juxy, in the first example, we will show how to use Juxy without JUnit.

We will assume here, that there is a stylesheet with a template taking a list of xml tags and transforming it to a comma separated list of strings. For example, let it be a transformation of something like:

<pre>
    &lt;list>
        &lt;item>item 1&lt;/item>
        &lt;item>item 2&lt;/item>
        ...
        &lt;item>item n&lt;/item>
    &lt;/list>
</pre>

to the string: item 1, item 2, ... item n.

<pre>
/* First of all we must obtain a Runner instance. Runner is a class that
   actually invokes transformation and returns the result. */
Runner runner = RunnerFactory.newRunner();

/* To setup what and how to transform we must create RunnerContext
   object, which is obtained from the Runner instance. We should
   provide RunnerContext with stylesheet system id (i.e. with a path
   to the stylesheet we are going to test). */
RunnerContext context = runner.newRunnerContext("stylesheet.xsl");

/* Next we set input document into the RunnerContext. */
context.setDocument("" +
        "&lt;list>" +
        "	&lt;item>item 1&lt;/item>" +
        "	&lt;item>item 2&lt;/item>" +
        "	&lt;item>item 3&lt;/item>" +
        "&lt;/list>");

/* Now we are ready to start transformation. We run it by invoking
   method applyTemplates() which works similar to the XSLT
   instruction: &lt;xsl:apply-templates/>.
   Result of the transformation is returned as a DOM Node object.
   In fact this is a DOM Document object, which in this case will
   contain only one TextNode node. */
Node result = runner.applyTemplates();

/* To retrieve text from the returned DOM Node we can create
   XPath expression. We should use XPathFactory for that. */
XPathExpr xpath = XPathFactory.newXPath("text()");

/* Now we are ready to evaluate the expression. */
String resultText = xpath.toString(result);
</pre>

Note: DOM Document in our case actually contains not well formed XML document,
which consists of one text node only. Usually it is impossible to create such a document,
however Juxy applies a simple workaround for that: internally Juxy holds transformation results
in a DocumentFragment node, but from the Runner you will receive a proxy object, which will behave like a Document node containing all the child nodes of the DocumentFragment node.

With JUnit it is possible to write more compact tests. For that you should extend your test cases from
JuxyTestCase. Take a look how the same test looks with JUnit:

<pre>
/* We should extend our test case from JuxyTestCase. */
public class SampleTestCase extends JuxyTestCase {

    public void testListTransformation() {
    /* We should not store context in the local variable, it will be stored
       automatically within the test case itself. */
       newContext("stylesheet.xsl");

    /* To obtain current RunnerContext we can call the context() method. */
       context().setDocument("" +
            "&lt;list>" +
            "	&lt;item>item 1&lt;/item>" +
            "	&lt;item>item 2&lt;/item>" +
            "	&lt;item>item 3&lt;/item>" +
            "&lt;/list>");

    /* We do not need to know about Runner here. We can simply invoke applyTemplates()
       method. Runner will be instantiated automatically within JuxyTestCase. */
       Node result = applyTemplates();

    /* With JuxyTestCase verification of the result is much simpler. */
       xpathAssert("text()", "item 1, item 2, item 3").eval(result);
    }

}
</pre>

Juxy also supports an XML format for tests. For example:

<pre>
&lt;test name="MoreThanOneElementInTheList_ApplyTemplates"&gt;
  &lt;document select="/list"&gt;
    &lt;list&gt;
       &lt;item&gt;first item&lt;/item&gt;
       &lt;item&gt;second item&lt;/item&gt;
       &lt;item&gt;third item&lt;/item&gt;
    &lt;/list&gt;
  &lt;/document&gt;
  &lt;apply-templates select="/list"/&gt;
  &lt;assert-equals&gt;
    &lt;expected&gt;first item, second item, third item&lt;/expected&gt;
  &lt;/assert-equals&gt;
&lt;/test&gt;
</pre>


More samples are available in the Juxy [samples](https://github.com/pavelsher/juxy/tree/master/samples) directory.

### How does it work?

Juxy works by dynamically creating XSLT stylesheet. From that stylesheet the tested stylesheet is imported via
&lt;xsl:import/> instruction, thus all its templates and variables automatically get lower priority, so we can
easily redefine them.

Any variables and parameters specified in the RunnerContext will be placed in the generated stylesheet. If current
node was specified, then corresponding &lt;xsl:for-each/> is added to that stylesheet.

Thus when call or apply templates method is invoked, Juxy generates new XSLT stylesheet (it will be generated
as DOM Document), places there specified variables and parameters, and inserts template matching root node ("/").
In this template the required &lt;xsl:call-template/> or &lt;xsl:apply-templates/> instructions are placed.
If current node was specified then &lt;xsl:for-each/> instruction will be added as their parent. To avoid endless loop Juxy will not insert apply-templates instruction if current node was set to "/".

Because Juxy does not use any XSLT processor specific features it should work with any XSLT processor supporting
JAXP/TRaX API. For now it is known to work with [Apache Xalan](http://xml.apache.org/xalan-j/) and
[Saxon](http://saxon.sourceforge.net/") (both Saxon 6.5.X and Saxon 8.X are supported). XSLT 2.0
templates are also supported.

## Other features

### Other ways to start transformation

In the examples above we invoked the transformation by calling method Runner.applyTemplates().
But it is also possible to get the results of the transformation produced by individual templates.
You can apply individual templates by specifying XPath expression and mode, or you can call templates by their name. Refer to the Runner's Javadoc for more details.

Often template requires current node to be set up. By default, it is a root node of the input document,
but you can set your own current node using method RunnerContext.setCurrentNode(XPathExpr).
RunnerContext also allows you to setup global transformation parameters, global variables, parameters for the template and namespaces.

### Assertions

With XPath you can apply different assertions on the transformation result. However, in most cases it is easier to compare XML fragments. You can do this with XMLComparator.assertXMLEquals(String, Node) method, for example: 
<pre>XMLComparator.assertXMLEquals("&lt;p>expected text&lt;/p>", result);</pre>

The assertXMLEquals method will compare expected document and the result node by node. In case of any differences you will get an output similar to:

<pre>
Documents differ, expected document:
&lt;p>expected text&lt;/p>

Actual document:
&lt;p>actual text&lt;/p>
</pre>

If you are using JuxyTestCase, this method is available directly from this class. JuxyTestCase also provides a couple of methods allowing to normalize text, i.e. to collapse spaces and remove trailing spaces. They are useful if spaces between words are insignificant for you.

### Tracing

In some situations it is hard to understand why transformation does not work as you expected. For that purposes Juxy provides you with an ability to trace execution of the XSLT instructions. You can enable or disable tracing by calling methods JuxyTestCase.enableTracing and JuxyTestCase.disableTracing correspondingly (or you can call these methods from the Runner instance). The tracing output looks like:

<pre>
Tracing of the stylesheet file://some/path/stylesheet.xsl started
2:  &lt;xsl:template match="/">
3:      &lt;xsl:for-each select="//*">
4:          &lt;xsl:value-of select=".">
3:      &lt;xsl:for-each select="//*">
4:          &lt;xsl:value-of select=".">
</pre>

In the tracing output you can see both, the line numbers and corresponding XSLT instructions.
There are limitations, and the most essential is that for now only instructions within templates and
template instructions itself are traced. Global variables, parameters and keys are not traced.

Another limitation is that currently tracing is supported for Saxon and Xalan only. Xalan XSLTC is not supported.

### XSLT Verification

Starting from version 0.7 Juxy package contains Ant task which performs syntax verification of a number of stylesheets at once. This task tries to compile every stylesheet (by creating JAXP Transformer object) and reports all errors and warnings.

Juxy verifier task checks parent stylesheets only, i.e. the stylesheets which are not included or imported from any other stylesheets, because included stylesheets can use global variables and parameters defined in the parent and might not be compiled without parent. Juxy will automatically filter out all imported and included stylesheets from the specified file set.

An example of task usage:

<pre>
&lt;path id="juxy_ant.task.path">
    &lt;pathelement path="xalan.jar"/>
    &lt;pathelement path="juxy_ant.jar"/>
&lt;/path>

&lt;target name="verify-xsl">
    &lt;taskdef resource="juxy_ant.properties"
             classpathref="juxy_ant.task.path"/>

    &lt;verifier>
        &lt;fileset dir="xsl">
            &lt;include name="**/*.xsl"/>
        &lt;/fileset>
    &lt;/verifier>
&lt;/target>
</pre>

Task supports the following attributes:
<table class="ant_attributes" cellpadding="0" cellspacing="0">
<tr>
    <th class="th">Attribute</th>
    <th class="th">Description</th>
    <th class="th">Required</th>
</tr>
<tr>
    <td class="td">dir</td>
    <td class="td">Directory where to search for xsl files</td>
    <td class="td">No, if nested &lt;fileset> specified.</td>
</tr>
<tr>
    <td class="td">includes</td>
    <td class="td">Comma- or space-separated list of files (may be specified using wildcard patterns) that must be included.</td>
    <td class="td">No</td>
</tr>
<tr>
    <td class="td">excludes</td>
    <td class="td">Comma- or space-separated list of files (may be specified using wildcard patterns) that must be excluded.</td>
    <td class="td">No</td>
</tr>
<tr>
    <td class="td">failonerror</td>
    <td class="td">Whether to fail on first error or not. True by default.</td>
    <td class="td">No</td>
</tr>
</table>

The following nested elements are allowed:

* standard Ant &lt;fileset/>
* &lt;catalog/> this element if appeared turns on XML catalogs resolution.

The &lt;catalog/> element has the following attributes:

<table class="ant_attributes" cellpadding="0" cellspacing="0">
<tr>
    <th class="th">Attribute</th>
    <th class="th">Description</th>
    <th class="th">Required</th>
</tr>
<tr>
    <td class="td">catalogfiles</td>
    <td class="td">Comma separated paths to the catalog files.</td>
    <td class="td">Yes</td>
</tr>
</table>

### Logging

Juxy produces some log messages which might be helpful for discovering problems. For logging purposes Juxy uses Jakarta commons-logging API, so commons-logging jars must be in the classpath.

## Requirements

Juxy requires Java 1.4 and will not work with earlier versions. A number of required libraries depends on a version of Java you are using.

For Java 1.4 the following libraries are required:

* juxy.jar</li>
* [commons-logging](http://jakarta.apache.org/commons/logging/)
* [Jaxen](http://jaxen.codehaus.org/)
* [Xerces 2](http://xml.apache.org/xerces2-j/)
* JAXP compliant XSLT engine

For Java 1.5 you should have:

* juxy.jar</li>
* [commons-logging](http://jakarta.apache.org/commons/logging/)
* JAXP compliant XSLT engine</li>

A couple of words about XML parser, XPath and XSLT engine.

Juxy has been tested with [Xerces 2](http://xml.apache.org/xerces2-j/) XML parser which is included now into the Java 1.5.
Juxy does not depend on any Xerces specific functionality, so it should work with other JAXP compliant XML parsers, which support SAX 2 Core, DOM Level 2 Core and Traversal and Range.

For XPath expressions Juxy supports both [Jaxen](http://jaxen.codehaus.org/) and [JAXP XPath](http://xml.apache.org/xerces2-j/javadocs/api/javax/xml/xpath/package-summary.html) (appeared in Java 1.5). Juxy will automatically detect presence of the supported engine, so if you are going to run tests under the Java 1.5 Jaxen is no longer required.

As for XSLT engine, Juxy should work with any JAXP compliant XSLT processor which supports XSLT 1.0 specification and JAXP DOMResult. Both [Xalan](http://xml.apache.org/xalan-j/) and [Saxon](http://saxon.sourceforge.net/) (6.5.X and 8.X versions) were tested and work fine with Juxy. Since version 0.7.2 Juxy also supports XSLT processor bundled with Java 1.5.

The following libraries are required for Ant verifier task:
* juxy_ant.jar
* JAXP compliant XSLT engine
* [XML commons resolver](http://xml.apache.org/commons/) (optional)

XML commons resolver.jar is required if you are going to use XML catalogs URI resolution. juxy.jar is not required for Ant task.

## Download package

Juxy is distributed under the terms of the Apache License 2.0.

The latest version of Juxy is 0.8. You can download it [here](http://juxy.tigris.org/servlets/ProjectDocumentList?folderID=5095&expandFolder=5095&folderID=7539).

The package includes:
* [API javadoc](http://teamcity.jetbrains.com/repository/download/bt24/.lastPinned/doc/javadoc/index.html?guest=1)
* juxy.jar
* juxy_ant.jar
* juxy_src.jar
* samples
* required libraries to run samples
</ul>

Juxy uses [TeamCity](http://www.jetbrains.com/teamcity) Continuous Integration server. The project has been setup in [TeamCity demo installation](http://teamcity.jetbrains.com/project.html?projectId=project16&tab=projectOverview&guest=1).

## Whom to contact?

My name is Pavel Sher, and I am the author of this project. Support for tests in XML format was contributed by Tony Graham. 

Please send your thoughts and questions to the [users mailing list](http://juxy.tigris.org/ds/viewForumSummary.do?dsForumId=3638).
If you found a bug, you can report it into the [issues mailing list](http://juxy.tigris.org/ds/viewForumSummary.do?dsForumId=3637).

## Related projects

Here are some related projects I do know about:
* [A set of XSLT stylesheets to simplify XSLT Unit Testing](http://www.jenitennison.com/xslt/utilities/unit-testing/)
* [XSLTunit](http://xsltunit.org/)
* [XMLUnit - JUnit and NUnit testing for XML](http://xmlunit.sourceforge.net/)
* [UTF-X - Unit Testing Framework - XSLT](http://utf-x.sourceforge.net/)

