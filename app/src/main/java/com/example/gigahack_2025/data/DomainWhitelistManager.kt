package com.example.gigahack_2025.data

import android.content.Context
import android.net.Uri

/**
 * Simple SharedPreferences-based whitelist for domains the user marked as trusted / auto-open.
 */
object DomainWhitelistManager {
    private const val PREFS = "domain_whitelist_prefs"
    private const val KEY_SET = "trusted_domains"

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isTrusted(ctx: Context, domain: String): Boolean {
        val set = prefs(ctx).getStringSet(KEY_SET, emptySet()) ?: emptySet()
        return domain.lowercase() in set
    }

    fun add(ctx: Context, domain: String) {
        val p = prefs(ctx)
        val set = (p.getStringSet(KEY_SET, emptySet()) ?: emptySet()).toMutableSet()
        set += domain.lowercase()
        p.edit().putStringSet(KEY_SET, set).apply()
    }

    fun remove(ctx: Context, domain: String) {
        val p = prefs(ctx)
        val set = (p.getStringSet(KEY_SET, emptySet()) ?: emptySet()).toMutableSet()
        if (set.remove(domain.lowercase())) {
            p.edit().putStringSet(KEY_SET, set).apply()
        }
    }

    fun extractDomain(url: String?): String? {
        return try {
            if (url.isNullOrBlank()) return null
            val host = Uri.parse(url).host ?: return null
            host.lowercase()
        } catch (_: Exception) { null }
    }
}
