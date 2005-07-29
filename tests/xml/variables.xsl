<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>

<xsl:variable name="varWithString" select="'defaultvalue'"/>
<xsl:variable name="varWithSelect" select="/"/>
<xsl:variable name="varWithContent">
    <rootElem/>
</xsl:variable>

<xsl:template name="getVarWithStringValue">
    <root>
        <xsl:value-of select="$varWithString"/>
    </root>
</xsl:template>

<xsl:template name="getVarWithSelectValue">
    <xsl:copy-of select="$varWithSelect"/>
</xsl:template>

<xsl:template name="getVarWithContentValue">
    <xsl:copy-of select="$varWithContent"/>
</xsl:template>

</xsl:stylesheet>