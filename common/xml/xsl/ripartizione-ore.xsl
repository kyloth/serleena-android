<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes" encoding="UTF-8" />

<xsl:template match="/partition">
  <xsl:variable name="thereArePeople" select="count(//@person) &gt; 0" />

  <!-- For testing purposes
  \documentclass{article}
  \begin{document}
  -->

  \begin{tabular}{|l|l|l|l|<xsl:if test="$thereArePeople">l|</xsl:if>}
  \hline
  \textbf{Identificativo} &amp;
  \textbf{Nome attività} &amp;
  \textbf{Ruolo} &amp;
  <xsl:if test="$thereArePeople">
    \textbf{Nominativo} &amp;
  </xsl:if>
  \textbf{Ore} \\
  \hline
  <xsl:for-each select="main-activity">
    <xsl:sort select="@id" />
    \textbf{<xsl:value-of select="@id"/>} &amp;
    \textbf{<xsl:value-of select="@name"/>} &amp; &amp;
    <xsl:if test="$thereArePeople">&amp; </xsl:if>\\
    \hline

    <xsl:for-each select="activity">
      <xsl:value-of select="@id"/> &amp;
      <xsl:value-of select="@name"/> &amp;
      \projrole{<xsl:value-of select="@role"/>} &amp;
      <xsl:if test="$thereArePeople">
        <xsl:value-of select="@person"/> &amp;
      </xsl:if>
      <xsl:value-of select="@hours"/> \\
      <xsl:if test="position() = last()">\hline</xsl:if>
    </xsl:for-each>

  </xsl:for-each>
  \end{tabular}

  <!-- For testing purposes
  \end{document}
  -->
</xsl:template>

</xsl:stylesheet>
