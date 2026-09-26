package com.ichigoadam.jarvis.friday

import android.content.Context
import com.ichigoadam.jarvis.core.JarvisHealthReader

/**
 * Internal intelligence layer of JARVIS.
 *
 * FRIDAY is not a second assistant. JARVIS Core owns the user-facing
 * identity, permissions and final response.
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
                            "وحدة التحليل جاهزة وخدمة التحكم متصلة"
                        else
                            "وحدة التحليل جاهزة لكن خدمة التحكم غير مفعلة"
                    } else {
                        "وحدة التحليل غير متاحة لأن جارفيس غير متصل"
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
