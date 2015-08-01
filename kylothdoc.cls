\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{kylothdoc}[2014/12/17 Kyloth Standard Documentation Class]
\LoadClass[10pt,a4paper]{article}

\RequirePackage[italian]{babel}
\RequirePackage[utf8]{inputenc}
\RequirePackage{amsmath}
\RequirePackage{amsfonts}
\RequirePackage{amssymb}
\RequirePackage{a4wide}
\RequirePackage{graphicx}
\RequirePackage{todonotes}
\RequirePackage{pbox}
\RequirePackage{fancyhdr}
\RequirePackage{etoolbox}
\RequirePackage{mpgraphics}
\RequirePackage{ifpdf}
\RequirePackage[shellescape,latex]{gmp}
\RequirePackage{hyperref}
\RequirePackage{array}
\RequirePackage{natbib}
\RequirePackage{tabularx}
\RequirePackage[shortlabels]{enumitem}
\RequirePackage{url}
\RequirePackage{fixltx2e}
\RequirePackage{pifont}
\RequirePackage{float}
\RequirePackage{url}
\RequirePackage{natbib}
\RequirePackage{adjustbox}
\RequirePackage{pdflscape}
\RequirePackage{eurosym}
\RequirePackage{ltablex}
\RequirePackage{titlesec}
\RequirePackage{pgf-umlsd}
\RequirePackage{lastpage}
\RequirePackage{subcaption}
\usepackage{ifthen}
\usepackage{xstring}

