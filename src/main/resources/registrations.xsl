<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
    <registrations>
      <xsl:apply-templates/>
    </registrations>
  </xsl:template>

  <xsl:template match="text()"/>

  <xsl:template match="RegisteredIPStations">
    <station>
      <xsl:attribute name="ext">
        <xsl:value-of select="Station_Extension/text()"/>
      </xsl:attribute>
      <xsl:attribute name="addr">
        <xsl:value-of select="Station_IP_Address/text()"/>
      </xsl:attribute>
    </station>
  </xsl:template>
  
</xsl:stylesheet>