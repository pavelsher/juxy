<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ns1="http://ns1.net"
                version='1.0'>


<xsl:template name="ns1:named">
    <named/>
</xsl:template>

<xsl:template match="source" mode="ns1:mode">
    <matched/>
</xsl:template>


</xsl:stylesheet>