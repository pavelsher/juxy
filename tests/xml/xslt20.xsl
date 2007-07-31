<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                version='2.0'>

  <xsl:template name="numbers">
    <xsl:for-each select="1 to 5">
      <xsl:value-of select="."/>
      <xsl:if test="not(position() = last())">, </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="function">
    <xsl:value-of select="fn:max((3, 2, 5))"/>
  </xsl:template>

</xsl:stylesheet>