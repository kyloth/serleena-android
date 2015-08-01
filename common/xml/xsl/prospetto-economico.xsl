<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes" />

<xsl:template match="/prospetto-economico">
  \begin{tabular}{|c|c|c|}
  \hline
  \textbf{Ruolo} &amp; \textbf{Ore} &amp; \textbf{Costo} \\
  \hline
  <xsl:for-each select="role">
    <xsl:value-of select="@name" /> &amp; <xsl:value-of select="@hours" /> &amp; <xsl:value-of select="@cost" /> \\
  </xsl:for-each>
  \hline
  \textbf{Totale} &amp;
  <xsl:value-of select="sum(role/@hours)" /> &amp;
  <xsl:value-of select="sum(role/@cost)" /> \\
  \hline
  \end{tabular}
</xsl:template>

</xsl:stylesheet>