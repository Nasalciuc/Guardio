package com.example.gigahack_2025.network

object NetworkConfig {
    // Backend configuration
    // For Android emulator, use 10.0.2.2 to access localhost
    // For physical device, use your computer's IP address
    const val BASE_URL = "http://192.168.8.200:3001/"
    
    // Alternative URLs for different setups:
    // const val BASE_URL = "http://localhost:3000/" // For testing only
    // const val BASE_URL = "http://192.168.1.100:3000/" // Replace with your computer's IP
    
    // Timeout configurations
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
}
