<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text" omit-xml-declaration="yes" />

  <xsl:template match="/">
    <xsl:for-each select="//usecase">
      &lt;<xsl:text>source id="uc</xsl:text><xsl:value-of select="@id" />
      <xsl:text>"></xsl:text>
        UC <xsl:value-of select="@id"/>: <xsl:value-of select="title"/>
      &lt;/source&gt;
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
