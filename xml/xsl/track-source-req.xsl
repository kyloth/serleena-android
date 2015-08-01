<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" indent="yes" />

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

  \begin{tabularx}{\textwidth}{|Y{.7}|Y{.3}|}
  \hline
  \textbf{Fonte} &amp; \textbf{Requisito} \\
  \hline
  <xsl:for-each select="/all/sources/source">
    <xsl:sort select="." />
    <xsl:variable name="src" select="@id"/>
    <xsl:value-of select="." /> &amp;
    <xsl:for-each
      select="/all/requirements/requirement[sources/source/text() = $src]">
      <xsl:apply-templates select="." />
      <xsl:if test="position() != last()"> \newline </xsl:if>
    </xsl:for-each>~ \\ \hline
  </xsl:for-each>
  \caption{Tracciamento fonti-requisiti}
  \end{tabularx}

  <!-- For testing purposes
  \end{document}
  -->
</xsl:template>

<xsl:template match="requirement">
  R<xsl:choose>
    <xsl:when test="category='functional'">F</xsl:when>
    <xsl:when test="category='quality'">Q</xsl:when>
    <xsl:when test="category='performance'">P</xsl:when>
    <xsl:when test="category='constraint'">V</xsl:when>
  </xsl:choose>
  <xsl:choose>
    <xsl:when test="importance=0">M</xsl:when>
    <xsl:when test="importance=1">D</xsl:when>
    <xsl:when test="importance=2">O</xsl:when>
  </xsl:choose>
  <xsl:value-of select="@id"/>
</xsl:template>

</xsl:stylesheet>
