<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes" />

  <xsl:template match="strong">\strong{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="emph">\emph{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="foreignword">\foreignword{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="fixedwidth">\fixedwidth{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="classname">\className{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="methodname">\methodName{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="gloss">\gloss{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="projrole">\projrole{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="projdoc">\projdoc{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="webaddress">\webaddress{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="email">\email{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="pathcommand">\pathcommand{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="code">\code{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="uri">\uri{<xsl:apply-templates/>}</xsl:template>

  <xsl:template match="ar">\AR{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="gl">\GL{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="np">\NP{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="pp">\PP{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="pq">\PQ{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="sf">\SF{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="st">\ST{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="ra">\RA{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="rp">\RP{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="rr">\RR{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="dp">\DP{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="mu">\MU{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="serleena">\serleena{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="kyloth">\Kyloth{<xsl:apply-templates/>}</xsl:template>
  <xsl:template match="cite">\cite{<xsl:apply-templates/>}</xsl:template>

</xsl:stylesheet>
