<?xml version="1.0" encoding="utf-8"?>
<!-- Sample stylesheet -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="xml"/>

  <xsl:strip-space elements="itm aut body bak"/>
  
  <xsl:template match="art">
    <article>
      <xsl:attribute name="article-type">
        <xsl:choose>
          <xsl:when test="@fmt = 'bkr'">book-review</xsl:when>
          <xsl:when test="@fmt = 'let'">letter</xsl:when>
          <xsl:otherwise>article</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <front>
        <article-meta>
          <contrib-group>
            <xsl:for-each select="auth">
              <contrib><xsl:apply-templates select="."/></contrib>
            </xsl:for-each>
          </contrib-group>
          <xsl:apply-templates select="ttl | //auth | //bok"/>
        </article-meta>
      </front>
      <xsl:apply-templates select="bod | bac"/>
    </article>
  </xsl:template>

  <xsl:template match="ttl">
    <title-group>
      <article-title><xsl:apply-templates/></article-title>
    </title-group>
  </xsl:template>

  <xsl:template match="bok/ttl">
    <source><xsl:apply-templates/></source>
  </xsl:template>

  <xsl:template match="bok">
    <product>
      <xsl:apply-templates/>
    </product>
  </xsl:template>
  
  <xsl:template match="isb">
    <issn><xsl:apply-templates/></issn>
  </xsl:template>
  
  <xsl:template match="pub">
    <publisher><xsl:apply-templates/></publisher>
  </xsl:template>
  
  <xsl:template match="isb">
    <isbn><xsl:apply-templates/></isbn>
  </xsl:template>
  
  <xsl:template match="pri">
    <bold><xsl:apply-templates/></bold>
  </xsl:template>
  
  <!-- The client is crazy for doing this this way. -->
  <xsl:template match="aut">
    <name>
      <surname><xsl:apply-templates select="snm"/></surname>
      <given-names><xsl:apply-templates select="fnm"/></given-names>
    </name>
  </xsl:template>
  
  <xsl:template match="//bod">
    <body><xsl:apply-templates/></body>
  </xsl:template>
  
  <xsl:template match="par">
    <p><xsl:value-of select="."/></p>
  </xsl:template>
  
<!--
  This used to be needed, don't know why:
  <xsl:template match="blue">
    <p><xsl:value-of select="."/></p>
  </xsl:template>
-->

  <xsl:template match="ita">
    <i><xsl:apply-templates/></i>
  </xsl:template>
  
</xsl:stylesheet>
