<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="noname-attribute" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[eo:abstract(.)]">
        <xsl:apply-templates select="." mode="abstract"/>
      </xsl:for-each>
    </defects>
  </xsl:template>
  <xsl:template match="o" mode="abstract">
    <xsl:for-each select="o[not(@name)]">
      <xsl:element name="defect">
        <xsl:attribute name="line">
          <xsl:value-of select="eo:lineno(@line)"/>
        </xsl:attribute>
        <xsl:attribute name="severity">
          <xsl:text>error</xsl:text>
        </xsl:attribute>
        <xsl:text>The object </xsl:text>
        <xsl:if test="@name">
          <xsl:value-of select="eo:escape(@name)"/>
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:text>has an attribute without a name</xsl:text>
      </xsl:element>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
