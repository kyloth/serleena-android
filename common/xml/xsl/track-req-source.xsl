<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" indent="yes" />

<xsl:include href="text.xsl" />

<xsl:template match="/all">

  \begin{tabularx}{\textwidth}{|Y{.3}|Y{.7}|}
  \hline
  \textbf{Requisito} &amp; \textbf{Fonte} \\
  \hline
	<xsl:for-each select="requirements/requirement">
		<xsl:sort select="@id" />
		<xsl:apply-templates select="." /> &amp;
		<xsl:for-each select="sources/source" >
      <xsl:variable name="identifier" select="." />
			<xsl:apply-templates select="/all/sources/source[@id = $identifier]" />~
      <xsl:if test="position() != last()"> \newline </xsl:if>
		</xsl:for-each> \\ \hline
	</xsl:for-each>
	\caption{Tracciamento requisiti-fonti}
  \end{tabularx}

</xsl:template>

<xsl:template match="source">
	<xsl:apply-templates />
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
