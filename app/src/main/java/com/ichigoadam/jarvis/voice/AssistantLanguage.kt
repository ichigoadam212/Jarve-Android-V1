package com.ichigoadam.jarvis.voice

import android.content.Context

/** Shared language preference for both assistant interfaces and speech. Arabic is the default. */
object AssistantLanguage {
    private const val PREFS = "jarvis_language_settings"
    private const val KEY_LANGUAGE = "language"
    const val ARABIC = "ar"
    const val ENGLISH = "en"

    fun current(context: Context): String =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, ARABIC) ?: ARABIC

    fun isArabic(context: Context): Boolean = current(context) == ARABIC

    fun toggle(context: Context): String {
        val next = if (isArabic(context)) ENGLISH else ARABIC
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_LANGUAGE, next).apply()
        return next
    }

    fun speechTag(context: Context): String = if (isArabic(context)) "ar-EG" else "en-US"

    fun ui(context: Context, english: String, arabic: String): String =
        if (isArabic(context)) arabic else english

    fun ui(context: Context, text: String): String {
        val translations = mapOf(
            "SELECT YOUR ASSISTANT" to "اختار المساعد",
            "JARVIS  •  PRIMARY ASSISTANT" to "جارفيس • المساعد الأساسي",
            "F.R.I.D.A.Y.  •  INDEPENDENT ASSISTANT" to "فرايداي • المساعدة المستقلة",
            "Separate interfaces • Independent state • Guardian failover" to "واجهتان منفصلتان • حالة مستقلة • حماية واستعادة",
            "ANDROID V1  •  CORE CONTROL" to "أندرويد V1 • التحكم الأساسي",
            "●  CORE READY" to "● النواة جاهزة",
            "JARVIS Android V1\nStatus: Ready" to "جارفيس أندرويد V1\nالحالة: جاهز",
            "START JARVIS + GUARDIAN" to "تشغيل جارفيس والحارس",
            "●  CORE ONLINE" to "● النواة تعمل",
            "JARVIS Android V1\nStatus: Running + Guardian" to "جارفيس أندرويد V1\nالحالة: يعمل مع الحارس",
            "VOICE LISTEN" to "استمع للأوامر الصوتية",
            "●  LISTENING" to "● يستمع الآن",
            "JARVIS Android V1\nListening..." to "جارفيس أندرويد V1\nأستمع الآن...",
            "ENROLL OWNER VOICE" to "تسجيل صوت المالك",
            "●  OWNER VOICE READY" to "● تم تسجيل صوت المالك",
            "JARVIS Android V1\nOwner voice profile stored locally" to "جارفيس أندرويد V1\nتم حفظ بصمة صوت المالك على الجهاز",
            "JARVIS Android V1\nVoice enrollment failed" to "جارفيس أندرويد V1\nتعذر تسجيل الصوت",
            "ENABLE APP CONTROL" to "إعدادات إمكانية الوصول",
            "STOP JARVIS" to "إيقاف جارفيس",
            "●  CORE STANDBY" to "● النواة في وضع الاستعداد",
            "JARVIS Android V1\nStatus: Stopped" to "جارفيس أندرويد V1\nالحالة: متوقف",
            "GUARDIAN  •  FRIDAY FALLBACK  •  ACCESSIBILITY\nVoice profile and Close-chat memory are managed locally." to "الحارس • فرايداي الاحتياطية • إمكانية الوصول\nملف الصوت وذاكرة الكلوز محفوظان محلياً.",
            "INDEPENDENT ASSISTANT  •  FALLBACK READY" to "مساعدة مستقلة • جاهزة للعمل الاحتياطي",
            "●  STANDBY" to "● في وضع الاستعداد",
            "ACTIVATE FRIDAY" to "تفعيل فرايداي",
            "HAND CONTROL BACK TO JARVIS" to "إعادة التحكم إلى جارفيس",
            "STOP FRIDAY" to "إيقاف فرايداي",
            "MICROPHONE PERMISSIONS" to "أذونات الميكروفون",
            "BACK TO ASSISTANT SELECTOR" to "العودة لاختيار المساعد"
        )
        if (isArabic(context)) return translations[text] ?: text
        return translations.entries.firstOrNull { it.value == text }?.key ?: text
    }
}