\newcolumntype{L}[1]{>{\raggedright\let\newline\\\arraybackslash\hspace{0pt}}m{#1}}
\newcolumntype{C}[1]{>{\centering\let\newline\\\arraybackslash\hspace{0pt}}m{#1}}
\newcolumntype{R}[1]{>{\raggedleft\let\newline\\\arraybackslash\hspace{0pt}}m{#1}}

% Column macro for tabularx ONLY!
\newcolumntype{Y}[1]{>{\hsize=#1\hsize}X}

% Commands
\newcommand{\foreignword}[1]{\emph{#1}}
\newcommand{\fixedwidth}[1]{\texttt{#1}}
\newcommand{\className}[1]{\code{#1}}
\newcommand{\methodName}[1]{\code{#1}}
\newcommand{\strong}[1]{\textbf{#1}}
\newcommand{\projrole}[1]{\textit{#1}}
\newcommand{\projdoc}[1]{\textit{#1}}
\newcommand{\webaddress}[1]{\path{#1}}
\newcommand{\uri}[1]{\path{#1}}
\newcommand{\email}[1]{\fixedwidth{#1}}
\newcommand{\pathcommand}[1]{\fixedwidth{#1}}
\newcommand{\autofit}[1]{\begin{adjustbox}{max size={\textwidth}{\textheight}}
\input{#1}
\end{adjustbox}
}
\newcommand{\tickrole}[1]{\textit{#1}}
\newcommand{\tickstatus}[1]{\textsc{#1}}


% Smartgloss, evidenzia solo la prima istanza
\newcommand{\true}{1}   % Appoggio per leggibilita'
\newcommand{\false}{0}  % 
% Glossdec e' il vecchio gloss
\newcommand{\glossdec}[1]{#1\textsuperscript{\ding{72}}}

% Emula malamente una hashmap
\def\addvalue#1{\expandafter\gdef\csname    glosstable@\detokenize{#1}\endcsname{\true}}
\def\usevalue#1{%
  \ifcsname glosstable@\detokenize{#1}\endcsname\true\else\false\fi
}
% Vera e propria macro smartgloss
\DeclareRobustCommand{\gloss}[1]{
    \IfEq{\usevalue{#1}}{\true}{#1}
    {\glossdec{#1}\addvalue{#1}}
}


% Shortcuts
\newcommand{\AR}{Analisi dei Requisiti}
\newcommand{\GL}{Glossario}
\newcommand{\NP}{Norme di Progetto}
\newcommand{\PP}{Piano di Progetto}
\newcommand{\PQ}{Piano di Qualifica}
\newcommand{\SF}{Studio di Fattibilità}
\newcommand{\ST}{Specifica Tecnica}
\newcommand{\RA}{Revisione di Accettazione}
\newcommand{\RP}{Revisione di Progettazione}
\newcommand{\RQ}{Revisione di Qualifica}
\newcommand{\RR}{Revisione dei Requisiti}
\newcommand{\DP}{Definizione di Prodotto}
\newcommand{\MU}{Manuale Utente}
\newcommand{\RES}{\tickstatus{Resolved}}
\newcommand{\TRI}{\tickstatus{Triaged}}
\newcommand{\REO}{\tickstatus{Reopened}}
\newcommand{\CLO}{\tickstatus{Closed}}
\newcommand{\INP}{\tickstatus{In Progress}}
\newcommand{\serleena}{\textsf{serleena}}
\newcommand{\Kyloth}{\textsf{Kyloth}}

% Environments
\newenvironment{code}{%
\begin{verbatim}%
}{%
\end{verbatim}%
}

% Variables
\newcommand{\biglogo}[2][1]{%
  \gdef\@biglogo{#2}%
  \blscale{#1}
}
\newcommand{\smalllogo}[2][1]{%
  \gdef\@smalllogo{#2}%
  \slscale{#1}%
}

\newcommand{\slscale}[1]{\gdef\@slscale{#1}}
\newcommand{\blscale}[1]{\gdef\@blscale{#1}}

\newcommand{\doctitle}[1]{\gdef\@doctitle{#1}}
\newcommand{\version}[1]{\gdef\@version{#1}}
\newcommand{\authorOne}[1]{\gdef\@authorOne{#1}}
\newcommand{\authorTwo}[1]{\gdef\@authorTwo{#1}}
\newcommand{\authorThree}[1]{\gdef\@authorThree{#1}}
\newcommand{\authorFour}[1]{\gdef\@authorFour{#1}}
\newcommand{\authorFive}[1]{\gdef\@authorFive{#1}}
\newcommand{\authorSix}[1]{\gdef\@authorSix{#1}}
\newcommand{\authorSeven}[1]{\gdef\@authorSeven{#1}}
\newcommand{\verifierOne}[1]{\gdef\@verifierOne{#1}}
\newcommand{\verifierTwo}[1]{\gdef\@verifierTwo{#1}}
\newcommand{\verifierThree}[1]{\gdef\@verifierThree{#1}}
\newcommand{\verifierFour}[1]{\gdef\@verifierFour{#1}}
\newcommand{\verifierFive}[1]{\gdef\@verifierFive{#1}}
\newcommand{\verifierSix}[1]{\gdef\@verifierSix{#1}}
\newcommand{\verifierSeven}[1]{\gdef\@verifierSeven{#1}}
\newcommand{\distribListOne}[1]{\gdef\@distribListOne{#1}}
\newcommand{\distribListTwo}[1]{\gdef\@distribListTwo{#1}}
\newcommand{\distribListThree}[1]{\gdef\@distribListThree{#1}}
\newcommand{\distribListFour}[1]{\gdef\@distribListFour{#1}}
\newcommand{\distribListFive}[1]{\gdef\@distribListFive{#1}}
\newcommand{\distribListSix}[1]{\gdef\@distribListSix{#1}}
\newcommand{\distribListSeven}[1]{\gdef\@distribListSeven{#1}}
\newcommand{\summary}[1]{\gdef\@summary{#1}}
\newcommand{\manager}[1]{\gdef\@manager{#1}}
\newcommand{\usetype}[1]{\gdef\@usetype{#1}}

\AtBeginDocument{%
  %\ifpdf%
  %\DeclareGraphicsRule{*}{mps}{*}{}%
  %\fi%
  \keepXColumns
  \pagestyle{fancy}%
  \fancyhf{}%
  \lhead{\leftmark}%
  \rhead{\includegraphics[scale=\@slscale]{\smalllogo}}%
  \rfoot{\thepage\ di \begin{NoHyper}\pageref{LastPage}\end{NoHyper}}%
  \lfoot{\projdoc{\doctitle\;v\version}}%
}

\AtBeginDocument{\let\doctitle\@doctitle}
\AtBeginDocument{\let\version\@version}
\AtBeginDocument{\let\authorOne\@authorOne}
\AtBeginDocument{\let\authorTwo\@authorTwo}
\AtBeginDocument{\let\authorThree\@authorThree}
\AtBeginDocument{\let\authorFour\@authorFour}
\AtBeginDocument{\let\authorFive\@authorFive}
\AtBeginDocument{\let\authorSix\@authorSix}
\AtBeginDocument{\let\authorSeven\@authorSeven}

\AtBeginDocument{\let\verifierOne\@verifierOne}
\AtBeginDocument{\let\verifierTwo\@verifierTwo}
\AtBeginDocument{\let\verifierThree\@verifierThree}
\AtBeginDocument{\let\verifierFour\@verifierFour}
\AtBeginDocument{\let\verifierFive\@verifierFive}
\AtBeginDocument{\let\verifierSix\@verifierSix}
\AtBeginDocument{\let\verifierSeven\@verifierSeven}

\AtBeginDocument{\let\distribListOne\@distribListOne}
\AtBeginDocument{\let\distribListTwo\@distribListTwo}
\AtBeginDocument{\let\distribListThree\@distribListThree}
\AtBeginDocument{\let\distribListFour\@distribListFour}
\AtBeginDocument{\let\distribListFive\@distribListFive}
\AtBeginDocument{\let\distribListSix\@distribListSix}
\AtBeginDocument{\let\distribListSeven\@distribListSeven}

\AtBeginDocument{\let\biglogo\@biglogo}
\AtBeginDocument{\let\smalllogo\@smalllogo}

\AtBeginDocument{\let\summary\@summary}

\AtBeginDocument{\let\manager\@manager}

\AtBeginDocument{\let\usetype\@usetype}

\newcommand{\checkfor}[2]{%
  \ifcsname#1\endcsname%
    #2%
  \fi%
}

\newcommand{\ifndeferror}[1]{%
  \ifcsname#1\endcsname%
  \else%
    \ClassError{kylothdoc}{Variable #1 not defined}{}
  \fi%
}

\AtBeginDocument{%
  \ifdefempty{\authorOne}{\undef{\authorOne}}{}\par
  \ifdefempty{\verifierOne}{\undef{\verifierOne}}{}\par
  \ifdefempty{\manager}{\undef{\manager}}{}\par
  \ifdefempty{\version}{\undef{\version}}{}\par
  \ifdefempty{\distribListOne}{\undef{\distribListOne}}{}\par
  \ifdefempty{\usetype}{\undef{\usetype}}{}\par
  \ifdefempty{\summary}{\undef{\summary}}{}\par
  \ifdefempty{\biglogo}{\undef{\biglogo}}{}\par
  \ifdefempty{\smalllogo}{\undef{\smalllogo}}{}\par
  \ifndeferror{authorOne}%
  \ifndeferror{verifierOne}%
  \ifndeferror{manager}%
  \ifndeferror{version}%
  \ifndeferror{distribListOne}%
  \ifndeferror{usetype}%
  \ifndeferror{summary}%
  \ifndeferror{biglogo}%
  \ifndeferror{smalllogo}%
}

\newcommand{\HRule}{\noindent\rule{\linewidth}{0.1mm}}

\newcommand{\makedoctitle}{
  \begin{titlepage}
  \begin{center}
  \includegraphics[scale=\@blscale]{\biglogo} \\
  {\huge \bfseries \vspace{0.5cm} serleena: Smart Hiking} \vspace{0.5cm} \\
  \HRule \\[3mm]
  {\huge \bfseries \doctitle} \\
  \HRule \\[5mm]
  \begin{tabular}{R{5cm}|L{5cm}}
  Versione & v\version \\ \\
  Autori & \authorOne \\
  \checkfor{authorTwo}{& \authorTwo \\}
  \checkfor{authorThree}{& \authorThree \\}
  \checkfor{authorFour}{& \authorFour \\}
  \checkfor{authorFive}{& \authorFive \\}
  \checkfor{authorSix}{& \authorSix \\}
  \checkfor{authorSeven}{& \authorSeven \\}
  Verificatori & \verifierOne \\
  \checkfor{verifierTwo}{& \verifierTwo \\}
  \checkfor{verifierThree}{& \verifierThree \\}
  \checkfor{verifierFour}{& \verifierFour \\}
  \checkfor{verifierFive}{& \verifierFive \\}
  \checkfor{verifierSix}{& \verifierSix \\}
  \checkfor{verifierSeven}{& \verifierSeven \\}
  Responsabile & \manager \\ \\
  Lista di distribuzione & \distribListOne \\
  \checkfor{distribListTwo}{& \distribListTwo \\}
  \checkfor{distribListThree}{& \distribListThree \\}
  \checkfor{distribListFour}{& \distribListFour \\}
  \checkfor{distribListFive}{& \distribListFive \\}
  \checkfor{distribListSix}{& \distribListSix \\}
  \checkfor{distribListSeven}{& \distribListSeven \\}
  Uso & \usetype
  \end{tabular}\\[1cm]
  {\Large \bfseries Sommario} \\
  \summary
  \end{center}
  \end{titlepage}
}

% Macro per Use Case
% Usage: \UC{codice} oppure \UC[main]{sec}
\newcommand{\UC}[2][]{
  \ifx&#1&
    UC[\text{#2}]
  \else
    UC[\text{#1}].[\text{#2}]
  \fi
}

% Macro per enumerate key: value
% Usage:
% \subsection{Personaggi}
% \begin{kv-itemize}
%   \key[Paperino] papero sfortunato.
%   \key[Paperoga] cugino pasticcione.
%   \key[Paperone] papero miliardario.
%   \item Altri abitanti di Paperopoli.
% \end{kv-itemize}

\newenvironment{kv-itemize}{\begin{itemize}
\providecommand\key[1][]{\item \textbf{##1:}}
}{\end{itemize}}

% Macro per nominare repository
\newcommand{\repository}[1]{
  \fixedwidth{#1}
}

% Macro per indicare i bottoni
\newcommand{\button}[1]{
  [#1]
}
%Colori di bandiera
\definecolor{marvingreen}{RGB}{175,225,98}
\definecolor{marvingrey}{RGB}{215,215,215}
\definecolor{marvinred}{RGB}{245,161,147}
% Macro per definire requisiti
% Usage: \REQ{CAT}{IMPORTANTZA}{CODICE}
\newcommand{\REQ}[3]{
  R \ \text{#1}.\text{#2}.\text{#3}
}

% Macro subsubsubsection
\titleclass{\subsubsubsection}{straight}[\subsection]

\newcounter{subsubsubsection}[subsubsection]
\renewcommand\thesubsubsubsection{\thesubsubsection.\arabic{subsubsubsection}}
\renewcommand\theparagraph{\thesubsubsubsection.\arabic{paragraph}}

\titleformat{\subsubsubsection}
  {\normalfont\normalsize\bfseries}{\thesubsubsubsection}{1em}{}
\titlespacing*{\subsubsubsection}
{0pt}{3.25ex plus 1ex minus .2ex}{1.5ex plus .2ex}

\renewcommand\paragraph{\@startsection{paragraph}{5}{\z@}%
  {3.25ex \@plus1ex \@minus.2ex}%
  {-1em}%
  {\normalfont\normalsize\bfseries}}
\renewcommand\subparagraph{\@startsection{subparagraph}{6}{\parindent}%
  {3.25ex \@plus1ex \@minus .2ex}%
  {-1em}%
  {\normalfont\normalsize\bfseries}}
\def\toclevel@subsubsubsection{4}
\def\toclevel@paragraph{5}
\def\toclevel@paragraph{6}
\def\l@subsubsubsection{\@dottedtocline{4}{7em}{4em}}
\def\l@paragraph{\@dottedtocline{5}{10em}{5em}}
\def\l@subparagraph{\@dottedtocline{6}{14em}{6em}}

\setcounter{secnumdepth}{4}
\setcounter{tocdepth}{4}
