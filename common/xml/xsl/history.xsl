<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes" />

<xsl:include href="text.xsl"/>

<xsl:template match="/">
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

\section*{Diario delle modifiche}
\begin{tabularx}{\textwidth}{|X|l|l|l|}
  \hline
  \strong{Descrizione modifica} &amp; \strong{Autore modifica} &amp; \strong{Data modifica} &amp; \strong{Versione} \\
  \hline
  <xsl:for-each select="//change">
    <xsl:sort order="descending" select="date" />
    <xsl:sort order="descending" select="version" />
    <xsl:apply-templates select="description" /> &amp; <xsl:value-of select="author" /> &amp; <xsl:value-of select="date" /> &amp; <xsl:value-of select="version" /> \\
    \hline
  </xsl:for-each>
\end{tabularx}
\addtocounter{table}{-1}

<!-- For testing purposes
\end{document}
-->

</xsl:template>

<xsl:template match="description">
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
