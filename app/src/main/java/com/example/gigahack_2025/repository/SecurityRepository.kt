package com.example.gigahack_2025.repository

import android.content.Context
import android.net.Uri
import com.example.gigahack_2025.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class SecurityRepository {
    
    private val apiService = NetworkClient.apiService
    
    suspend fun scanUrl(url: String): Result<ScanResponse> = withContext(Dispatchers.IO) {
        try {
            val request = UrlScanRequest(url)
            val response = apiService.scanUrl(request)
            
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    Result.success(body)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun scanFile(context: Context, uri: Uri): Result<FileScanResponse> = withContext(Dispatchers.IO) {
        try {
            // Convert URI to File
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = getFileName(context, uri)
            val file = File(context.cacheDir, fileName)
            
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            
            // Create multipart request
            val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", fileName, requestFile)
            
            val response = apiService.scanFile(body)
            
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    Result.success(body)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun reportProblem(
        type: String,
        description: String,
        email: String? = null,
        screenshotBase64: String? = null,
        affectedResources: String? = null,
        detectionDateIso: String? = null
    ): Result<ReportResponse> = withContext(Dispatchers.IO) {
        try {
            val request = ReportRequest(
                type = type,
                description = description,
                email = email,
                screenshot_base64 = screenshotBase64,
                meta = buildMap {
                    affectedResources?.let { put("affected_resources", it) }
                    detectionDateIso?.let { put("detection_date", it) }
                }
            )
            
            val response = apiService.reportProblem(request)
            
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    Result.success(body)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getFileName(context: Context, uri: Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameIndex) ?: "unknown_file"
        } ?: "unknown_file"
    }
}
