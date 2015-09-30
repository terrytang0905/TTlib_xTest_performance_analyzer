<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="html" />

	<xsl:template match="/">
		<html>
			<head></head>
			<body>
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="suite">
		<H1>
			<xsl:value-of select="@name" />
			-
			<xsl:value-of select="@id" />
		</H1>
		<xsl:apply-templates select="xTest" />
		<xsl:apply-templates select="xDB" />
		<xsl:apply-templates select="system" />
		<xsl:apply-templates select="suiteParameters" />
		<xsl:apply-templates select="phase" />
		<xsl:apply-templates select="test" />
		<xsl:apply-templates select="time" />
	</xsl:template>

	<xsl:template match="xTest">
		<p>
			xTest version=
			<xsl:value-of select="version" />
		</p>
	</xsl:template>

	<xsl:template match="xDB">
		<p>
			xDB version=
			<xsl:value-of select="version" />
			, branch=
			<xsl:value-of select="branch" />
			, build=
			<xsl:value-of select="build" />
			, jarType=
			<xsl:value-of select="jarType" />
		</p>
	</xsl:template>

	<xsl:template match="system">
		<p>
			cpuCount=
			<xsl:value-of select="cpus/@count" />
			,
			memory=
			<xsl:value-of select="memory/totalSize" />
		</p>
	</xsl:template>

	<xsl:template match="suiteParameters">
		<p>
			<xsl:value-of select="." />,
		</p>
	</xsl:template>

	<xsl:template match="phase">
		<p>
			Phase type= <xsl:value-of select="@type" />
		</p>
		<xsl:apply-templates select="time" />
		<table class="table table-striped">
			<thead>
				<tr>
					<th>actionName</th>
					<th>actionAspects</th>
					<th>nrThreads</th>
					<th>threadName</th>
					<th>duration</th>
					<th>execCount</th>
					<th>execRate</th>
					<th>deadlockCount</th>
					<th>deadlockRate</th>
					<th>avgExecTime</th>
					<th>avgRspTime</th>
					<th>maxRspTime</th>
					<th>minRspTime</th>
					<th>stdDevRspTime</th>
				</tr>
			</thead>
			<tbody>
				<xsl:apply-templates select="action" />
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="time">
		<table class="table table-striped">
			<thead>
				<tr>
					<th>duration</th>
					<th>startTime</th>
					<th>endTime</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>
						<xsl:value-of select="@duration" />
					</td>
					<td>
						<xsl:value-of select="@startTime" />
					</td>
					<td>
						<xsl:value-of select="@endTime" />
					</td>
				</tr>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="action">
		<tr>
			<td>
				<xsl:value-of select="@name" />
			</td>
			<td>
				<xsl:value-of select="aspects" />
			</td>
			<td>
				<xsl:value-of select="nrThreads" />
			</td>
			<xsl:for-each select="thread">
				<td>
					<xsl:value-of select="@name" />
				</td>
				<td>
					<xsl:value-of select="duration" />
				</td>
				<td>
					<xsl:value-of select="execCount" />
				</td>
				<td>
					<xsl:value-of select="execRate" />
				</td>
				<td>
					<xsl:value-of select="dlCount" />
				</td>
				<td>
					<xsl:value-of select="dlRate" />
				</td>
				<td>
					<xsl:value-of select="avgExecTime" />
				</td>
				<xsl:for-each select="rspTime">
					<td>
						<xsl:value-of select="@avg" />
					</td>
					<td>
						<xsl:value-of select="@max" />
					</td>
					<td>
						<xsl:value-of select="@min" />
					</td>
					<td>
						<xsl:value-of select="@stdDev" />
					</td>
				</xsl:for-each>
			</xsl:for-each>
		</tr>
	</xsl:template>

	<xsl:template match="test">
		<p>
			Test name= <xsl:value-of select="@name" />
		</p>
		<p>
			<xsl:value-of select="aspects" />
		</p>
		<xsl:apply-templates select="time" />
		<xsl:apply-templates select="phase" />
	</xsl:template>


	<xsl:template match="*|@*">
		<!-- ignore -->
	</xsl:template>

</xsl:stylesheet>
