<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:fn="http://www.w3.org/2005/xpath-functions">
<xsl:output method="text" omit-xml-declaration="yes" />

<xsl:template match="/">
  <xsl:for-each select="//version">
    <xsl:sort select="." order="descending" />
    <xsl:if test="position() = 1">
      <xsl:value-of select="."/><xsl:text>&#xA;</xsl:text>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
