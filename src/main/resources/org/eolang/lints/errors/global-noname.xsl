<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="global-noname" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/program/objects/o[not(@name)]">
        <xsl:element name="defect">
          <xsl:attribute name="line">
            <xsl:value-of select="eo:lineno(@line)"/>
          </xsl:attribute>
          <xsl:attribute name="severity">
            <xsl:text>error</xsl:text>
          </xsl:attribute>
          <xsl:text>The global object doesn't have a name</xsl:text>
          <xsl:if test="@base">
            <xsl:text> (@base='</xsl:text>
            <xsl:value-of select="@base"/>
            <xsl:text>')</xsl:text>
          </xsl:if>
        </xsl:element>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
