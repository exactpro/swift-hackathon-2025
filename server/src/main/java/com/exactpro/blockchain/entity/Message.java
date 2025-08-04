package com.exactpro.blockchain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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

    public Message() {
    }

    public Message(String messageType, Integer transferId, String content) {
        this.messageType = messageType;
        this.transferId = transferId;
        this.content = content;
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
