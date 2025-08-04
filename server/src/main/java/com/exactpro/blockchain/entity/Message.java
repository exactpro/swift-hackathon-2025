package com.exactpro.blockchain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("Message")
public class Message {
    @Id
    @Column("messageId")
    private String messageId;

    @Column("messageType")
    @JsonProperty("messageType")
    private String messageType;

    @Column("transferId")
    private Integer transferId;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    public Message() {
    }

    public Message(String messageType, Integer transferId, String content, Instant timestamp) {
        this.messageType = messageType;
        this.transferId = transferId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Integer getTransferId() {
        return transferId;
    }

    public void setTransferId(Integer transferId) {
        this.transferId = transferId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
