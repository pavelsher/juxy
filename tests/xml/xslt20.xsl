<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='2.0'>

    <xsl:template name="numbers">
        <xsl:for-each select="1 to 5">
            <xsl:value-of select="."/>
            <xsl:if test="not(position() = last())">, </xsl:if>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>