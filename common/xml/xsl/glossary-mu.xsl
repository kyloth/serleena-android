<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text" omit-xml-declaration="yes" encoding="UTF-8" />

  <xsl:include href="text.xsl"/>

  <xsl:key name="kEntryInitial" match="glossentry/name"
           use="translate(substring(., 1, 1), 
             'abcdefghijklmnopqrstuvwxyz', 
             'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>

  <xsl:template match="/glossary">
    <!-- Select terms with distinct initials (case invariant) -->
    <xsl:variable name="termsByDistinctInitial"
                  select="glossentry/name[generate-id() = 
                             generate-id(key('kEntryInitial', 
                                            translate(substring(., 1, 1), 
                                            'abcdefghijklmnopqrstuvwxyz',
                                            'ABCDEFGHIJKLMNOPQRSTUVWXYZ'))[1])]" />

    <!-- Header -->
    <xsl:apply-templates select="$termsByDistinctInitial" mode="header">
      <xsl:sort select="translate(., 'abcdefghijklmnopqrstuvwxyz',
                                     'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"
                data-type="text" order="ascending" />
    </xsl:apply-templates>
      <xsl:apply-templates select="$termsByDistinctInitial" mode="main">
        <xsl:sort select="translate(., 'abcdefghijklmnopqrstuvwxyz',
                                     'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"
                  data-type="text"
                  order="ascending" />
      </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="name" mode="header">
    <xsl:variable name="initial">
      <xsl:call-template name="ToUpper">
        <xsl:with-param name="value" select="substring(., 1, 1)" />
      </xsl:call-template>
    </xsl:variable>
    \noindent\hyperref[glossary-<xsl:value-of select="$initial" />]{<xsl:value-of select="$initial" />}
  </xsl:template>

  <xsl:template match="name" mode="main">
    <xsl:variable name="initial">
      <xsl:call-template name="ToUpper">
        <xsl:with-param name="value" select="substring(., 1, 1)" />
      </xsl:call-template>
    </xsl:variable>
    \subsection*{<xsl:value-of select="$initial" />}\label{glossary-<xsl:value-of select="$initial" />}
    \begin{description}
    <xsl:apply-templates select="key('kEntryInitial', $initial)/.." />
    \end{description}
  </xsl:template>

  <xsl:template match="glossentry">
    \item[<xsl:apply-templates select="name"/>] \hfill \\
    <xsl:apply-templates select="description"/>
  </xsl:template>

  <xsl:template match="description">
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template name="ToUpper">
    <xsl:param name="value" />
    <xsl:value-of select="translate(substring($value, 1, 1), 
                      'abcdefghijklmnopqrstuvwxyz',
                      'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
  </xsl:template>
</xsl:stylesheet>
