<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:oldns="http://juxy.tigris.org/0.9"
    xmlns:newns="http://juxy.tigris.org/1.0"
    version='1.0'>

<xsl:template match="oldns:*">
    <xsl:element name="{concat('newns:', local-name(.))}">
        <xsl:apply-templates select="node() | @*"/>
    </xsl:element>
</xsl:template>

<xsl:template match="node() | @*">
    <xsl:copy>
        <xsl:apply-templates select="node() | @*"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>