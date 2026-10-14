package com.ichigoadam.jarvis.memory

import java.util.Locale

/** Compact offline English learner's dictionary with Arabic meanings and plain-English definitions. */
object EnglishDictionary {
    private data class Entry(val arabic: String, val definition: String)
    private val words = mapOf(
        "ability" to Entry("قدرة", "the power or skill to do something"),
        "achieve" to Entry("يحقق / ينجز", "to succeed in doing something"),
        "accurate" to Entry("دقيق / صحيح", "correct and exact"),
        "action" to Entry("فعل / إجراء", "something that someone does"),
        "actually" to Entry("في الحقيقة / فعلياً", "in fact; really"),
        "advice" to Entry("نصيحة", "an opinion about what someone should do"),
        "afraid" to Entry("خائف", "feeling fear"),
        "although" to Entry("بالرغم من أن", "despite the fact that"),
        "answer" to Entry("إجابة / يجيب", "a response to a question"),
        "appear" to Entry("يظهر", "to become visible or seem to be"),
        "arrive" to Entry("يصل", "to reach a place"),
        "beautiful" to Entry("جميل", "pleasing to look at"),
        "because" to Entry("لأن", "for the reason that"),
        "become" to Entry("يصبح", "to begin to be"),
        "believe" to Entry("يعتقد / يؤمن", "to think that something is true"),
        "brave" to Entry("شجاع", "ready to face danger or difficulty"),
        "build" to Entry("يبني", "to make or construct something"),
        "calm" to Entry("هادئ", "peaceful and not worried"),
        "careful" to Entry("حذر", "taking care to avoid mistakes or danger"),
        "change" to Entry("يغير / تغيير", "to make or become different"),
        "choose" to Entry("يختار", "to decide which thing you want"),
        "clear" to Entry("واضح", "easy to understand or see"),
        "clever" to Entry("ذكي / فطن", "quick to learn and understand"),
        "complete" to Entry("يكمل / كامل", "having all parts; to finish"),
        "confident" to Entry("واثق", "sure about your ability or a situation"),
        "create" to Entry("ينشئ / يبتكر", "to make something new"),
        "curious" to Entry("فضولي", "wanting to know or learn something"),
        "danger" to Entry("خطر", "the possibility of harm"),
        "decide" to Entry("يقرر", "to choose after thinking"),
        "dictionary" to Entry("قاموس", "a resource that explains words"),
        "different" to Entry("مختلف", "not the same"),
        "difficult" to Entry("صعب", "not easy to do or understand"),
        "discover" to Entry("يكتشف", "to find something for the first time"),
        "dream" to Entry("حلم / يحلم", "a hope or a series of thoughts during sleep"),
        "easy" to Entry("سهل", "not difficult"),
        "effort" to Entry("مجهود", "energy used to do something"),
        "enough" to Entry("كافٍ", "as much or as many as needed"),
        "environment" to Entry("بيئة", "the surroundings in which something exists"),
        "example" to Entry("مثال", "something that shows what another thing is like"),
        "explain" to Entry("يشرح", "to make something clear"),
        "failure" to Entry("فشل", "lack of success"),
        "famous" to Entry("مشهور", "known by many people"),
        "freedom" to Entry("حرية", "the ability to act without unfair control"),
        "friend" to Entry("صديق", "a person you like and trust"),
        "future" to Entry("المستقبل", "the time that is yet to come"),
        "goal" to Entry("هدف", "something you want to achieve"),
        "guess" to Entry("يخمن / تخمين", "to give an answer without being certain"),
        "happen" to Entry("يحدث", "to take place"),
        "honest" to Entry("صادق", "truthful and not misleading"),
        "hope" to Entry("أمل / يأمل", "to want something good to happen"),
        "important" to Entry("مهم", "having great value or significance"),
        "improve" to Entry("يحسن / يطور", "to make something better"),
        "knowledge" to Entry("معرفة", "information and understanding gained by learning"),
        "learn" to Entry("يتعلم", "to gain knowledge or skill"),
        "meaning" to Entry("معنى", "what a word, sign, or action expresses"),
        "memory" to Entry("ذاكرة", "the ability to store and recall information"),
        "mistake" to Entry("خطأ", "something that is incorrect"),
        "necessary" to Entry("ضروري", "needed for a purpose"),
        "notice" to Entry("يلاحظ / ملاحظة", "to become aware of something"),
        "opportunity" to Entry("فرصة", "a situation that makes something possible"),
        "patient" to Entry("صبور", "able to wait without getting upset"),
        "peace" to Entry("سلام", "a state without fighting or disturbance"),
        "possible" to Entry("ممكن", "able to happen or be done"),
        "powerful" to Entry("قوي", "having great power or influence"),
        "prepare" to Entry("يستعد / يجهز", "to get ready for something"),
        "problem" to Entry("مشكلة", "a situation that needs a solution"),
        "protect" to Entry("يحمي", "to keep someone or something safe"),
        "quick" to Entry("سريع", "moving or happening in a short time"),
        "reason" to Entry("سبب / منطق", "an explanation for something"),
        "remember" to Entry("يتذكر", "to bring something back to mind"),
        "respect" to Entry("احترام / يحترم", "to treat someone with consideration"),
        "safe" to Entry("آمن", "protected from danger or harm"),
        "solution" to Entry("حل", "an answer to a problem"),
        "strong" to Entry("قوي", "having power or strength"),
        "success" to Entry("نجاح", "the achievement of a goal"),
        "support" to Entry("يدعم / دعم", "to help or encourage someone"),
        "trust" to Entry("ثقة / يثق", "to believe someone is reliable"),
        "understand" to Entry("يفهم", "to know the meaning of something"),
        "useful" to Entry("مفيد", "helpful for a purpose"),
        "voice" to Entry("صوت", "the sound made when someone speaks"),
        "wonder" to Entry("يتساءل / يتعجب", "to think about something with curiosity"),
        "word" to Entry("كلمة", "a unit of language with meaning"),
        "work" to Entry("عمل / يعمل", "activity involving effort to achieve something")
    )

    fun lookup(input: String): String? {
        val word = input.trim().lowercase(Locale.ROOT)
            .trim { !it.isLetterOrDigit() && it != '\'' && it != '-' }
        if (word.isBlank() || word.any { it.isWhitespace() }) return null
        val entry = words[word] ?: return null
        return "$word: ${entry.arabic}. In English: ${entry.definition}."
    }

    fun supportedWords(): Int = words.size
}
