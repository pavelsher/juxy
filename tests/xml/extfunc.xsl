<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:tz="java:java.util.TimeZone"
                version='1.1'>

<xsl:param name="tz"/>

<xsl:template name="getTimeZoneString">
    <root>
        <xsl:value-of select="tz:toString($tz)"/>
    </root>
</xsl:template>


</xsl:stylesheet>