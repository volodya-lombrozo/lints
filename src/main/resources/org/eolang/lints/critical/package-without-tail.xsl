<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="package-without-tail" version="2.0">
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="/program/metas/meta[head = 'package' and tail = '']">
        <defect>
          <xsl:attribute name="line">
            <xsl:value-of select="@line"/>
          </xsl:attribute>
          <xsl:attribute name="severity">critical</xsl:attribute>
          <xsl:text>The +package meta must have a tail</xsl:text>
        </defect>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
