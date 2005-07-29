<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>

<xsl:template name="copyDoc">
    <xsl:copy-of select="document('document.xml')"/>
</xsl:template>

</xsl:stylesheet>