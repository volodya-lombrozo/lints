<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" version="2.0" id="incorrect-test-object-name">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:output encoding="UTF-8" method="xml" indent="yes"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/program[metas/meta[head='tests']]/objects/o[@name]">
        <xsl:variable name="regexp" select="'^[a-z][a-z0-9]*(-[a-z0-9]+)*$'"/>
        <xsl:if test="not(matches(@name, $regexp))">
          <defect>
            <xsl:attribute name="line">
              <xsl:value-of select="eo:lineno(@line)"/>
            </xsl:attribute>
            <xsl:attribute name="severity">warning</xsl:attribute>
            <xsl:text>The name of the object </xsl:text>
            <xsl:value-of select="eo:escape(@name)"/>
            <xsl:text> doesn't match </xsl:text>
            <xsl:value-of select="eo:escape($regexp)"/>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
