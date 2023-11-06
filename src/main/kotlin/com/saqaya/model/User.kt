package com.saqaya.model

import com.saqaya.dto.UserResponse
import jakarta.persistence.*
import org.modelmapper.ModelMapper
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
class User(
    @Id @Column(name = "id", nullable = false) var id: String? = null,
    @Column(nullable = false) var firstName: String? = null,
    @Column(nullable = false) var lastName: String? = null,
    @Column(nullable = false) var email: String? = null,
    @Column(nullable = false) @JvmField var password: String? = null,
    @Column(nullable = false) var forcePasswordChange: Boolean = false,
    @Column(nullable = false) var marketingConsent: Boolean = false,

    @Enumerated(EnumType.STRING) var role: Role = Role.USER,

    @Column(nullable = false) var enabled: Boolean = true,
    @Column(nullable = false) var accountNonExpired: Boolean = true,
    @Column(nullable = false) var accountNonLocked: Boolean = true,
    @Column(nullable = false) var credentialsNonExpired: Boolean = true
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_" + role.name));

    override fun getPassword(): String = password.toString()

    override fun getUsername(): String = email.toString()

    override fun isAccountNonExpired(): Boolean = accountNonExpired

    override fun isAccountNonLocked(): Boolean = accountNonLocked

    override fun isCredentialsNonExpired(): Boolean = credentialsNonExpired

    override fun isEnabled(): Boolean = enabled

    fun toUserDto(): UserResponse {
        val modelMapper = ModelMapper()
        return modelMapper.map(this, UserResponse::class.java)
    }
}


enum class Role {
    USER,
    ADMIN
}