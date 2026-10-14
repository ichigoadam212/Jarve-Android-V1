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

    /** Translate only known assistant phrases; arbitrary user/app text is left untouched. */
    fun localizeSpeech(context: Context, text: String): String {
        if (isArabic(context)) return text
        val exact = mapOf(
            "نعم" to "Yes.",
            "سجل بصمة صوت المالك أولاً من إعدادات جارفيس" to "Please enroll the owner's voice from JARVIS settings first.",
            "لم يتم التحقق من صوت المالك" to "The owner's voice could not be verified.",
            "جارفيس متصل وخدمة التحكم في التطبيقات مفعلة" to "JARVIS is connected and app control is enabled.",
            "جارفيس متصل لكن خدمة التحكم في التطبيقات غير مفعلة" to "JARVIS is connected, but app control is not enabled.",
            "جارفيس غير متصل" to "JARVIS is not connected.",
            "تمت إضافة الشات للكلوز" to "The chat was added to Close Chat.",
            "افتح الشات أولاً ثم أعطني الأمر" to "Open the chat first, then give me the command.",
            "تمت إزالة الشات من الكلوز" to "The chat was removed from Close Chat.",
            "افتح الشات أولاً" to "Open the chat first.",
            "تم تحليل سجل الشات وحفظه محلياً" to "The chat history was analyzed and saved locally.",
            "لم أستطع قراءة سجل الشات" to "I could not read the chat history.",
            "تم تشغيل الرد التلقائي للكلوز" to "Close Chat auto-replies are on.",
            "تم إيقاف الرد التلقائي للكلوز" to "Close Chat auto-replies are off.",
            "الشات ده مش مضاف للكلوز" to "This chat is not in Close Chat.",
            "تم الإرسال" to "Sent.",
            "قل نص الرد بعد كلمة رد" to "Say the reply text after the word reply.",
            "خدمة التحكم في التطبيقات غير مفعلة" to "App control is not enabled.",
            "فعّل خدمة التحكم في التطبيقات أولاً" to "Enable app control first.",
            "لم أستطع الإرسال من التطبيق الحالي" to "I could not send from the current app.",
            "تم الإيقاف" to "Stopped.",
            "تم إيقاف الوضع السري" to "Secret mode is off.",
            "لا يوجد نص مقروء من التطبيق الحالي" to "There is no readable text in the current app.",
            "تم تسجيل صوت المالك" to "Owner voice profile saved.",
            "لم أستطع فتح واتساب" to "I could not open WhatsApp.",
            "لم أستطع فتح انستجرام" to "I could not open Instagram.",
            "لم أستطع فتح فيسبوك" to "I could not open Facebook.",
            "فعّل خدمة التحكم في التطبيقات أولاً" to "Enable app control first."
        )
        exact[text]?.let { return it }
        return when {
            text.startsWith("تم فتح ") -> "Opened " + text.removePrefix("تم فتح ")
            text.startsWith("لم أستطع فتح ") -> "I could not open " + text.removePrefix("لم أستطع فتح ")
            else -> text
        }
    }

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
            "BACK TO ASSISTANT SELECTOR" to "العودة لاختيار المساعد",
            "Speech recognition unavailable. Check Google speech services and microphone permission." to "التعرّف على الكلام غير متاح. تأكد من خدمات الكلام وإذن الميكروفون.",
            "GUARDIAN  •  FRIDAY FALLBACK  •  ACCESSIBILITY\\nVoice profile and Close-chat memory are managed locally.\\nEnglish dictionary: say 'meaning <word>' or 'معنى <word>'." to "الحارس • فرايداي الاحتياطية • إمكانية الوصول\\nملف الصوت وذاكرة الكلوز محفوظان محلياً.\\nالقاموس: قول معنى ثم الكلمة بالإنجليزي.",
            "GUARDIAN  •  FRIDAY FALLBACK  •  ACCESSIBILITY\nVoice profile and Close-chat memory are managed locally.\nEnglish dictionary: say 'meaning <word>' or 'معنى <word>'." to "الحارس • فرايداي الاحتياطية • إمكانية الوصول\nملف الصوت وذاكرة الكلوز محفوظان محلياً.\nالقاموس: قول معنى ثم الكلمة بالإنجليزي."
        )
        if (isArabic(context)) return translations[text] ?: text
        return translations.entries.firstOrNull { it.value == text }?.key ?: text
    }
}
