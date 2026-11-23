<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:p="urn:ietf:rfc:7807"
                exclude-result-prefixes="p">
    <xsl:output method="html" encoding="UTF-8" doctype-system="about:legacy-compat"/>

    <xsl:template match="/">
        <html lang="ru">
            <head>
                <meta charset="UTF-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <title>Библиотека · XML</title>
                <link rel="stylesheet" href="/css/style.css"/>
                <style>
                    article.editor + article.editor { margin-top: 12px; }
                    article.editor h2 { margin: 0; }
                </style>
            </head>
            <body>
                <header class="site-header">
                    <a class="logo" href="/api/books?format=xml">Библиотека · XML</a>
                    <nav aria-label="Каталог">
                        <a href="/api/books?format=xml">Книги</a>
                        <a href="/api/authors?format=xml">Авторы</a>
                    </nav>
                </header>
                <main>
                    <xsl:apply-templates select="*"/>
                </main>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="books">
        <h1>Книги <small><xsl:value-of select="count(book)"/></small></h1>
        <xsl:if test="not(book)">
            <p class="empty">В каталоге пока нет книг.</p>
        </xsl:if>
        <xsl:apply-templates select="book" mode="card"/>
    </xsl:template>

    <xsl:template match="authors">
        <h1>Авторы <small><xsl:value-of select="count(author)"/></small></h1>
        <xsl:if test="not(author)">
            <p class="empty">В каталоге пока нет авторов.</p>
        </xsl:if>
        <xsl:apply-templates select="author" mode="card"/>
    </xsl:template>

    <xsl:template match="/book">
        <h1>Карточка книги</h1>
        <xsl:apply-templates select="." mode="card"/>
    </xsl:template>

    <xsl:template match="/author">
        <h1>Карточка автора</h1>
        <xsl:apply-templates select="." mode="card"/>
    </xsl:template>

    <xsl:template match="book" mode="card">
        <article class="editor">
            <h2><a href="/api/books/{id}?format=xml"><xsl:value-of select="title"/></a></h2>
            <p><strong>ISBN </strong><xsl:value-of select="isbn"/></p>
            <p><strong>Год издания </strong><xsl:value-of select="publicationYear"/></p>
            <p>
                <strong>Авторы </strong>
                <xsl:choose>
                    <xsl:when test="authorIds/authorId">
                        <xsl:for-each select="authorIds/authorId">
                            <xsl:if test="position() &gt; 1">, </xsl:if>
                            <a href="/api/authors/{.}?format=xml">№ <xsl:value-of select="."/></a>
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise>нет связанных записей</xsl:otherwise>
                </xsl:choose>
            </p>
        </article>
    </xsl:template>

    <xsl:template match="author" mode="card">
        <article class="editor">
            <h2>
                <a href="/api/authors/{id}?format=xml">
                    <xsl:value-of select="concat(firstName, ' ', lastName)"/>
                </a>
            </h2>
            <p>
                <strong>Дата рождения </strong>
                <xsl:choose>
                    <xsl:when test="normalize-space(birthDate)"><xsl:value-of select="birthDate"/></xsl:when>
                    <xsl:otherwise>не указана</xsl:otherwise>
                </xsl:choose>
            </p>
            <p>
                <strong>Книги </strong>
                <xsl:choose>
                    <xsl:when test="bookIds/bookId">
                        <xsl:for-each select="bookIds/bookId">
                            <xsl:if test="position() &gt; 1">, </xsl:if>
                            <a href="/api/books/{.}?format=xml">№ <xsl:value-of select="."/></a>
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise>нет связанных записей</xsl:otherwise>
                </xsl:choose>
            </p>
        </article>
    </xsl:template>

    <xsl:template match="p:problem | problem">
        <h1>Ошибка <xsl:value-of select="p:status | status"/></h1>
        <p><xsl:value-of select="p:detail | detail"/></p>
    </xsl:template>
</xsl:stylesheet>
