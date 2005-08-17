<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:tz="java:java.util.TimeZone"
                xmlns:container="java:org.tigris.juxy.ValueContainer"
                version='1.0'>

<xsl:param name="tz"/>
<xsl:param name="container"/>

<xsl:template name="getTimeZoneString">
    <root>
        <xsl:value-of select="tz:toString($tz)"/>
    </root>
</xsl:template>

<xsl:template name="setStringToContainer">
    <xsl:param name="string" select="''"/>

    <xsl:value-of select="container:setString($container, $string)"/>
</xsl:template>

<!--
<xsl:value-of select="tracer:log($juxy:tracer, 1, 2, 'file.xsl', 'message')"/>    
-->

<!--
<xsl:template name="setNodeToContainer">
    <xsl:value-of select="container:setNode($container, /)"/>
</xsl:template>
-->

</xsl:stylesheet>