<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" id="incorrect-bytes-format">
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/program">
    <defects>
      <xsl:apply-templates select="objects/o"/>
    </defects>
  </xsl:template>
  <xsl:template match="o">
    <xsl:variable name="bytes" select="normalize-space(.)"/>
    <xsl:if test="not(matches($bytes, '^--|[0-9A-F]{2}(-|(-[0-9A-F]{2})+)$'))">
      <defect>
        <xsl:attribute name="line">
          <xsl:value-of select="if (@line) then @line else '0'"/>
        </xsl:attribute>
        <xsl:attribute name="severity">critical</xsl:attribute>
        <xsl:text>Incorrect bytes format: "</xsl:text>
        <xsl:value-of select="$bytes"/>
        <xsl:text>"</xsl:text>
      </defect>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
