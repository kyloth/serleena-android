<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes" />

<xsl:template match="/requirements">
  \begin{figure}
  \centering
  \begin{tabular}{|l|l|l|l|}
  \hline
  \textbf{Categoria} &amp;
  \textbf{Obbligatorio} &amp;
  \textbf{Desiderabile} &amp;
  \textbf{Opzionale} \\
  \hline

  Funzionale &amp;
  <xsl:value-of select="count(requirement[category='functional' and importance='0'])"/>
  &amp;
  <xsl:value-of select="count(requirement[category='functional' and importance='1'])"/>
  &amp;
  <xsl:value-of select="count(requirement[category='functional' and importance='2'])"/>
  \\ \hline
  Prestazionale &amp;
  <xsl:value-of select="count(requirement[category='performance' and importance='0'])"/>
  &amp;
  <xsl:value-of select="count(requirement[category='performance' and importance='1'])"/>
  &amp;
  <xsl:value-of select="count(requirement[category='performance' and importance='2'])"/>
  \\ \hline
  Di qualità &amp;
  <xsl:value-of select="count(requirement[category='quality' and importance='0'])"/>
  &amp;
  <xsl:value-of select="count(requirement[category='quality' and importance='1'])"/>
  &amp;
  <xsl:value-of select="count(requirement[category='quality' and importance='2'])"/>
  \\ \hline
  Di vincolo &amp;
  <xsl:value-of select="count(requirement[category='constraint' and importance='0'])"/>
  &amp;
  <xsl:value-of select="count(requirement[category='constraint' and importance='1'])"/>
  &amp;
  <xsl:value-of select="count(requirement[category='constraint' and importance='2'])"/>
  \\ \hline
  \end{tabular}
  \caption{Riepilogo dei requisiti}
  \end{figure}
</xsl:template>

</xsl:stylesheet>