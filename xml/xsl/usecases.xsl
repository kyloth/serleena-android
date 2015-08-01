<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" omit-xml-declaration="yes" />

<xsl:include href="text.xsl"/>

<xsl:template match="usecase-set">
  <!-- For testing purposes

  \documentclass{../kylothdoc}
  \doctitle{Norme di progetto}
  \authorOne{asdas}
  \authorTwo{asdasd}

  \manager{asdasd}
  \verifierOne{Verifier}
  \version{1.0.6}
  \distribListOne{Kyloth}
  \usetype{Interno}
  \summary{asdasdasd}
  \biglogo[0.1]{../logo.jpg}
  \smalllogo[0.02]{../logo.jpg}
  \begin{document}

  -->

  \subsection{<xsl:value-of select="@title"/>}

  \begin{figure}[H]
  \centering
  \begin{mpdefs}
    input metauml
  \end{mpdefs}
  \begin{mpdisplay}
  <xsl:value-of select="diagram"/>
  \end{mpdisplay}
  \caption{Diagramma <xsl:value-of select="@title"/>}
  \end{figure}

  <xsl:for-each select="usecases/usecase">
    \subsubsection{UC <xsl:value-of select="@id"/>}

    \begin{tabularx}{\textwidth}{l X}

      \textbf{Titolo} &amp; <xsl:apply-templates select="title"/> \\
      \textbf{Descrizione} &amp; <xsl:apply-templates select="description"/> \\
      \textbf{Precondizione} &amp; <xsl:apply-templates select="precondition"/> \\
      \textbf{Postcondizione} &amp; <xsl:apply-templates select="postcondition"/> \\

      <xsl:if test="related-requirements">
        \textbf{Requisiti collegati} &amp;
        <xsl:for-each select="related-requirements/requirement">
          <xsl:value-of select="."/>
          <xsl:if test="position() != last()">\newline</xsl:if>
        </xsl:for-each> \\
      </xsl:if>

      \textbf{Attori primari} &amp;
      <xsl:for-each select="primary-actors/actor">
        <xsl:value-of select="."/>
        <xsl:if test="position() != last()"> \newline </xsl:if>
      </xsl:for-each> \\

      <xsl:if test="secondary-actors">
        \textbf{Attori secondari} &amp;
        <xsl:for-each select="secondary-actors/actor">
          <xsl:value-of select="."/>
          <xsl:if test="position() != last()"> \newline </xsl:if>
        </xsl:for-each> \\
      </xsl:if>

      \textbf{Scenario principale} &amp;
      \begin{enumerate}[nosep,leftmargin=0.4cm]
      <xsl:for-each select="flow/step">
        <xsl:sort select="@number"/>
        \item [<xsl:value-of select="@number"/>] <xsl:value-of select="."/>
      </xsl:for-each>
      \end{enumerate} \\

      <xsl:if test="extensions">
        \textbf{Estensioni} &amp;
        \begin{enumerate}[nosep,leftmargin=0.4cm,label=\alph*]
        <xsl:for-each select="extensions/extension">
          <xsl:sort select="@branch-from"/>
          \item <xsl:apply-templates select="precondition"/>
          \begin{enumerate}[nosep,leftmargin=0.65cm]
          <xsl:for-each select="flow/step">
            <xsl:sort select="@number"/>
            \item [<xsl:value-of select="../../@branch-from"/><xsl:text>.</xsl:text><xsl:value-of select="@number"/>] <xsl:value-of select="."/>
          </xsl:for-each>
          \end{enumerate}
          \textbf{Postcondizione:} <xsl:apply-templates select="postcondition" />
        </xsl:for-each>
        \end{enumerate} \\
      </xsl:if>

    \end{tabularx}
    \addtocounter{table}{-1}

  </xsl:for-each>

  <!-- For testing purposes
  \end{document}
  -->

</xsl:template>

<xsl:template match="system-name">
  <xsl:apply-templates/>
</xsl:template>
<xsl:template match="title">
  <xsl:apply-templates/>
</xsl:template>
<xsl:template match="description">
  <xsl:apply-templates/>
</xsl:template>
<xsl:template match="precondition">
  <xsl:apply-templates/>
</xsl:template>
<xsl:template match="postcondition">
  <xsl:apply-templates/>
</xsl:template>
<xsl:template match="failure-end-condition">
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
