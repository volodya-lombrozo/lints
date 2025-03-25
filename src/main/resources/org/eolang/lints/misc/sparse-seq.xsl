<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eo="https://www.eolang.org" id="sparse-seq" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="o" mode="arguments" as="xs:integer">
    <xsl:choose>
      <xsl:when test="@base='.with' and count(o)=2">
        <xsl:variable name="nested">
          <xsl:apply-templates select="o[1]" mode="arguments"/>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="$nested!=-1">
            <xsl:value-of select="$nested + 1"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="-1"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="@base='Q.org.eolang.tuple.empty'">
        <xsl:value-of select="0"/>
      </xsl:when>
      <xsl:when test="@base='Q.org.eolang.tuple.empty.with' and count(o)=1">
        <xsl:value-of select="1"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="-1"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base='Q.org.eolang.seq']">
        <xsl:variable name="objects">
          <xsl:apply-templates select="o[1]" mode="arguments"/>
        </xsl:variable>
        <xsl:if test="$objects = 1">
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
            <xsl:text>The "Q.org.eolang.seq" object has only one object inside</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
