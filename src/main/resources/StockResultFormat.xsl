<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="html" />

	<xsl:template match="/">
		<ul>
			<xsl:for-each select="/root/element">
				<li></li>	 
		    </xsl:for-each>
	    </ul>
	</xsl:template>

	<xsl:template match="*|@*">
		<!-- ignore -->
	</xsl:template>

</xsl:stylesheet>
