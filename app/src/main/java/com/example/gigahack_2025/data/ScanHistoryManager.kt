package com.example.gigahack_2025.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

data class ScanHistoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val target: String, // file name or URL
    val type: String, // "file" | "url"
    val verdict: String, // "safe" | "suspicious" | "malicious"
    val timestamp: Long = System.currentTimeMillis()
)

object ScanHistoryManager {
    private const val PREFS_NAME = "scan_history_prefs"
    private const val KEY_HISTORY = "scan_history_json"
    private val gson = Gson()

    fun getEntries(context: Context): List<ScanHistoryEntry> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return runCatching {
            val type = object : TypeToken<List<ScanHistoryEntry>>() {}.type
            gson.fromJson<List<ScanHistoryEntry>>(json, type) ?: emptyList()
        }.getOrDefault(emptyList())
    }

    fun addEntry(context: Context, entry: ScanHistoryEntry) {
        val current = getEntries(context).toMutableList()
        current.add(0, entry) // newest first
        save(context, current)
    }

    fun clear(context: Context) {
        save(context, emptyList())
    }

    private fun save(context: Context, entries: List<ScanHistoryEntry>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(entries)
        prefs.edit().putString(KEY_HISTORY, json).apply()
    }
}


