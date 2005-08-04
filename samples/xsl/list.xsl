<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>

<xsl:template name="makeList">
    <xsl:for-each select="item">
        <xsl:value-of select="."/>
        <xsl:if test="not(position() = last())">, </xsl:if>
    </xsl:for-each>
</xsl:template>

<xsl:template match="list">
    <xsl:call-template name="makeList"/>
</xsl:template>

</xsl:stylesheet>
