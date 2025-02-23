<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="same-line-names" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/program/objects//o[@line and @name]">
        <xsl:apply-templates select="." mode="check"/>
      </xsl:for-each>
    </defects>
  </xsl:template>
  <xsl:template match="o" mode="check">
    <xsl:variable name="x" select="."/>
    <xsl:for-each select="/program/objects//o[not(. is $x) and @name=$x/@name and @line=$x/@line]">
      <xsl:element name="defect">
        <xsl:attribute name="line">
          <xsl:value-of select="eo:lineno(@line)"/>
        </xsl:attribute>
        <xsl:attribute name="severity">
          <xsl:text>error</xsl:text>
        </xsl:attribute>
        <xsl:text>The name </xsl:text>
        <xsl:value-of select="eo:escape(@name)"/>
        <xsl:text> has been used twice on line no.</xsl:text>
        <xsl:value-of select="eo:lineno(@line)"/>
      </xsl:element>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
