<?xml version="1.0" encoding="UTF-8"?>
<!--
The MIT License (MIT)

Copyright (c) 2016-2024 Objectionary.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="line-is-absent" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <!--
  Here we go through all objects and find what their @base
  are referring to. If we find the object they refer to,
  everything is OK. If we don't, we report an error.
  -->
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:key name="objsNoLineByName" match="o[not(@line)]" use="@name"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="//o[@base and not(starts-with(@base, '.')) and @base!='$' and @base!='^']">
        <xsl:variable name="self" select="."/>
        <xsl:variable name="target" select="key('objsNoLineByName', $self/@base)"/>
        <xsl:if test="$target">
          <defect line="eo:lineno(@line)" severity="error">
            The @line attribute is absent at <xsl:value-of select="$target/@name"/>
          </defect>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
