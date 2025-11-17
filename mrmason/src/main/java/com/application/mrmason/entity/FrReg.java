package com.application.mrmason.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.application.mrmason.enums.RegSource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "fr_reg")
public class FrReg implements UserDetails {

    @Id
    @Column(name = "frUserId")
    private String frUserId;

    @Column(nullable = false, unique = true)
    private String frEmail;

    @Column(nullable = true, unique = true)
    private String frMobile;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String frLinkedInProfile;

    @Column(name = "email_verified")
    private String emailVerified = "no";

    @Column(name = "mobile_verified")
    private String mobileVerified = "no";

    @Enumerated(EnumType.STRING)
    @Column(name = "reg_source", nullable = false)
    private RegSource regSource;

    private String status = "ACTIVE";

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "subCategory")
    private String subCategory;

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        // Generate unique user ID if missing
        if (this.frUserId == null || this.frUserId.isEmpty()) {
            DateTimeFormatter idFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
            this.frUserId = "FR" + now.format(idFormatter);
        }

        // Set updated date to current timestamp
        this.updatedDate = now;

        // Default values
        if (this.emailVerified == null) this.emailVerified = "no";
        if (this.mobileVerified == null) this.mobileVerified = "no";
        if (this.status == null) this.status = "ACTIVE";
    }

    // --- Spring Security Overrides ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (getUserType() == null) {
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + getUserType().name()));
    }

    @Override
    public String getUsername() {
        return this.frEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
