<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:iso="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.13"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*">
        <xsl:element name="iso:{local-name()}">
            <xsl:apply-templates select="node()|@*"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="FIToFICstmrCdtTrf">
        <iso:Document xmlns:iso="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.13">
            <iso:FIToFICstmrCdtTrf>
                <xsl:apply-templates select="node()|@*"/>
            </iso:FIToFICstmrCdtTrf>
        </iso:Document>
    </xsl:template>

    <xsl:template match="GrpHdr">
        <iso:GrpHdr>
            <xsl:apply-templates select="node()|@*"/>
            <iso:NbOfTxs>1</iso:NbOfTxs>
            <iso:SttlmInf>
                <iso:SttlmMtd>CLRG</iso:SttlmMtd>
            </iso:SttlmInf>
        </iso:GrpHdr>
    </xsl:template>

    <xsl:template match="CdtTrfTxInf">
        <iso:CdtTrfTxInf>
            <iso:PmtId>
                <xsl:apply-templates select="EndToEndId"/>
            </iso:PmtId>
            <iso:IntrBkSttlmAmt>
                <xsl:attribute name="Ccy"><xsl:value-of select="IntrBkSttlmCcy"/></xsl:attribute>
                <xsl:value-of select="IntrBkSttlmAmt"/>
            </iso:IntrBkSttlmAmt>
            <xsl:apply-templates select="IntrBkSttlmDt"/>
            <iso:ChrgBr>SLEV</iso:ChrgBr>
            <iso:Dbtr>
                <xsl:apply-templates select="Dbtr/Nm"/>
            </iso:Dbtr>
            <iso:DbtrAcct>
                <iso:Id>
                    <xsl:apply-templates select="Dbtr/IBAN"/>
                </iso:Id>
            </iso:DbtrAcct>
            <iso:DbtrAgt>
                <iso:FinInstnId>
                    <xsl:apply-templates select="Dbtr/BICFI"/>
                </iso:FinInstnId>
            </iso:DbtrAgt>
            <iso:CdtrAgt>
                <iso:FinInstnId>
                    <xsl:apply-templates select="Cdtr/BICFI"/>
                </iso:FinInstnId>
            </iso:CdtrAgt>
            <iso:Cdtr>
                <xsl:apply-templates select="Cdtr/Nm"/>
            </iso:Cdtr>
            <iso:CdtrAcct>
                <iso:Id>
                    <xsl:apply-templates select="Cdtr/IBAN"/>
                </iso:Id>
            </iso:CdtrAcct>
            <iso:RmtInf>
                <iso:Ustrd><xsl:value-of select="RmtInf/text()"/></iso:Ustrd>
            </iso:RmtInf>
        </iso:CdtTrfTxInf>
    </xsl:template>

</xsl:stylesheet>
