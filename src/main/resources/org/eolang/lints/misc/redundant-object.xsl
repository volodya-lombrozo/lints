<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="redundant-object" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@name and @base and @base != '∅']">
        <xsl:variable name="usage" select="concat('^\$(?:\.\^)*\.', @name, '(?:\.\w+)?$')"/>
        <xsl:variable name="lauto" select="substring(//o[@base = '.eachi' or @base = '.each']/o[starts-with(@base, '$.a🌵')]/@base, 3)"/>
        <xsl:variable name="auto" select="//o[@name=$lauto]"/>
        <xsl:variable name="looped" select="boolean(//o[@base = '.eachi' or @base = '.each']/o[matches(@base, $usage)])"/>
        <xsl:variable name="looped-auto" select="boolean($auto/o[matches(@base, $usage)])"/>
        <xsl:if test="count(//o[matches(@base, $usage)])=1">
          <xsl:if test="not($looped) and not($looped-auto)">
            <xsl:element name="defect">
              <xsl:variable name="line" select="eo:lineno(@line)"/>
              <xsl:attribute name="line">
                <xsl:value-of select="$line"/>
              </xsl:attribute>
              <xsl:if test="$line = '0'">
                <xsl:attribute name="context">
                  <xsl:value-of select="eo:defect-context(.)"/>
                </xsl:attribute>
              </xsl:if>
              <xsl:attribute name="severity">
                <xsl:text>warning</xsl:text>
              </xsl:attribute>
              <xsl:text>The object </xsl:text>
              <xsl:value-of select="eo:escape(@name)"/>
              <xsl:text> is redundant, consider inline it instead</xsl:text>
            </xsl:element>
          </xsl:if>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
