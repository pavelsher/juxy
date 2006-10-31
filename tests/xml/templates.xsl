<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>

  <xsl:template name="getRoot">
    <result>
      <xsl:copy-of select="/"/>
    </result>
  </xsl:template>

  <xsl:template match="root">
    <result>
      <xsl:value-of select="text()"/>
    </result>
  </xsl:template>

  <xsl:template match="root" mode="mode">
    <result>
      <xsl:value-of select="text()"/>
      <xsl:text> [with mode]</xsl:text>
    </result>
  </xsl:template>

</xsl:stylesheet>