<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="comment-too-short" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <xsl:variable name="min" select="32"/>
    <defects>
      <xsl:for-each select="/program/comments/comment[string-length(.) &lt; $min]">
        <xsl:element name="defect">
          <xsl:attribute name="line">
            <xsl:value-of select="eo:lineno(@line)"/>
          </xsl:attribute>
          <xsl:attribute name="severity">
            <xsl:text>warning</xsl:text>
          </xsl:attribute>
          <xsl:text>The comment for the object is too short (shorter than </xsl:text>
          <xsl:value-of select="$min"/>
          <xsl:text> characters)</xsl:text>
        </xsl:element>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
