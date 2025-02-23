<?xml version="1.0"?>
<!--
 * Copyright (c) 2016-2025 Objectionary.com
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eo="https://www.eolang.org" version="2.0" id="object-does-not-match-filename">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:variable name="package" select="/program/metas/meta[head='package'][1]"/>
  <xsl:template match="/">
    <defects>
      <xsl:if test="not(/program/metas/meta[head='tests']) and /program/@source">
        <xsl:apply-templates select="/program/objects/o" mode="live"/>
      </xsl:if>
    </defects>
  </xsl:template>
  <xsl:template match="o" mode="live">
    <xsl:variable name="opath" as="xs:string">
      <xsl:choose>
        <xsl:when test="$package">
          <xsl:value-of select="concat($package/tail/text(), '.', @name)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@name"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="source" as="xs:string" select="replace($opath, '\.', '/')"/>
    <xsl:if test="not(starts-with(/program/@source, concat($source, '.')))">
      <defect>
        <xsl:attribute name="line">
          <xsl:value-of select="eo:lineno(@line)"/>
        </xsl:attribute>
        <xsl:attribute name="severity">warning</xsl:attribute>
        <xsl:text>It is expected that the </xsl:text>
        <xsl:value-of select="eo:escape($opath)"/>
        <xsl:text> object is coming from either </xsl:text>
        <xsl:value-of select="eo:escape(concat($source, '.eo'))"/>
        <xsl:text> or </xsl:text>
        <xsl:value-of select="eo:escape(concat($source, '.phi'))"/>
        <xsl:text> file, however its "program/@source" points to </xsl:text>
        <xsl:value-of select="eo:escape(/program/@source)"/>
      </defect>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
