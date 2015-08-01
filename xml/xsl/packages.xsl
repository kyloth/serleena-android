<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="text" omit-xml-declaration="yes" />

  <xsl:include href="text.xsl"/>

  <xsl:template match="//package">

    <xsl:for-each select="class">
      <xsl:sort select="@name" />
      \subsubsection{\pathcommand{<xsl:value-of select="@qualified"/>}
      (classe<xsl:if test="@abstract='true'"> astratta</xsl:if>)}
      \subsubsubsection{Descrizione}
      <xsl:value-of select="comment"/>
      \subsubsubsection{Utilizzo}
      <xsl:value-of select="tag[@name='@use']/@text"/>

      <xsl:if test="class/@qualified != 'java.lang.Object'">
        <xsl:for-each select="class">
          \subsubsubsection{Eredita da}
          \begin{itemize}
            \item <xsl:value-of select="@qualified"/>.
          \end{itemize}
        </xsl:for-each>
      </xsl:if>
      <xsl:for-each select="interface">
        <xsl:if test="position()=1">
          \subsubsubsection{Interfacce implementate}
          \begin{itemize}
        </xsl:if>
          \item <xsl:value-of select="@qualified"/><xsl:choose><xsl:when test="position() != last()">;</xsl:when><xsl:otherwise>.</xsl:otherwise></xsl:choose>
        <xsl:if test="position()=last()">
          \end{itemize}
        </xsl:if>
      </xsl:for-each>

      <xsl:for-each select="tag[@name = '@field']">
        <xsl:if test="position()=1">
          \subsubsubsection{Campi dati}
          \begin{itemize}
        </xsl:if>
          \item <xsl:value-of select="@text"/>
        <xsl:if test="position()=last()">
          \end{itemize}
        </xsl:if>
      </xsl:for-each>

      <xsl:for-each select="constructor">
        <xsl:if test="position()=1">
          \subsubsubsection{Costruttori}
          \begin{itemize}
        </xsl:if>
          \item \path{<xsl:value-of select="@name"/>(<xsl:for-each select="parameter"><xsl:value-of select="@name"/> : <xsl:value-of select="type/@qualified"/><xsl:if test="position() != last()">, </xsl:if></xsl:for-each>)} : <xsl:value-of select="comment"/>

      <xsl:for-each select="completo">
        <xsl:if test="position()=1">
          \paragraph{Parametri}
          \begin{itemize}
        </xsl:if>
          \item \path{<xsl:value-of select="@nome"/> : <xsl:value-of select="@tipo"/>} : <xsl:value-of select="@descrizione"/>
        <xsl:if test="position()=last()">
          \end{itemize}
        </xsl:if>
      </xsl:for-each>

        <xsl:if test="position()=last()">
          \end{itemize}
        </xsl:if>
      </xsl:for-each>

      <xsl:for-each select="method">
        <xsl:if test="position()=1">
          \subsubsubsection{Metodi}
          \begin{itemize}
        </xsl:if>
          \item \path{<xsl:value-of select="@name"/>(<xsl:for-each select="parameter"><xsl:value-of select="@name"/> : <xsl:value-of select="type/@qualified"/><xsl:if test="position() != last()">, </xsl:if></xsl:for-each>) : <xsl:value-of select="return/@qualified"/>} : <xsl:value-of select="comment"/>

      <xsl:for-each select="completo">
        <xsl:if test="position()=1">
          \paragraph{Parametri}
          \begin{itemize}
        </xsl:if>
          \item \path{<xsl:value-of select="@nome"/> : <xsl:value-of select="@tipo"/>} : <xsl:value-of select="@descrizione"/>
        <xsl:if test="position()=last()">
          \end{itemize}
        </xsl:if>
      </xsl:for-each>

        <xsl:if test="position()=last()">
          \end{itemize}
        </xsl:if>
      </xsl:for-each>

    </xsl:for-each>

    <xsl:for-each select="enum">
      <xsl:sort select="@name" />
      \subsubsection{\pathcommand{<xsl:value-of select="@qualified"/>}
      (tipo enumerativo)}
      \subsubsubsection{Descrizione}
      <xsl:value-of select="comment"/>
      \subsubsubsection{Utilizzo}
      <xsl:value-of select="tag[@name='@use']/@text"/>

      \subsubsubsection{Valori possibili}
      <xsl:for-each select="constant">
        <xsl:if test="position()=1">
          \begin{itemize}
        </xsl:if>
          \item <xsl:value-of select="@name"/>
        <xsl:if test="position()=last()">
          \end{itemize}
        </xsl:if>
      </xsl:for-each>
    </xsl:for-each>

    <xsl:for-each select="interface">
      <xsl:sort select="@name" />
      \subsubsection{\pathcommand{<xsl:value-of select="@qualified"/>}
      (interfaccia)}
      \subsubsubsection{Descrizione}
      <xsl:value-of select="comment"/>
      \subsubsubsection{Utilizzo}
      <xsl:value-of select="tag[@name='@use']/@text"/>

      <xsl:for-each select="interface">
        <xsl:if test="position()=1">
          \subsubsubsection{Interfacce da cui eredita}
          \begin{itemize}
        </xsl:if>
          \item <xsl:value-of select="@qualified"/><xsl:choose><xsl:when test="position() != last()">;</xsl:when><xsl:otherwise>.</xsl:otherwise></xsl:choose>
        <xsl:if test="position()=last()">
          \end{itemize}
        </xsl:if>
      </xsl:for-each>

      <xsl:for-each select="method">
        <xsl:if test="position()=1">
          \subsubsubsection{Metodi}
          \begin{itemize}
        </xsl:if>
          \item \path{<xsl:value-of select="@name"/>(<xsl:for-each select="parameter"><xsl:value-of select="@name"/> : <xsl:value-of select="type/@qualified"/><xsl:if test="position() != last()">, </xsl:if></xsl:for-each>) : <xsl:value-of select="return/@qualified"/>} : <xsl:value-of select="comment"/>

      <xsl:for-each select="completo">
        <xsl:if test="position()=1">
          \paragraph{Parametri}
          \begin{itemize}
        </xsl:if>
          \item \path{<xsl:value-of select="@nome"/> : <xsl:value-of select="@tipo"/>} : <xsl:value-of select="@descrizione"/>
        <xsl:if test="position()=last()">
          \end{itemize}
        </xsl:if>
      </xsl:for-each>

        <xsl:if test="position()=last()">
          \end{itemize}
        </xsl:if>
      </xsl:for-each>

    </xsl:for-each>

  </xsl:template>

</xsl:stylesheet>
