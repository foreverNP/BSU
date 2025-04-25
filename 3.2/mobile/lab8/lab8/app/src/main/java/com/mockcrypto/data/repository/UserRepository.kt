package com.mockcrypto.data.repository

import com.mockcrypto.domain.model.UserProfile
import kotlinx.coroutines.delay

interface UserRepository {
    suspend fun getUserProfile(): Result<UserProfile>
    suspend fun logout(): Result<Unit>
    suspend fun login(username: String, password: String): Result<UserProfile>
}

// Mock implementation
class MockUserRepository : UserRepository {
    // Mock user profile data
    private var mockUserProfile = UserProfile(
        userId = "demo123",
        username = "DemoUser",
        email = "demo@example.com",
        isLoggedIn = true,
        createdAt = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000 // 30 days ago
    )

    override suspend fun getUserProfile(): Result<UserProfile> {
        delay(500) // Simulate network delay
        return Result.success(mockUserProfile)
    }

    override suspend fun logout(): Result<Unit> {
        delay(500) // Simulate network delay
        mockUserProfile = mockUserProfile.copy(isLoggedIn = false)
        return Result.success(Unit)
    }

    override suspend fun login(username: String, password: String): Result<UserProfile> {
        delay(1000) // Simulate network delay
        // In a mock implementation, we'll accept any credentials
        mockUserProfile = mockUserProfile.copy(
            username = username,
            isLoggedIn = true
        )
        return Result.success(mockUserProfile)
    }
} 