<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="text" omit-xml-declaration="yes" />

  <xsl:include href="text.xsl"/>

  <xsl:template match="/components">
    \begin{itemize}
    <xsl:for-each select="component">
      <xsl:sort select="name" />
      \item <xsl:apply-templates select="." />
    </xsl:for-each>
    \end{itemize}
  </xsl:template>

  <xsl:template match="component">
    <!-- \paragraph{\texttt{<xsl:value-of select="name" />}}-->
    \texttt{<xsl:value-of select="name" />}
    \begin{description}\item[Tipologia]
    <xsl:choose>
      <xsl:when test="@type='interface'">Interfaccia</xsl:when>
      <xsl:when test="@type='class'">Classe</xsl:when>
    </xsl:choose>
    \item[Descrizione]
    <xsl:apply-templates select="description" />
    <xsl:if test="usage">
      \item[Utilizzo]
      <xsl:apply-templates select="usage" />
    </xsl:if>
    <xsl:if test="inheritance">
      \item[Eredita da]
      <xsl:for-each select="inheritance/super">
        \texttt{<xsl:value-of select="." />}
        <xsl:if test="position() != last()">, </xsl:if>
      </xsl:for-each>
    </xsl:if>
    \end{description}
  </xsl:template>

  <xsl:template match="description">
    <xsl:apply-templates />
  </xsl:template>

</xsl:stylesheet>
