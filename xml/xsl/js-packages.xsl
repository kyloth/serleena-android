<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="text" omit-xml-declaration="yes" />

  <xsl:include href="text.xsl"/>

  <xsl:template match="//jsdoc">

    <xsl:for-each select="//classes">

      <xsl:for-each select="//constructor">
        \subsubsection{\pathcommand{<xsl:value-of select="name"/>}}
        \subsubsubsection{Descrizione}
        <xsl:value-of select="description"/>
        \subsubsubsection{Utilizzo}
        <xsl:value-of select="examples"/>

        <xsl:for-each select="parameters">
          <xsl:if test="position()=1">
            \subsubsubsection{Dipendenze}
            \begin{itemize}
          </xsl:if>
          \item \texttt{ <xsl:value-of select="name"/> :
          <xsl:value-of select="type"/>} :
          <xsl:value-of select="description"/>
          <xsl:if test="position()=last()">
            \end{itemize}
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>

      <xsl:for-each select="//properties">
        <xsl:if test="position()=1">
          \subsubsubsection{Campi dati}
          \begin{itemize}
        </xsl:if>
          \item \path{<xsl:value-of select="name"/> :
          <xsl:value-of select="type/names"/>} :
          <xsl:value-of select="description"/>
        <xsl:if test="position()=last()">
          \end{itemize}
        </xsl:if>
      </xsl:for-each>

      <xsl:for-each select="//functions"> <!-- METODO -->
        <xsl:if test="position()=1">
          \subsubsubsection{Metodi}
          \begin{itemize}
        </xsl:if>
          \item \path{<xsl:value-of select="name"/>(<xsl:for-each select="parameters"><xsl:value-of select="name"/> : <xsl:value-of select="type"/><xsl:if test="position() != last()">, </xsl:if></xsl:for-each>) : <xsl:value-of select="returns/type"/>} : <xsl:value-of select="description"/>
        <xsl:for-each select="parameters"> <!-- CAMPI DATI -->
          <xsl:if test="position()=1">
            \paragraph{Parametri}

            \begin{itemize}
          </xsl:if>
          \item <xsl:value-of select="name"/> : <xsl:value-of select="description"/>
          <xsl:if test="position()=last()">
            \end{itemize}
          </xsl:if>
        </xsl:for-each> <!-- FINE CAMPI DATI -->
        <xsl:if test="position()=last()">
          \end{itemize}
        </xsl:if>
      </xsl:for-each>

    </xsl:for-each>

  </xsl:template>

</xsl:stylesheet>
