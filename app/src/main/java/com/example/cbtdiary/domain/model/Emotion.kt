package com.example.cbtdiary.domain.model

enum class EmotionCategory(val displayName: String, val color: Long) {
    ANGER("Гнев", 0xFFFF4444),
    FEAR("Страх", 0xFFFF9800),
    SADNESS("Грусть", 0xFF87CEEB),
    JOY("Радость", 0xFF4CAF50),
    LOVE("Любовь", 0xFFFF69B4)
}

data class Emotion(
    val name: String,
    val category: EmotionCategory
)

object Emotions {
    val allEmotions = listOf(
        // ГНЕВ (ANGER)
        Emotion("БЕШЕНСТВО", EmotionCategory.ANGER),
        Emotion("ЯРОСТЬ", EmotionCategory.ANGER),
        Emotion("НЕНАВИСТЬ", EmotionCategory.ANGER),
        Emotion("ИСТЕРИЯ", EmotionCategory.ANGER),
        Emotion("ЗЛОСТЬ", EmotionCategory.ANGER),
        Emotion("РАЗДРАЖЕНИЕ", EmotionCategory.ANGER),
        Emotion("ПРЕЗРЕНИЕ", EmotionCategory.ANGER),
        Emotion("НЕГОДОВАНИЕ", EmotionCategory.ANGER),
        Emotion("ОБИДА", EmotionCategory.ANGER),
        Emotion("РЕВНОСТЬ", EmotionCategory.ANGER),
        Emotion("УЯЗВЛЕННОСТЬ", EmotionCategory.ANGER),
        Emotion("ДОСАДА", EmotionCategory.ANGER),
        Emotion("ЗАВИСТЬ", EmotionCategory.ANGER),
        Emotion("НЕПРИЯЗНЬ", EmotionCategory.ANGER),
        Emotion("ВОЗМУЩЕНИЕ", EmotionCategory.ANGER),
        Emotion("ОТВРАЩЕНИЕ", EmotionCategory.ANGER),
        Emotion("НАДМЕННОСТЬ", EmotionCategory.ANGER),
        
        // СТРАХ (FEAR)
        Emotion("УЖАС", EmotionCategory.FEAR),
        Emotion("ОТЧАЯНИЕ", EmotionCategory.FEAR),
        Emotion("ИСПУГ", EmotionCategory.FEAR),
        Emotion("ОЦЕПЕНЕНИЕ", EmotionCategory.FEAR),
        Emotion("ПОДОЗРЕНИЕ", EmotionCategory.FEAR),
        Emotion("ТРЕВОГА", EmotionCategory.FEAR),
        Emotion("ОШАРАШЕННОСТЬ", EmotionCategory.FEAR),
        Emotion("БЕСПОКОЙСТВО", EmotionCategory.FEAR),
        Emotion("БОЯЗНЬ", EmotionCategory.FEAR),
        Emotion("УНИЖЕНИЕ", EmotionCategory.FEAR),
        Emotion("ЗАМЕШАТЕЛЬСТВО", EmotionCategory.FEAR),
        Emotion("РАСТЕРЯННОСТЬ", EmotionCategory.FEAR),
        Emotion("ВИНА/СТЫД", EmotionCategory.FEAR),
        Emotion("СОМНЕНИЕ", EmotionCategory.FEAR),
        Emotion("ЗАСТЕНЧИВОСТЬ", EmotionCategory.FEAR),
        Emotion("ОПАСЕНИЕ", EmotionCategory.FEAR),
        Emotion("СМУЩЕНИЕ", EmotionCategory.FEAR),
        Emotion("СЛОМЛЕННОСТЬ", EmotionCategory.FEAR),
        Emotion("ПОДВОХ", EmotionCategory.FEAR),
        Emotion("ОШЕЛОМЛЕННОСТЬ", EmotionCategory.FEAR),
        
        // ГРУСТЬ (SADNESS)
        Emotion("ГОРЕЧЬ", EmotionCategory.SADNESS),
        Emotion("ТОСКА", EmotionCategory.SADNESS),
        Emotion("СКОРБЬ", EmotionCategory.SADNESS),
        Emotion("ЛЕНЬ", EmotionCategory.SADNESS),
        Emotion("ЖАЛОСТЬ", EmotionCategory.SADNESS),
        Emotion("ОТРЕШЕННОСТЬ", EmotionCategory.SADNESS),
        Emotion("БЕСПОМОЩНОСТЬ", EmotionCategory.SADNESS),
        Emotion("ДУШЕВНАЯ БОЛЬ", EmotionCategory.SADNESS),
        Emotion("БЕЗНАДЕЖНОСТЬ", EmotionCategory.SADNESS),
        Emotion("ОТЧУЖДЕННОСТЬ", EmotionCategory.SADNESS),
        Emotion("РАЗОЧАРОВАНИЕ", EmotionCategory.SADNESS),
        Emotion("ПОТРЯСЕНИЕ", EmotionCategory.SADNESS),
        Emotion("СОЖАЛЕНИЕ", EmotionCategory.SADNESS),
        Emotion("СКУКА", EmotionCategory.SADNESS),
        Emotion("БЕЗЫСХОДНОСТЬ", EmotionCategory.SADNESS),
        Emotion("ПЕЧАЛЬ", EmotionCategory.SADNESS),
        Emotion("ЗАГНАННОСТЬ", EmotionCategory.SADNESS),
        
        // РАДОСТЬ (JOY)
        Emotion("СЧАСТЬЕ", EmotionCategory.JOY),
        Emotion("ВОСТОРГ", EmotionCategory.JOY),
        Emotion("ЛИКОВАНИЕ", EmotionCategory.JOY),
        Emotion("ПРИПОДНЯТОСТЬ", EmotionCategory.JOY),
        Emotion("ОЖИВЛЕНИЕ", EmotionCategory.JOY),
        Emotion("УМИРОТВОРЕНИЕ", EmotionCategory.JOY),
        Emotion("УВЛЕЧЕНИЕ", EmotionCategory.JOY),
        Emotion("ИНТЕРЕС", EmotionCategory.JOY),
        Emotion("ЗАБОТА", EmotionCategory.JOY),
        Emotion("ОЖИДАНИЕ", EmotionCategory.JOY),
        Emotion("ВОЗБУЖДЕНИЕ", EmotionCategory.JOY),
        Emotion("ПРЕДВКУШЕНИЕ", EmotionCategory.JOY),
        Emotion("НАДЕЖДА", EmotionCategory.JOY),
        Emotion("ЛЮБОПЫТСТВО", EmotionCategory.JOY),
        Emotion("ОСВОБОЖДЕНИЕ", EmotionCategory.JOY),
        Emotion("ПРИНЯТИЕ", EmotionCategory.JOY),
        Emotion("НЕТЕРПЕНИЕ", EmotionCategory.JOY),
        Emotion("ВЕРА", EmotionCategory.JOY),
        Emotion("ИЗУМЛЕНИЕ", EmotionCategory.JOY),
        
        // ЛЮБОВЬ (LOVE)
        Emotion("НЕЖНОСТЬ", EmotionCategory.LOVE),
        Emotion("ТЕПЛОТА", EmotionCategory.LOVE),
        Emotion("СОЧУВСТВИЕ", EmotionCategory.LOVE),
        Emotion("БЛАЖЕНСТВО", EmotionCategory.LOVE),
        Emotion("ДОВЕРИЕ", EmotionCategory.LOVE),
        Emotion("БЕЗОПАСНОСТЬ", EmotionCategory.LOVE),
        Emotion("БЛАГОДАРНОСТЬ", EmotionCategory.LOVE),
        Emotion("СПОКОЙСТВИЕ", EmotionCategory.LOVE),
        Emotion("СИМПАТИЯ", EmotionCategory.LOVE),
        Emotion("ИДЕНТИЧНОСТЬ", EmotionCategory.LOVE),
        Emotion("ГОРДОСТЬ", EmotionCategory.LOVE),
        Emotion("ВОСХИЩЕНИЕ", EmotionCategory.LOVE),
        Emotion("УВАЖЕНИЕ", EmotionCategory.LOVE),
        Emotion("САМОЦЕННОСТЬ", EmotionCategory.LOVE),
        Emotion("ВЛЮБЛЕННОСТЬ", EmotionCategory.LOVE),
        Emotion("ЛЮБОВЬ К СЕБЕ", EmotionCategory.LOVE),
        Emotion("ОЧАРОВАННОСТЬ", EmotionCategory.LOVE),
        Emotion("СМИРЕНИЕ", EmotionCategory.LOVE),
        Emotion("ИСКРЕННОСТЬ", EmotionCategory.LOVE),
        Emotion("ДРУЖЕЛЮБИЕ", EmotionCategory.LOVE),
        Emotion("ДОБРОТА", EmotionCategory.LOVE),
        Emotion("ВЗАИМОВЫРУЧКА", EmotionCategory.LOVE)
    )
    
    fun getEmotionsByCategory(category: EmotionCategory): List<Emotion> {
        return allEmotions.filter { it.category == category }
    }
}
