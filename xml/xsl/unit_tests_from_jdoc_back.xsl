<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes" />

<xsl:include href="text.xsl"/>

<xsl:template match="root">
  <xsl:for-each select="package[class/@integration = 'no']">
      \subsubsubsection{<xsl:value-of select="@name" />}
      <xsl:for-each select="class">
        <xsl:if test="@integration = 'no'">
          \begin{tabularx}{\textwidth}{| Y{.14}|p{3cm}|Y{.5}|c |}
          \hline
          \strong{Id} &amp; \strong{Nome test} &amp; \strong{Descrizione} &amp; \strong{Stato} \\ [0.5 ex]
          \hline
          <xsl:for-each select="method[@name != 'initialize' and @name != 'setup' and @name != 'tearDown' and @name != 'cleanUp']">
              TUB<xsl:value-of select="../@x" />.<xsl:value-of select="@y" /> &amp;
              \hspace{0pt}<xsl:value-of select="@name" /> &amp;
              \hspace{0pt}<xsl:value-of select="comment" /> &amp;
              superato \\ \hline
          </xsl:for-each>
          \caption{Test di unità per la classe \texttt{<xsl:value-of select="@name" />}}
          \end{tabularx}
      </xsl:if>
      </xsl:for-each>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
