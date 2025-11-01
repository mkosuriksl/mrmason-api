package com.application.mrmason.entity;

import java.time.LocalDateTime;
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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fr_login")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrLogin implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fr_userid", nullable = false, unique = true)
    private String frUserid;

    @Column(name = "fr_email")
    private String frEmail;

    @Column(name = "fr_mobile")
    private String frMobile;

    @Column(name = "pwd", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "reg_source", nullable = false)
    private RegSource regSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Column(name = "email_verified", length = 5)
    private String emailVerified; // "yes" / "no"

    @Column(name = "mobile_verified", length = 5)
    private String mobileVerified; // "yes" / "no"

    @Column(name = "status", length = 10)
    private String status; // "ACTIVE" / "INACTIVE"

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        this.updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
    
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

