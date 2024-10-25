package com.application.mrmason.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class AdminUiEndPointId implements Serializable {

    private String systemId;
    private String ipUrlToUi;

    public AdminUiEndPointId(String systemId) {
        this.systemId = systemId;
    }

    public AdminUiEndPointId(String systemId, String ipUrlToUi) {
        this.systemId = systemId;
        this.ipUrlToUi = ipUrlToUi;
    }

    public AdminUiEndPointId() {

    }
}
