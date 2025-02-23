<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="application-duality" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base]">
        <xsl:variable name="args">
          <xsl:choose>
            <xsl:when test="starts-with(@base, '.')">
              <xsl:copy-of select="o[position()&gt;1]"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:copy-of select="o"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:if test="count($args/*) != count($args/*[@as]) and count($args/*) != count($args/*[not(@as)])">
          <defect>
            <xsl:attribute name="line">
              <xsl:value-of select="eo:lineno(@line)"/>
            </xsl:attribute>
            <xsl:attribute name="severity">
              <xsl:text>critical</xsl:text>
            </xsl:attribute>
            <xsl:text>The </xsl:text>
            <xsl:choose>
              <xsl:when test="@name">
                <xsl:text>object </xsl:text>
                <xsl:value-of select="eo:escape(@name)"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>anonymous object</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:text> cannot have both @name and @as attributes as this is prohibited due to duality in the application, please use only one</xsl:text>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
