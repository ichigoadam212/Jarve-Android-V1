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

        return when {
            normalized == "friday status" ||
            normalized == "status" ||
            normalized == "حالة فرايداي" ||
            normalized == "الحالة" -> {
                val health = JarvisHealthReader.read(context)
                FridayResult(
                    handled = true,
                    response = if (health.running) {
                        if (health.appControlConnected)
                            "فرايداي تعمل، وجارفيس متصل وخدمة التحكم في التطبيقات مفعلة"
                        else
                            "فرايداي تعمل، وجارفيس متصل لكن خدمة التحكم في التطبيقات غير مفعلة"
                    } else {
                        "فرايداي تعمل بشكل مستقل. جارفيس غير متصل حالياً، ويمكنني استلام الأوامر المدعومة."
                    }
                )
            }

            normalized == "friday analyze" ||
            normalized == "analyze" ||
            normalized == "حلل باستخدام فرايداي" ||
            normalized == "حلل" -> {
                FridayResult(
                    handled = true,
                    response = "تم تمرير الطلب إلى طبقة التحليل الداخلية"
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
