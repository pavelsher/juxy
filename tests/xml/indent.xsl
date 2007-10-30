<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:template match="/">
            <xsl:apply-templates select="XMLMsg/Body_Msg/Bodys"/>
    </xsl:template>
    <xsl:template match="Body">
            <Test/>
    </xsl:template>
</xsl:stylesheet>