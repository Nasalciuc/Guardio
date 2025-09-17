package com.example.gigahack_2025.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @POST("api/scan")
    @Headers("Content-Type: application/json")
    suspend fun scanUrl(@Body request: UrlScanRequest): Response<ScanResponse>
    
    @POST("api/scan/file")
    @Multipart
    suspend fun scanFile(
        @Part file: MultipartBody.Part
    ): Response<FileScanResponse>
    
    @POST("api/report")
    @Headers("Content-Type: application/json")
    suspend fun reportProblem(@Body request: ReportRequest): Response<ReportResponse>
}

data class UrlScanRequest(
    val url: String
)

data class ScanResponse(
    val verdict: String,
    val details: String
)

data class FileScanResponse(
    val filename: String,
    val size: Long,
    val sha256: String,
    val verdict: String,
    val vt_status: String,
    val vt_id: String?,
    val note: String
)

data class ReportRequest(
    val type: String,
    val description: String,
    val screenshot_base64: String? = null,
    val email: String? = null,
    val source_item: String? = null,
    val meta: Map<String, Any>? = null
)

data class ReportResponse(
    val status: String,
    val id: String?,
    val stub_mode: Boolean,
    val downgraded_no_meta: Boolean
)
