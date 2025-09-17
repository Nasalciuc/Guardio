package com.example.gigahack_2025.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.Date

data class SecurityFile(
    val id: String,
    val name: String,
    val size: String,
    val uploadDate: Date,
    val status: FileStatus,
    val type: FileType
)

data class SecurityUrl(
    val id: String,
    val url: String,
    val domain: String,
    val uploadDate: Date,
    val status: UrlStatus
)

data class SecurityArticle(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val publishDate: Date,
    val severity: ArticleSeverity,
    val category: String,
    val read: Boolean = false
)

enum class FileStatus {
    SAFE, UNSAFE, SCANNING, ERROR
}

enum class UrlStatus {
    SAFE, UNSAFE, SCANNING, ERROR
}

enum class ArticleSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum class FileType {
    PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, TXT, ZIP, RAR, EXE, OTHER
}

object SecurityMockData {
    val mockFiles = listOf(
        SecurityFile(
            id = "1",
            name = "contract_document.pdf",
            size = "2.4 MB",
            uploadDate = Date(System.currentTimeMillis() - 86400000), // 1 day ago
            status = FileStatus.SAFE,
            type = FileType.PDF
        ),
        SecurityFile(
            id = "2",
            name = "suspicious_script.exe",
            size = "1.2 MB",
            uploadDate = Date(System.currentTimeMillis() - 172800000), // 2 days ago
            status = FileStatus.UNSAFE,
            type = FileType.EXE
        ),
        SecurityFile(
            id = "3",
            name = "financial_report.xlsx",
            size = "856 KB",
            uploadDate = Date(System.currentTimeMillis() - 3600000), // 1 hour ago
            status = FileStatus.SCANNING,
            type = FileType.XLSX
        ),
        SecurityFile(
            id = "4",
            name = "presentation.pptx",
            size = "5.1 MB",
            uploadDate = Date(System.currentTimeMillis() - 7200000), // 2 hours ago
            status = FileStatus.SAFE,
            type = FileType.PPTX
        )
    )

    val mockUrls = listOf(
        SecurityUrl(
            id = "1",
            url = "https://example.com/safe-document",
            domain = "example.com",
            uploadDate = Date(System.currentTimeMillis() - 1800000), // 30 minutes ago
            status = UrlStatus.SAFE
        ),
        SecurityUrl(
            id = "2",
            url = "https://malicious-site.com/download",
            domain = "malicious-site.com",
            uploadDate = Date(System.currentTimeMillis() - 3600000), // 1 hour ago
            status = UrlStatus.UNSAFE
        ),
        SecurityUrl(
            id = "3",
            url = "https://banking-portal.gov.md/login",
            domain = "banking-portal.gov.md",
            uploadDate = Date(System.currentTimeMillis() - 900000), // 15 minutes ago
            status = UrlStatus.SCANNING
        )
    )

    val mockArticles = listOf(
        SecurityArticle(
            id = "1",
            title = "New Phishing Campaign Targets Government Services",
            summary = "Security researchers have identified a new phishing campaign specifically targeting government digital services in Moldova.",
            content = "A sophisticated phishing campaign has been detected targeting users of government digital services. The attackers are using fake login pages that closely mimic official government portals. Users are advised to verify URLs and never enter credentials on suspicious websites.",
            publishDate = Date(System.currentTimeMillis() - 3600000), // 1 hour ago
            severity = ArticleSeverity.HIGH,
            category = "Phishing",
            read = false
        ),
        SecurityArticle(
            id = "2",
            title = "Critical Security Update for Digital Identity System",
            summary = "An important security update has been released for the digital identity system. All users are advised to update immediately.",
            content = "A critical security vulnerability has been identified and patched in the digital identity system. The update includes enhanced encryption and improved authentication mechanisms. Users will be automatically updated, but manual verification is recommended.",
            publishDate = Date(System.currentTimeMillis() - 7200000), // 2 hours ago
            severity = ArticleSeverity.CRITICAL,
            category = "System Update",
            read = true
        ),
        SecurityArticle(
            id = "3",
            title = "Best Practices for Secure File Sharing",
            summary = "Learn about secure methods for sharing sensitive documents through the digital platform.",
            content = "When sharing sensitive documents, always verify the recipient's identity and use encrypted channels. Avoid sharing documents via unsecured email or public cloud services. The government platform provides secure sharing capabilities with end-to-end encryption.",
            publishDate = Date(System.currentTimeMillis() - 86400000), // 1 day ago
            severity = ArticleSeverity.MEDIUM,
            category = "Best Practices",
            read = false
        ),
        SecurityArticle(
            id = "4",
            title = "Ransomware Alert: New Variant Detected",
            summary = "A new variant of ransomware has been detected targeting government systems. Immediate action required.",
            content = "Security teams have identified a new ransomware variant that specifically targets government infrastructure. The malware spreads through malicious email attachments and compromised websites. All users are advised to be extra cautious with email attachments and to report any suspicious activity immediately.",
            publishDate = Date(System.currentTimeMillis() - 172800000), // 2 days ago
            severity = ArticleSeverity.CRITICAL,
            category = "Malware",
            read = false
        )
    )

    fun getFileTypeIcon(type: FileType): ImageVector {
        return when (type) {
            FileType.PDF -> Icons.Filled.PictureAsPdf
            FileType.DOC, FileType.DOCX -> Icons.Filled.Description
            FileType.XLS, FileType.XLSX -> Icons.Filled.TableChart
            FileType.PPT, FileType.PPTX -> Icons.Filled.Slideshow
            FileType.TXT -> Icons.Filled.TextSnippet
            FileType.ZIP, FileType.RAR -> Icons.Filled.Archive
            FileType.EXE -> Icons.Filled.Build
            FileType.OTHER -> Icons.Filled.InsertDriveFile
        }
    }

    fun getStatusColor(status: FileStatus): String {
        return when (status) {
            FileStatus.SAFE -> "#4CAF50" // Green
            FileStatus.UNSAFE -> "#F44336" // Red
            FileStatus.SCANNING -> "#FFC107" // Orange
            FileStatus.ERROR -> "#9E9E9E" // Gray
        }
    }

    fun getUrlStatusColor(status: UrlStatus): String {
        return when (status) {
            UrlStatus.SAFE -> "#4CAF50" // Green
            UrlStatus.UNSAFE -> "#F44336" // Red
            UrlStatus.SCANNING -> "#FFC107" // Orange
            UrlStatus.ERROR -> "#9E9E9E" // Gray
        }
    }

    fun getSeverityColor(severity: ArticleSeverity): String {
        return when (severity) {
            ArticleSeverity.LOW -> "#4CAF50" // Green
            ArticleSeverity.MEDIUM -> "#FFC107" // Orange
            ArticleSeverity.HIGH -> "#FF9800" // Dark Orange
            ArticleSeverity.CRITICAL -> "#F44336" // Red
        }
    }
}
