<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes" />

<xsl:include href="text.xsl"/>

<xsl:param name="caption" select="_not_set" />

<xsl:template match="/all">
  <!-- For testing purposes

  \documentclass{../kylothdoc}
  \doctitle{Norme di progetto}
  \authorOne{Pozzan Gabriele}
  \authorTwo{Sestini Filippo}
  \manager{Valle Sebastiano}
  \verifierOne{Verifier}
  \version{1.0.6}
  \distribListOne{Kyloth}
  \usetype{Interno}
  \summary{Questo documento specifica le norme di progetto adottate da Kyloth.}
  \biglogo[0.1]{../logo.jpg}
  \smalllogo[0.02]{../logo.jpg}

  \begin{document}
  -->

  \begin{tabularx}{\textwidth}{|Y{.14}|Y{.14}|Y{.5}|Y{.22}|}
  \hline
  \textbf{Requisito} &amp; \textbf{Tipologia} &amp; \textbf{Descrizione} &amp; \textbf{Fonti} \\
  \hline
  <xsl:apply-templates select="requirements"/>
  <xsl:if test="$caption != '_not_set'">
    \caption{<xsl:value-of select="$caption"/>}
  </xsl:if>
  \end{tabularx}

  <!-- For testing purposes
  \end{document}
  -->
</xsl:template>

<xsl:template match="requirements">
  <xsl:for-each select="requirement">
    <xsl:sort select="@id1" data-type="number"/>
    <xsl:sort select="@id2" data-type="number"/>
    <xsl:sort select="@id3" data-type="number"/>
    <xsl:apply-templates select="." />
  </xsl:for-each>
</xsl:template>

<xsl:template match="requirement">
  R<xsl:choose>
    <xsl:when test="category='functional'">F</xsl:when>
    <xsl:when test="category='quality'">Q</xsl:when>
    <xsl:when test="category='performance'">P</xsl:when>
    <xsl:when test="category='constraint'">C</xsl:when>
  </xsl:choose>
  <xsl:choose>
    <xsl:when test="importance=0">M</xsl:when>
    <xsl:when test="importance=1">D</xsl:when>
    <xsl:when test="importance=2">O</xsl:when>
  </xsl:choose>
  <xsl:value-of select="@id"/>
  &amp;
  <xsl:choose>
    <xsl:when test="category='functional'">Funzionale</xsl:when>
    <xsl:when test="category='quality'">Di qualità</xsl:when>
    <xsl:when test="category='performance'">Prestazionale</xsl:when>
    <xsl:when test="category='constraint'">Di vincolo</xsl:when>
  </xsl:choose> \newline
  <xsl:choose>
    <xsl:when test="importance='0'">Obbligatorio</xsl:when>
    <xsl:when test="importance='1'">Desiderabile</xsl:when>
    <xsl:when test="importance='2'">Opzionale</xsl:when>
  </xsl:choose>
  &amp;
  <xsl:apply-templates select="description"/>
  &amp;
  <xsl:for-each select="sources/source">
    <xsl:variable name="identifier" select="." />
    <xsl:apply-templates select="/all/sources/source[@id = $identifier]" />~
    <xsl:if test="position() != last()"> \newline </xsl:if>
  </xsl:for-each>
  \\ \hline
</xsl:template>

<xsl:template match="description">
  <xsl:apply-templates/>
</xsl:template>
<xsl:template match="source">
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
