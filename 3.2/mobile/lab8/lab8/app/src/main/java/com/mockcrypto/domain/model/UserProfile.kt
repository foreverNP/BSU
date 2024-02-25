package com.mockcrypto.domain.model

data class UserProfile(
    val userId: String,
    val username: String,
    val email: String,
    val isLoggedIn: Boolean,
    val createdAt: Long // timestamp in milliseconds
) 