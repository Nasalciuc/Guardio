package com.example.gigahack_2025.ui.screens

import android.net.Uri
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.res.stringResource
import android.util.Base64
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.gigahack_2025.ui.theme.*
import com.example.gigahack_2025.repository.SecurityRepository
import kotlinx.coroutines.*
import java.util.regex.Pattern
import com.example.gigahack_2025.R
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.activity.compose.BackHandler
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.Key
// removed KeyEventType/nativeKeyCode for broader compatibility

private fun getDisplayName(context: Context, uri: Uri): String {
    return try {
        val resolver = context.contentResolver
        val cursor = resolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)
        var name: String? = null
        cursor?.use {
            if (it.moveToFirst()) {
                val idx = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (idx >= 0) name = it.getString(idx)
            }
        }
        if (name.isNullOrBlank()) {
            // Fallback: attempt to parse last path segment
            name = uri.lastPathSegment
        }
        if (name.isNullOrBlank()) name = context.getString(R.string.selected_file_fallback)
        // Strip SAF pseudo prefixes like msf: or primary:
        name = name!!.substringAfterLast(":").removePrefix("msf").removePrefix(":")
            .replace(Regex("^\n+"), "")
        name!!.ifBlank { context.getString(R.string.selected_file_fallback) }
    } catch (e: Exception) {
        context.getString(R.string.selected_file_fallback)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportProblemScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedProblemType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var affectedResources by remember { mutableStateOf("") }
    var detectionDateMillis by remember { mutableStateOf<Long?>(null) }
    var isDatePickerOpen by remember { mutableStateOf(false) }
    var isDropdownOpen by remember { mutableStateOf(false) }
    var isEmailFocused by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedAttachment by remember { mutableStateOf<Uri?>(null) }
    var selectedAttachmentName by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val repository = remember { SecurityRepository() }
    val scope = rememberCoroutineScope()

    // Prefill from hosting activity extras (if launched via share-to-report)
    LaunchedEffect(Unit) {
        val act = (context as? android.app.Activity) ?: return@LaunchedEffect
        val preDesc = act.intent?.getStringExtra("report_prefill_description")
        val preUri = act.intent?.getParcelableExtra<Uri>("report_prefill_file_uri")
        if (!preDesc.isNullOrBlank() && description.isBlank()) description = preDesc
        if (preUri != null && selectedAttachment == null) {
            selectedAttachment = preUri
            selectedAttachmentName = getDisplayName(context, preUri)
        }
    }
    
    val problemTypes = listOf(
        stringResource(id = R.string.problem_type_mal_13),
        stringResource(id = R.string.problem_type_thr_01),
        stringResource(id = R.string.problem_type_soc_10),
        stringResource(id = R.string.problem_type_com_07),
        stringResource(id = R.string.problem_type_zro_18),
        stringResource(id = R.string.problem_type_vul_02),
        stringResource(id = R.string.problem_type_phi_12),
        stringResource(id = R.string.problem_type_exp_03),
        stringResource(id = R.string.problem_type_inf_11),
        stringResource(id = R.string.problem_type_ops_20),
        stringResource(id = R.string.problem_type_mra_16),
        stringResource(id = R.string.problem_type_mbn_15),
        stringResource(id = R.string.problem_type_mwr_14),
        stringResource(id = R.string.problem_type_dos_08),
        stringResource(id = R.string.problem_type_mis_06),
        stringResource(id = R.string.problem_type_rpt_21),
        stringResource(id = R.string.problem_type_oth_99),
        stringResource(id = R.string.problem_type_int_17)
    )
    
    // Email validation
    fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
        )
        return emailPattern.matcher(email).matches()
    }

    fun readUriAsBase64(uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val bytes = input.readBytes()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            null
        }
    }

    // Simple heuristic type inference based on keywords in description
    fun inferType(desc: String): String? {
        val d = desc.lowercase(Locale.getDefault())
        return when {
            d.contains("phish") || d.contains("fals") || d.contains("parola") -> context.getString(R.string.problem_type_phi_12)
            d.contains("ransom") || d.contains("criptare") -> context.getString(R.string.problem_type_mra_16)
            d.contains("botnet") -> context.getString(R.string.problem_type_mbn_15)
            d.contains("worm") -> context.getString(R.string.problem_type_mwr_14)
            d.contains("zero-day") || d.contains("0-day") || d.contains("0day") -> context.getString(R.string.problem_type_zro_18)
            d.contains("dos") || d.contains("denial") -> context.getString(R.string.problem_type_dos_08)
            d.contains("vulnerab") || d.contains("cve-") -> context.getString(R.string.problem_type_vul_02)
            d.contains("config") || d.contains("misconfig") -> context.getString(R.string.problem_type_mis_06)
            d.contains("false positive") || d.contains("nu e malware") -> context.getString(R.string.problem_type_rpt_21)
            else -> null
        }
    }

    // React to description changes to auto-suggest a problem type if empty
    LaunchedEffect(description) {
        if (selectedProblemType.isBlank() && description.length >= 8) {
            inferType(description)?.let { inferred ->
                selectedProblemType = inferred
            }
        }
    }
    
    val isFormValid = selectedProblemType.isNotBlank() && 
                     description.isNotBlank() && 
                     (email.isBlank() || isValidEmail(email))
    
    // File picker launcher (single file, like file scan)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = OpenDocument()
    ) { uri: Uri? ->
        selectedAttachment = uri
        selectedAttachmentName = uri?.let { getDisplayName(context, it) }
    }

    fun submitReport() {
        if (isFormValid) {
            isLoading = true
            scope.launch {
                try {
                    val screenshotBase64 = withContext(Dispatchers.IO) {
                        selectedAttachment?.let { first ->
                            readUriAsBase64(first)
                        }
                    }
                    val detectionDateIso = withContext(Dispatchers.Default) {
                        detectionDateMillis?.let { millis ->
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
                        }
                    }
                    val result = repository.reportProblem(
                        type = selectedProblemType,
                        description = description,
                        email = if (email.isNotBlank()) email else null,
                        screenshotBase64 = screenshotBase64,
                        affectedResources = affectedResources.ifBlank { null },
                        detectionDateIso = detectionDateIso
                    )
                    isLoading = false
                    result.fold(
                        onSuccess = { _ ->
                            // Navigate back then finish the Activity so app exits if this was a terminal flow
                            onBackClick()
                            val act = (context as? android.app.Activity)
                            act?.runOnUiThread {
                                // Slight delay to allow navigation stack to process before finishing
                                act.window?.decorView?.postDelayed({
                                    act.finishAffinity()
                                }, 150)
                            }
                        },
                        onFailure = { _ ->
                            isLoading = false
                        }
                    )
                } catch (e: Exception) {
                    isLoading = false
                }
            }
        }
    }
    
    BackHandler(enabled = true) {
        onBackClick()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FigmaWhite)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back),
                        tint = FigmaDarkBlue
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.report_incident_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Normal,
                    color = FigmaDarkBlue
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FigmaWhite)
                    .navigationBarsPadding()
                    .padding(8.dp, 16.dp, 16.dp, 16.dp)
            ) {
                Button(
                    onClick = { submitReport() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FigmaPrimaryBlue,
                        disabledContainerColor = FigmaPrimaryBlue
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = FigmaWhite,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.send),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = FigmaWhite
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(FigmaWhite)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FigmaWhite)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Type of incident section - matches Figma select-input
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Figma: gap-8
                ) {
                    // Label
                    Text(
                        text = stringResource(id = R.string.type_of_incident),
                        style = MaterialTheme.typography.bodyMedium, // Figma: Onest 14px weight-400
                        fontWeight = FontWeight.Normal,
                        color = FigmaDarkGray, // Figma: rgb(56, 56, 56)
                        modifier = Modifier.padding(bottom = 4.dp) // Figma: gap-4
                    )
                    
                    // Dropdown - matches Figma design
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDropdownOpen = true },
                        colors = CardDefaults.cardColors(containerColor = FigmaWhite),
                        shape = RoundedCornerShape(12.dp), // Figma: radius-12
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(2.dp, Color(0xFFD9D9D9), RoundedCornerShape(12.dp)) // Figma: border-2 rgb(217, 217, 217)
                                .padding(16.dp) // Figma: padding-16
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (selectedProblemType.isEmpty()) stringResource(id = R.string.select_type) else selectedProblemType,
                                    style = MaterialTheme.typography.bodyLarge, // Figma: Onest 16px weight-400
                                    fontWeight = FontWeight.Normal,
                                    color = if (selectedProblemType.isEmpty()) FigmaGray else FigmaBlack // Figma: rgb(18, 18, 18)
                                )
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = stringResource(id = R.string.dropdown),
                                    tint = FigmaGray
                                )
                            }
                        }
                    }
                }
                
                // Description section - matches Figma text-area
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Figma: gap-8
                ) {
                    // Label
                    Text(
                        text = stringResource(id = R.string.description_of_incident),
                        style = MaterialTheme.typography.bodyMedium, // Figma: Onest 14px weight-400
                        fontWeight = FontWeight.Normal,
                        color = FigmaDarkGray, // Figma: rgb(56, 56, 56)
                        modifier = Modifier.padding(bottom = 4.dp) // Figma: gap-4
                    )
                    
                    // Text area - matches Figma design
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { 
                            Text(
                                stringResource(id = R.string.describe_incident_placeholder),
                                color = FigmaGray
                            ) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .onPreviewKeyEvent { kev ->
                                if (kev.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
                                    submitReport(); true
                                } else false
                            },
                        shape = RoundedCornerShape(12.dp), // Figma: radius-12
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD9D9D9), // Figma: rgb(217, 217, 217)
                            unfocusedBorderColor = Color(0xFFD9D9D9),
                            unfocusedContainerColor = FigmaWhite
                        ),
                        maxLines = 6,
                        textStyle = LocalTextStyle.current.copy(color = FigmaBlack)
                    )
                }

                // Affected resources/systems section
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.affected_resources_label),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = FigmaDarkGray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = affectedResources,
                        onValueChange = { affectedResources = it },
                        placeholder = {
                            Text(
                                stringResource(id = R.string.affected_resources_placeholder),
                                color = FigmaGray
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onPreviewKeyEvent { kev ->
                                if (kev.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_ENTER) { submitReport(); true } else false
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD9D9D9),
                            unfocusedBorderColor = Color(0xFFD9D9D9),
                            unfocusedContainerColor = FigmaWhite
                        ),
                        singleLine = false,
                        maxLines = 3,
                        textStyle = LocalTextStyle.current.copy(color = FigmaBlack)
                    )
                }

                // Detection date section
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.detection_date_label),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = FigmaDarkGray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDatePickerOpen = true },
                        colors = CardDefaults.cardColors(containerColor = FigmaWhite),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(2.dp, Color(0xFFD9D9D9), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            val displayDate = detectionDateMillis?.let { millis ->
                                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(millis))
                            } ?: stringResource(id = R.string.date_placeholder)
                            Text(
                                text = displayDate,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Normal,
                                color = if (detectionDateMillis == null) FigmaGray else FigmaBlack
                            )
                        }
                    }
                }
                
                // Attachment section - switch to generic file upload like file scan
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Figma: gap-8
                ) {
                    // Label with icon - matches Figma structure
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp) // Figma: gap-4
                    ) {
                        Text(
                            text = stringResource(id = R.string.attachment_optional),
                            style = MaterialTheme.typography.bodyMedium, // Figma: Onest 14px weight-400
                            fontWeight = FontWeight.Normal,
                            color = FigmaDarkGray // Figma: rgb(56, 56, 56)
                        )
                        // Icon placeholder (matches Figma structure)
                        Box(
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    // Upload input - generic file picker
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = FigmaWhite),
                        shape = RoundedCornerShape(12.dp), // Figma: radius-12
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp) // Figma: height-64
                                .border(2.dp, Color(0xFFD9D9D9), RoundedCornerShape(12.dp)) // Figma: border-2 rgb(217, 217, 217)
                                .padding(16.dp) // Figma: padding-16
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp) // Figma: gap-8
                            ) {
                                // Upload icon - matches Figma 24-image
                                Icon(
                                    imageVector = Icons.Filled.Upload,
                                    contentDescription = stringResource(id = R.string.upload),
                                    tint = FigmaGray,
                                    modifier = Modifier.size(24.dp)
                                )
                                
                                // Content area - matches Figma structure
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Figma: gap-8
                                ) {
                                    Text(
                                        text = selectedAttachmentName?.removePrefix("msf:") ?: stringResource(id = R.string.upload_file),
                                        style = MaterialTheme.typography.bodyLarge, // Figma: Onest 16px weight-400
                                        fontWeight = FontWeight.Normal,
                                        color = FigmaBlack // Figma: rgb(18, 18, 18)
                                    )
                                }
                                
                                // Upload button - matches Figma button-rectangular
                                Button(
                                    onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = FigmaPrimaryBlue
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.choose_file),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = FigmaWhite
                                    )
                                }
                            }
                        }
                    }
                    // Inline preview (image or text under 200KB)
                    selectedAttachment?.let { uri ->
                        val mime = remember(uri) { context.contentResolver.getType(uri) ?: "" }
                        val isImage = mime.startsWith("image/")
                        val isText = mime.startsWith("text/") || mime.endsWith("json") || mime.contains("xml")
                        if (isImage) {
                            // Use Coil AsyncImage (already in dependencies)
                            androidx.compose.foundation.Image(
                                painter = coil.compose.rememberAsyncImagePainter(model = uri),
                                contentDescription = stringResource(id = R.string.file_preview),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, FigmaLightGray, RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else if (isText) {
                            var textPreview by remember(uri) { mutableStateOf<String?>(null) }
                            LaunchedEffect(uri) {
                                withContext(Dispatchers.IO) {
                                    runCatching {
                                        context.contentResolver.openInputStream(uri)?.use { ins ->
                                            val bytes = ins.readBytes()
                                            if (bytes.size <= 200_000) {
                                                textPreview = bytes.toString(Charsets.UTF_8)
                                                    .lines()
                                                    .take(20)
                                                    .joinToString("\n")
                                            }
                                        }
                                    }
                                }
                            }
                            textPreview?.let { snippet ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = FigmaBackgroundGray)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(stringResource(id = R.string.file_preview), fontWeight = FontWeight.SemiBold, color = FigmaDarkBlue)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(snippet, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }

                    // Remove button for selected file
                    if (selectedAttachment != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = selectedAttachmentName?.removePrefix("msf:") ?: stringResource(id = R.string.selected_file_fallback),
                                style = MaterialTheme.typography.bodySmall,
                                color = FigmaDarkGray
                            )
                            IconButton(
                                onClick = {
                                    selectedAttachment = null
                                    selectedAttachmentName = null
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = stringResource(id = R.string.remove),
                                    tint = FigmaGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                
                // Reporter email section
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Figma: gap-16
                ) {
                    // Label
                    Text(
                        text = stringResource(id = R.string.reporter_email_label),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = FigmaDarkGray
                    )
                    
                    // Email input - matches Figma text-input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            emailError = if (it.isNotBlank() && !isValidEmail(it)) {
                                context.getString(R.string.email_invalid)
                            } else null
                        },
                        placeholder = {
                            Text(
                                if (isEmailFocused) stringResource(id = R.string.reporter_email_placeholder_focused) else stringResource(id = R.string.reporter_email_placeholder),
                                color = FigmaGray
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), // Figma: radius-12
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (emailError != null) Color.Red else Color(0xFFD9D9D9), // Figma: rgb(217, 217, 217)
                            unfocusedBorderColor = if (emailError != null) Color.Red else Color(0xFFD9D9D9),
                            unfocusedContainerColor = FigmaWhite
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        isError = emailError != null,
                        supportingText = emailError?.let { 
                            { Text(text = it, color = Color.Red) }
                        },
                        textStyle = LocalTextStyle.current.copy(color = FigmaBlack)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { submitReport() },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FigmaPrimaryBlue,
                    disabledContainerColor = FigmaPrimaryBlue
                ),
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = FigmaWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.send),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = FigmaWhite
                    )
                }
            }
        }
    }
    
                // Problem type dropdown dialog
    if (isDropdownOpen) {
        Dialog(onDismissRequest = { isDropdownOpen = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(containerColor = FigmaWhite),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.choose_type_of_incident),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = FigmaDarkBlue
                        )
                        IconButton(
                            onClick = { isDropdownOpen = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = stringResource(id = R.string.close),
                                tint = FigmaGray
                            )
                        }
                    }
                    
                    HorizontalDivider(color = FigmaLightGray)

                    // Scrollable list of problem types with a max height
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)) {
                        LazyColumn {
                            itemsIndexed(problemTypes) { index, type ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedProblemType = type
                                            isDropdownOpen = false
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = type,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = FigmaDarkBlue
                                    )
                                }
                                if (index < problemTypes.lastIndex) {
                                    HorizontalDivider(
                                        color = FigmaBackgroundGray,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Date picker dialog
    if (isDatePickerOpen) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = detectionDateMillis)
        DatePickerDialog(
            onDismissRequest = { isDatePickerOpen = false },
            confirmButton = {
                TextButton(onClick = {
                    detectionDateMillis = datePickerState.selectedDateMillis
                    isDatePickerOpen = false
                }) { Text(text = stringResource(id = R.string.select_date)) }
            },
            dismissButton = {
                TextButton(onClick = { isDatePickerOpen = false }) { Text(text = stringResource(id = R.string.close)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}