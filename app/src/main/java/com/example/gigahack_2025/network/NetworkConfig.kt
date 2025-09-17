package com.example.gigahack_2025.network

object NetworkConfig {
    // Backend configuration
    // Production URL - secure HTTPS endpoint
    const val BASE_URL = "https://api.guardio.app/"
    
    // Development URLs (commented out for production):
    // const val BASE_URL = "http://10.0.2.2:3000/" // For Android emulator
    // const val BASE_URL = "http://localhost:3000/" // For testing only
    // const val BASE_URL = "http://192.168.1.100:3000/" // For local development
    
    // Timeout configurations
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
}
