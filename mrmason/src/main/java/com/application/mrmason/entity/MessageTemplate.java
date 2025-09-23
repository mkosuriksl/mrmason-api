package com.application.mrmason.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "promo_offer")
public class MessageTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String templateCode; // e.g., "PROMO_OFFER"

    @Column(columnDefinition = "TEXT")
    private String templateText; // e.g., "Store user ({suUserId}) sent promotional offers from {updatedBy}"

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTemplateCode() {
        return templateCode;
    }
    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }
    public String getTemplateText() {
        return templateText;
    }
    public void setTemplateText(String templateText) {
        this.templateText = templateText;
    }
}
