<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>

  <xsl:param name="aparam"/>

  <xsl:template name="getText">
    <root>
      <xsl:text>atext</xsl:text>
    </root>
  </xsl:template>

  <xsl:template name="getGlobalParamValue">
    <root>
      <xsl:value-of select="$aparam"/>
    </root>
  </xsl:template>


  <xsl:template name="getConcatenatedInvokeParamValues">
    <xsl:param name="invparam1"/>
    <xsl:param name="invparam2"/>

    <root>
      <xsl:value-of select="concat($invparam1, ':', $invparam2)"/>
    </root>
  </xsl:template>


  <xsl:template name="getSumOfInvokeParamValues">
    <xsl:param name="invparam1"/>
    <xsl:param name="invparam2"/>

    <root>
      <xsl:value-of select="$invparam1 + $invparam2"/>
    </root>
  </xsl:template>


</xsl:stylesheet>