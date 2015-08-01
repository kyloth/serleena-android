<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes" />

<xsl:include href="text.xsl"/>

<xsl:template match="test_list">
  <xsl:for-each select="system">
    \subsubsection{<xsl:value-of select="@name" />}
      <xsl:for-each select="package">
        \subsubsubsection{<xsl:value-of select="@name" />}
        <xsl:for-each select="class">
          \begin{tabularx}{\textwidth}{| Y{.14}|p{3cm}|Y{.5}|c |}
          \hline
          \strong{Id} &amp; \strong{Nome test} &amp; \strong{Descrizione} &amp; \strong{Stato} \\ [0.5 ex]
          \hline 
          <xsl:for-each select="test">
            <xsl:value-of select="concat('TU', count(../preceding::class)+1, '.', count(preceding-sibling::test)+1)" /> &amp;
            \hspace{0pt}<xsl:value-of select="@name" /> &amp;
            <xsl:apply-templates select="description" /> &amp;
            <xsl:choose>
            <xsl:when test="@status"><xsl:value-of select="@status"/></xsl:when>
            <xsl:otherwise>superato</xsl:otherwise></xsl:choose> \\ \hline
          </xsl:for-each>
          \caption{Test di unità per la classe \strong{<xsl:value-of select="@name" />}}
          \end{tabularx}
        </xsl:for-each>
      </xsl:for-each>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
