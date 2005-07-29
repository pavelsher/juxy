<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ns1="http://ns1.net"
                version='1.0'>


<xsl:param name="ns1:par"/>

<xsl:template name="getter">
    <root>
        <xsl:value-of select="$ns1:par"/>
    </root>
</xsl:template>

</xsl:stylesheet>