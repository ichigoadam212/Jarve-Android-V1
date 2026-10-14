package com.ichigoadam.jarvis.friday

import android.content.Context
import com.ichigoadam.jarvis.core.JarvisHealthReader

/**
 * FRIDAY's independent command and status module.
 *
 * This module is called by FRIDAY's isolated fallback service and keeps
 * FRIDAY's supported commands separate from JARVIS command handling.
 */
class FridayModule(private val context: Context) {

    fun analyze(command: String): FridayResult {
        val normalized = command.trim().lowercase()

        val dictionaryQuery = when {
            normalized.startsWith("معنى ") -> command.trim().substringAfter("معنى ").trim()
            normalized.startsWith("ترجم ") -> command.trim().substringAfter("ترجم ").trim()
            normalized.startsWith("قاموس ") -> command.trim().substringAfter("قاموس ").trim()
            normalized.startsWith("define ") -> command.trim().substringAfter("define ").trim()
            normalized.startsWith("dictionary ") -> command.trim().substringAfter("dictionary ").trim()
            normalized.startsWith("meaning ") -> command.trim().substringAfter("meaning ").trim()
            normalized.startsWith("what does ") && normalized.endsWith(" mean") ->
                command.trim().substringAfter("what does ").removeSuffix(" mean").trim()
            else -> ""
        }
        if (dictionaryQuery.isNotBlank()) {
            val found = com.ichigoadam.jarvis.memory.EnglishDictionary.lookup(dictionaryQuery, com.ichigoadam.jarvis.voice.AssistantLanguage.isArabic(context))
            return FridayResult(
                handled = true,
                response = found ?: if (com.ichigoadam.jarvis.voice.AssistantLanguage.isArabic(context))
                    "مش لاقية الكلمة دي في القاموس المحلي حالياً."
                else "I couldn't find that word in the offline dictionary yet."
            )
        }

        return when {
            normalized == "friday status" ||
            normalized == "status" ||
            normalized == "حالة فرايداي" ||
            normalized == "الحالة" -> {
                val health = JarvisHealthReader.read(context)
                FridayResult(
                    handled = true,
                    response = if (com.ichigoadam.jarvis.voice.AssistantLanguage.isArabic(context)) {
                        if (health.running) {
                            if (health.appControlConnected)
                                "فرايداي تعمل، وجارفيس متصل وخدمة التحكم في التطبيقات مفعلة"
                            else "فرايداي تعمل، وجارفيس متصل لكن خدمة التحكم في التطبيقات غير مفعلة"
                        } else "فرايداي تعمل بشكل مستقل. جارفيس غير متصل حالياً، ويمكنني استلام الأوامر المدعومة."
                    } else {
                        if (health.running) {
                            if (health.appControlConnected)
                                "FRIDAY is active. JARVIS is connected and app control is enabled."
                            else "FRIDAY is active. JARVIS is connected, but app control is not enabled."
                        } else "FRIDAY is running independently. JARVIS is not connected; I can handle supported commands."
                    }
                )
            }

            normalized == "friday analyze" ||
            normalized == "analyze" ||
            normalized == "حلل باستخدام فرايداي" ||
            normalized == "حلل" -> {
                FridayResult(
                    handled = true,
                    response = if (com.ichigoadam.jarvis.voice.AssistantLanguage.isArabic(context)) "تم تمرير الطلب إلى طبقة التحليل الداخلية" else "The request was passed to the internal analysis layer."
                )
            }

            else -> FridayResult()
        }
    }

    fun capabilities(): Set<FridayCapability> = setOf(
        FridayCapability.ANALYSIS,
        FridayCapability.CONTEXT,
        FridayCapability.SYSTEM_STATUS,
        FridayCapability.RESOURCE_AWARENESS
    )
}
