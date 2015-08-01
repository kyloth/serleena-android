<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes" />

<xsl:include href="text.xsl"/>

<xsl:template match="/requirements">
  \begin{figure}[h!]
  \centering
  \begin{tabular}{|l|l|}
  \hline
  \textbf{Requisito} &amp; \textbf{Descrizione} \\
  \hline
  <xsl:for-each select="requirement[@accepted='true' and importance != '0']">
    <!-- TODO testare -->
    <xsl:apply-templates select="."/> &amp; <xsl:apply-templates select="description"/>\\
    \hline    
  </xsl:for-each>
  \end{tabular}
  \caption{Requisiti desiderabili e opzionali accettati}
  \end{figure}
</xsl:template>

<xsl:template match="requirement">
  R<xsl:value-of select="importance"/>
  <xsl:choose>
    <xsl:when test="category='functional'">F</xsl:when>
    <xsl:when test="category='quality'">Q</xsl:when>
    <xsl:when test="category='performance'">P</xsl:when>
    <xsl:when test="category='constraint'">V</xsl:when>
  </xsl:choose>
  <xsl:value-of select="@id"/>
</xsl:template>

<xsl:template match="description">
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
