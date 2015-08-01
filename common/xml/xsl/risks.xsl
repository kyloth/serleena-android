<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes" />

<xsl:include href="text.xsl"/>

<xsl:template match="/risks">
  
  <xsl:if test="count(risk[@level='technologies']) &gt; 0">
    \subsection{Livello tecnologico}
    <xsl:apply-templates select="risk[@level='technologies']" />
  </xsl:if>

  <xsl:if test="count(risk[@level='people']) &gt; 0">
    \subsection{Livello del personale}
    <xsl:apply-templates select="risk[@level='people']" />
  </xsl:if>

  <xsl:if test="count(risk[@level='organization']) &gt; 0">
    \subsection{Livello organizzativo}
    <xsl:apply-templates select="risk[@level='organization']" />
  </xsl:if>

  <xsl:if test="count(risk[@level='requirements']) &gt; 0">
    \subsection{Livello dei requisiti}
    <xsl:apply-templates select="risk[@level='requirements']" />
  </xsl:if>

  <xsl:if test="count(risk[@level='assessment']) &gt; 0">
    \subsection{Livello di stima}
    <xsl:apply-templates select="risk[@level='assessment']" />
  </xsl:if>

</xsl:template>

<xsl:template match="risk">
  \subsubsection{<xsl:apply-templates select="name" />}
  \begin{itemize}
  \item \textbf{Probabilità di occorrenza}:
        <xsl:apply-templates select="@probability" />
  \item \textbf{Grado di pericolosità}:
        <xsl:apply-templates select="@danger-level" />
  \item \textbf{Descrizione}:
        <xsl:apply-templates select="description" />
  \item \textbf{Strategie di rilevazione}:
        <xsl:apply-templates select="detection-strategy" />
  \item \textbf{Strategie di prevenzione}:
        <xsl:apply-templates select="prevention" />
  \item \textbf{Contromisure}:
        <xsl:apply-templates select="countermeasures" />
  \item \textbf{Attualizzazione}:
        <xsl:apply-templates select="actualization" />
  \end{itemize}
</xsl:template>

<xsl:template match="@probability">
  <xsl:choose>
    <xsl:when test=".='high'">Alto</xsl:when>
    <xsl:when test=".='mid'">Medio</xsl:when>
    <xsl:when test=".='low'">Basso</xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template match="@danger-level">
  <xsl:choose>
    <xsl:when test=".='catastrophic'">Catastrofico</xsl:when>
    <xsl:when test=".='serious'">Serio</xsl:when>
    <xsl:when test=".='tolerable'">Tollerabile</xsl:when>
    <xsl:when test=".='insignificant'">Trascurabile</xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template match="name">
  <xsl:apply-templates/>
</xsl:template>
<xsl:template match="description">
  <xsl:apply-templates/>
</xsl:template>
<xsl:template match="detection-strategy">
  <xsl:apply-templates/>
</xsl:template>
<xsl:template match="countermeasures">
  <xsl:apply-templates/>
</xsl:template>
<xsl:template match="actualization">
  <xsl:apply-templates/>
</xsl:template>
<xsl:template match="prevention">
  <xsl:apply-templates/>
</xsl:template>


</xsl:stylesheet>
