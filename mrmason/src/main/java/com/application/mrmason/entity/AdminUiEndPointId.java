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
    
    public AdminUiEndPointId() {
    }

    public AdminUiEndPointId(String systemId, String ipUrlToUi) {
        this.systemId = systemId;
        this.ipUrlToUi = ipUrlToUi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AdminUiEndPointId))
            return false;

        AdminUiEndPointId that = (AdminUiEndPointId) o;

        if (!systemId.equals(that.systemId))
            return false;
        return ipUrlToUi.equals(that.ipUrlToUi);
    }

    @Override
    public int hashCode() {
        int result = systemId.hashCode();
        result = 31 * result + ipUrlToUi.hashCode();
        return result;
    }
}
