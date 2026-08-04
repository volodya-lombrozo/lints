<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="line-is-absent" version="2.0">
  <xsl:import href="/org/eolang/funcs/escape.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <!--
  Here we go through all objects. If it has @line attribute,
  everything is OK. If it's not, we report an error.
  -->
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[not(@line)]">
        <xsl:element name="defect">
          <xsl:attribute name="line">
            <xsl:value-of select="0"/>
          </xsl:attribute>
          <xsl:if test="not(@name)">
            <xsl:attribute name="context">
              <xsl:value-of select="eo:defect-context(.)"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:attribute name="severity">
            <xsl:text>error</xsl:text>
          </xsl:attribute>
          <xsl:text>The @line attribute is absent at the </xsl:text>
          <xsl:choose>
            <xsl:when test="@name">
              <xsl:text>object </xsl:text>
              <xsl:value-of select="eo:escape(@name)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>anonymous object</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:element>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
