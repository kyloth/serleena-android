<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes" />

<xsl:template match="/ripartizione-lavoro">
\begin{tabular}{|l|c|c|c|c|c|c|c|}
\hline
\textbf{Nominativo} &amp; \multicolumn{6}{|c|}{\textbf{Ore per ruolo}} &amp; \textbf{Ore totali} \\
&amp; Re &amp; Am &amp; An  &amp; Pt &amp; Ve &amp; Pr &amp; \\
\hline
<xsl:for-each select="person">
  <xsl:value-of select="@name" />
  &amp; <xsl:apply-templates select="work[@role='Re']/@hours" />
  &amp; <xsl:apply-templates select="work[@role='Am']/@hours" />
  &amp; <xsl:apply-templates select="work[@role='An']/@hours" />
  &amp; <xsl:apply-templates select="work[@role='Pt']/@hours" />
  &amp; <xsl:apply-templates select="work[@role='Ve']/@hours" />
  &amp; <xsl:apply-templates select="work[@role='Pr']/@hours" />
  <xsl:variable name="total" select="sum(work/@hours)" />
  &amp;
  <xsl:if test="round($total) = 0 and $total &lt; 0">-</xsl:if>
  <xsl:if test="$total &gt; 0">
    <xsl:value-of select="floor($total)" />
    <xsl:if test="$total - floor($total) != 0">
      <xsl:text>:30</xsl:text>
    </xsl:if>
  </xsl:if>
  <xsl:if test="$total &lt; 0">
    <xsl:value-of select="round($total)" />
    <xsl:if test="$total - round($total) != 0">
      <xsl:text>:30</xsl:text>
    </xsl:if>
  </xsl:if>

  \\
</xsl:for-each>

\hline
\end{tabular}
</xsl:template>

<xsl:template match="@hours">
  <xsl:if test="round(.) = 0 and . &lt; 0">-</xsl:if>
  <xsl:if test=". &gt; 0">
    <xsl:value-of select="floor(.)" />
    <xsl:if test=". - floor(.) != 0">
      <xsl:text>:30</xsl:text>
    </xsl:if>
  </xsl:if>
  <xsl:if test=". &lt; 0">
    <xsl:value-of select="round(.)" />
    <xsl:if test=". - round(.) != 0">
      <xsl:text>:30</xsl:text>
    </xsl:if>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>
