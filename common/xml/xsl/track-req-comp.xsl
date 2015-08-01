<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" indent="yes" />

<xsl:include href="text.xsl" />

<xsl:template match="/">
  <!-- For testing purposes
  \documentclass{../../kylothdoc}
  \doctitle{Norme di progetto}
  \authorOne{Pozzan Gabriele}
  \authorTwo{Sestini Filippo}
  \manager{Valle Sebastiano}
  \verifierOne{Verifier}
  \version{1.0.6}
  \distribListOne{Kyloth}
  \usetype{Interno}
  \summary{Questo documento specifica le norme di progetto adottate da Kyloth.}
  \biglogo[0.1]{../../logo_full.eps}
  \smalllogo[0.02]{../../logo.eps}

  \begin{document}
  -->

  \begin{tabularx}{\textwidth}{|Y{.3}|Y{.7}|}
  \hline
  \textbf{Requisito} &amp; \textbf{Componente} \\
  \hline
  <xsl:for-each select="requirements/requirement">
    <xsl:sort select="@id" />
    <xsl:apply-templates select="." /> &amp;
    <xsl:for-each select="components/component" >
      <xsl:value-of select="text()" />~
      <xsl:if test="position() != last()"> \newline </xsl:if>
    </xsl:for-each> \\ \hline
  </xsl:for-each>
  \caption{Tracciamento requisiti-componenti}
  \end{tabularx}
  <!-- For testing purposes
  \end{document}
  -->
</xsl:template>

<xsl:template match="requirement">
  <xsl:value-of select="@id"/>
</xsl:template>

</xsl:stylesheet>
