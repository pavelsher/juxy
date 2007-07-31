<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>

  <xsl:param name="globalParam"/>

  <xsl:variable name="globalVar" select="globalVarValue"/>

  <xsl:template name="template-parameter">
    <xsl:param name="param1"/>
    <xsl:value-of select="$param1"/>
  </xsl:template>

  <xsl:template name="template-global-variable">
    <xsl:value-of select="$globalVar"/>
  </xsl:template>

  <xsl:template name="template-global-parameter">
    <xsl:value-of select="$globalParam"/>
  </xsl:template>

</xsl:stylesheet>
