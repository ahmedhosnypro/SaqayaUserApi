package com.saqaya.service

import com.saqaya.exception.ResourceAlreadyExistsException
import com.saqaya.model.User
import com.saqaya.repository.UserRepository
import com.saqaya.security.passwordEncoder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.Optional
import kotlin.math.floor


@Service
class UserService(
    private val userRepo: UserRepository,
) : UserDetailsService {
    fun save(user: User): Pair<User, String> {
        // Check if the email address is already in use
        if (userRepo.existsByEmail(user.email)) throw user.email?.let { ResourceAlreadyExistsException("User", it) }!!
        // Generate a SHA1 hash of the email address
        val userId: String = generateHash(user.email!!)
        user.id = userId

        // Encode the password
        if (user.password == null) {
            user.password = generateRandomPassword()
        }

        val password = user.password!!
        val passwordEncoder = BCryptPasswordEncoder()
        val encodedPassword = passwordEncoder.encode(user.getPassword())
        user.password = encodedPassword

        // Save the user to the database
        return Pair(userRepo.save(user), password)
    }

    fun generateHash(email: String): String {
        val md = MessageDigest.getInstance("SHA-1")
        md.update((email + SALT).toByteArray())
        val digest = md.digest()
        return bytesToHex(digest)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexString = StringBuilder()
        for (b in bytes) {
            hexString.append(String.format("%02x", b))
        }
        return hexString.toString()
    }

    fun find(): List<User> = userRepo.findAll()
    fun findById(id: String): Optional<User> = userRepo.findById(id)

    override fun loadUserByUsername(email: String): UserDetails {
        return userRepo.findByEmail(email)
            .orElseThrow { UsernameNotFoundException("User not found") }
    }

    companion object {
        const val SALT = "450d0b0db2bcf4adde5032eca1a7c416e560cf44"
    }
}

fun generateRandomPassword(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXY#abcdefghilkmnopqrstuvwxyz0123456789"
    val stringLength = 10
    val randomString = StringBuilder(stringLength)
    for (i in 0 until stringLength) {
        randomString.append(chars[floor(Math.random() * chars.length).toInt()])
    }
    return randomString.toString()
}