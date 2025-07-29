<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
        xmlns:iso="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.13"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8" indent="no"/>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@iso:*">
        <xsl:attribute name="{local-name()}">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="iso:*">
        <xsl:element name="{local-name()}">
            <xsl:apply-templates select="node()|@*"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="iso:Document">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xsl:template match="iso:GrpHdr">
        <GrpHdr>
            <xsl:apply-templates select="iso:MsgId|iso:CreDtTm"/>
        </GrpHdr>
    </xsl:template>

    <xsl:template match="iso:CdtTrfTxInf">
        <CdtTrfTxInf>
            <xsl:apply-templates select="iso:PmtId|iso:IntrBkSttlmAmt|iso:IntrBkSttlmDt"/>
            <Dbtr>
                <xsl:apply-templates select="iso:Dbtr"/>
                <xsl:apply-templates select="iso:DbtrAcct"/>
                <xsl:apply-templates select="iso:DbtrAgt"/>
            </Dbtr>
            <Cdtr>
                <xsl:apply-templates select="iso:Cdtr"/>
                <xsl:apply-templates select="iso:CdtrAcct"/>
                <xsl:apply-templates select="iso:CdtrAgt"/>
            </Cdtr>
            <xsl:apply-templates select="iso:RmtInf"/>
        </CdtTrfTxInf>
    </xsl:template>

    <xsl:template match="iso:PmtId">
        <xsl:apply-templates select="iso:EndToEndId"/>
    </xsl:template>

    <xsl:template match="iso:IntrBkSttlmAmt">
        <IntrBkSttlmAmt>
            <xsl:value-of select="text()"/>
        </IntrBkSttlmAmt>
        <IntrBkSttlmCcy>
            <xsl:value-of select="@Ccy"/>
        </IntrBkSttlmCcy>
    </xsl:template>

    <xsl:template match="iso:Cdtr|iso:Dbtr">
        <Nm>
            <xsl:value-of select="iso:Nm/text()"/>
        </Nm>
    </xsl:template>

    <xsl:template match="iso:CdtrAgt|iso:DbtrAgt">
        <BICFI>
            <xsl:value-of select="iso:FinInstnId/iso:BICFI/text()"/>
        </BICFI>
    </xsl:template>

    <xsl:template match="iso:CdtrAcct|iso:DbtrAcct">
        <IBAN>
            <xsl:value-of select="iso:Id/iso:IBAN/text()"/>
        </IBAN>
    </xsl:template>

    <xsl:template match="iso:RmtInf">
        <RmtInf>
            <xsl:value-of select="iso:Ustrd/text()"/>
        </RmtInf>
    </xsl:template>

</xsl:stylesheet>