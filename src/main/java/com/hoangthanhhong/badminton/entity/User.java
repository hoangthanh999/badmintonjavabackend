package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_phone", columnList = "phone"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 500)
    private String avatar;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "phone_verified")
    @Builder.Default
    private Boolean phoneVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loyalty_tier_id")
    private LoyaltyTier loyaltyTier;

    // === RELATIONSHIPS ===

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LoyaltyPoint> loyaltyPoints = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ChatParticipant> chatParticipants = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<TournamentParticipant> tournamentParticipants = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Attendance> attendances = new ArrayList<>();

    // *** THÊM MỚI: Referrals ***
    @OneToMany(mappedBy = "referrer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserReferral> referralsSent = new ArrayList<>(); // Những người mình giới thiệu

    @OneToMany(mappedBy = "referred", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserReferral> referralsReceived = new ArrayList<>(); // Được người khác giới thiệu

    // *** THÊM MỚI: Addresses ***
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserAddress> addresses = new ArrayList<>();

    // === USERDETAILS IMPLEMENTATION ===

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.BANNED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }

    // === HELPER METHODS ===

    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setUser(this);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setUser(null);
    }

    // *** Address Helper Methods ***
    public void addAddress(UserAddress address) {
        addresses.add(address);
        address.setUser(this);
    }

    public void removeAddress(UserAddress address) {
        addresses.remove(address);
        address.setUser(null);
    }

    public UserAddress getDefaultAddress() {
        return addresses.stream()
                .filter(UserAddress::getIsDefault)
                .findFirst()
                .orElse(null);
    }

    public void setDefaultAddress(UserAddress newDefault) {
        // Unset current default
        addresses.stream()
                .filter(UserAddress::getIsDefault)
                .forEach(UserAddress::unsetDefault);
        // Set new default
        newDefault.setAsDefault();
    }

    // *** Referral Helper Methods ***
    public void addReferral(UserReferral referral) {
        referralsSent.add(referral);
        referral.setReferrer(this);
    }

    public Integer getTotalSuccessfulReferrals() {
        return (int) referralsSent.stream()
                .filter(r -> r.getStatus() == com.hoangthanhhong.badminton.enums.ReferralStatus.COMPLETED)
                .count();
    }

    public Integer getTotalLoyaltyPoints() {
        return loyaltyPoints.stream()
                .mapToInt(LoyaltyPoint::getPoints)
                .sum();
    }

    public LoyaltyTier getLoyaltyTier() {
        return loyaltyTier;
    }

    public void setLoyaltyTier(LoyaltyTier loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }
}