package com.example.data.quotes

data class MoodQuote(
    val quote: String,
    val source: String,
    val moodTag: String,
    val emoji: String,
    val prompt: String
)

object MoodQuoteRepository {

    // --- LOW MOOD (Deeply comforting, tender, uplifting, removing pressure) ---
    private val lowIntros = listOf(
        "Take off the heavy armor you've been carrying today.",
        "You don't have to be strong every single second of this life.",
        "It is completely okay if all you did today was breathe.",
        "Wrap yourself in quiet, unconditional kindness right now.",
        "When the world feels like too much, remember you can pause.",
        "Give your tired heart permission to rest without guilt.",
        "You do not have to earn your right to exist and be at peace.",
        "Be exceedingly gentle with yourself in this delicate moment.",
        "Softly unclench your hands and release the weight you carry.",
        "Even in the quietest valley, you are never truly alone."
    )

    private val lowHearts = listOf(
        "Your worth is not measured by what you accomplish, but by the love and life in your heart.",
        "Heavy days are not failures—they are sacred pauses where your spirit asks for tender care.",
        "Even the darkest winter trees quietly harbor the green buds of spring beneath their bark.",
        "You have survived every difficult day before this one, and you will emerge through this too.",
        "It is okay to feel fragile; the softest petals often belong to the most resilient flowers.",
        "Crying is not weakness; it is the soul washing the dust from its eyes so it can heal.",
        "You matter to this world in ways you cannot even begin to calculate or imagine.",
        "Resting your mind and body is the most courageous, productive thing you can do today.",
        "Be patient with your heart; healing is not linear, but love is always waiting for you.",
        "The sun never forgets to rise, even when dense clouds hide its gentle golden rays."
    )

    private val lowClosures = listOf(
        "Breathe softly. Better, lighter days are quietly making their way to you.",
        "Let yourself rest; tomorrow holds a softer sunrise for your spirit.",
        "You are deeply cherished, and this heavy cloud will gently pass.",
        "Hold yourself with grace tonight—you are doing so much better than you realize.",
        "One small, tender breath at a time. Peace is returning to you.",
        "Sleep softly, rest deeply, and remember you are worthy of every good thing.",
        "Close your eyes, feel your heart beat, and know you are safe right now.",
        "The universe is holding you gently in its quiet hands.",
        "May a soothing calm settle over your thoughts and bring you rest.",
        "Tomorrow is a fresh page waiting for your quiet renewal."
    )

    // --- STRESSED MOOD (Grounding, unburdening, anxiety-relieving, slow breathing) ---
    private val stressedIntros = listOf(
        "Drop your shoulders away from your ears and soften your jaw.",
        "Notice this exact present moment; right here and right now, you are safe.",
        "You do not have to solve the entire puzzle of tomorrow today.",
        "Pause the rushing world around you for just sixty gentle seconds.",
        "Place one hand gently on your chest and feel the rhythm of your breath.",
        "Step back from the spinning thoughts and look at the sky.",
        "Give yourself permission to slow down; nothing is worth losing your peace over.",
        "Breathe in calm tranquility, and breathe out the hurry of this day.",
        "Hurry is a habit of the mind, but peace is the true home of your soul.",
        "Let the urgent demands wait outside the quiet room of your mind."
    )

    private val stressedHearts = listOf(
        "Urgency is often just fear in disguise—true wisdom and clarity always move with quiet steps.",
        "You have walked through overwhelming storms before and come out grounded and whole.",
        "Release what you cannot control, and fiercely protect the quiet sanctuary inside your chest.",
        "Nothing in nature blooms all year round without resting; fallow ground is sacred ground.",
        "You only ever have to live this one single moment that is directly in front of you.",
        "The noise of the world is temporary, but the stillness within you is permanent and deep.",
        "You don't need to answer every question right now; let time do its quiet healing work.",
        "When everything feels tangled, simply doing the next tiny kind thing untangles the thread.",
        "Your peace of mind is your most precious possession—do not trade it for haste.",
        "Quiet confidence comes from knowing you don't need to control the outcome to be okay."
    )

    private val stressedClosures = listOf(
        "Take one slow, deep breath in... and let everything go.",
        "One step at a time is all that is ever asked of you.",
        "Allow peace to fill the spaces where worry used to reside.",
        "You are capable, calm, grounded, and resilient.",
        "Rest your mind. The answers will come when the water is still.",
        "Inhale stillness, exhale tension. You are entirely in control of this breath.",
        "Slow your pace. What is meant for you will never pass you by.",
        "Everything will be okay. Trust the quiet timing of your life.",
        "May this moment bring a soothing quiet to your busy thoughts.",
        "Relax your shoulders and smile; you are doing wonderfully."
    )

    // --- OKAY MOOD (Gentle balance, finding magic in ordinary moments, steady peace) ---
    private val okayIntros = listOf(
        "There is a quiet, sacred beauty in an ordinary, calm day.",
        "You don't need fireworks to have a deeply meaningful life.",
        "Steady, quiet days are the anchors that keep our lives rooted.",
        "Embrace the gentle rhythm and soft cadence of this peaceful hour.",
        "An okay day is a gift of quiet balance and unstirred waters.",
        "Notice the simple things: a warm drink, a soft breeze, a clear breath.",
        "No pressure, no drama—just you, living peacefully at your own pace.",
        "Let the quiet stability of today be your soft companion.",
        "Ordinary moments are where the most lasting memories quietly grow.",
        "Walk softly today, enjoying the freedom of having nowhere to rush."
    )

    private val okayHearts = listOf(
        "Contentment is not having everything, but cherishing the soft stillness you have right now.",
        "When nothing is demanding of your soul, everything becomes a subtle and gentle gift.",
        "Walking at a steady, unhurried pace is how true long-term resilience is built.",
        "A peaceful heart makes every simple room feel like a sanctuary of comfort.",
        "You do not need to chase excitement; true joy often arrives in slippers of silence.",
        "Balance is the quiet superpower that keeps you strong through life's shifting seasons.",
        "A calm mind sees the delicate beauty that rushing eyes always overlook.",
        "To live simply and quietly without anxiety is one of life's highest achievements.",
        "Cherish this gentle plateau—it gives you strength for whatever hills lie ahead.",
        "Every quiet breath is a small thank you to the mystery of being alive."
    )

    private val okayClosures = listOf(
        "Savor the quiet grace of just being yourself today.",
        "May this peaceful balance nourish your spirit from the inside out.",
        "Rest in the simplicity and unhurried charm of this moment.",
        "Stay steady, stay gentle, and let the day unfold with ease.",
        "You are centered, present, and at home within yourself.",
        "Keep this soft smile close to your heart as you go.",
        "Ordinary days are the silent bricks of a peaceful life.",
        "Enjoy the calm; you have earned this quiet tranquility.",
        "Let your heart rest in gratitude for a quiet day.",
        "Walk gently, breathe freely, and be content."
    )

    // --- GOOD MOOD (Content, warm, flowing, mindful gratitude) ---
    private val goodIntros = listOf(
        "Notice the golden warmth that lives in your spirit today.",
        "Carry this light step forward with quiet gratitude and joy.",
        "A good day is a garden you planted with patience and small steps.",
        "Smile at how far your gentle persistence has brought your life.",
        "Let the pleasant momentum of today carry you like a smooth river.",
        "Open your eyes to the small miracles scattered across your path today.",
        "There is a lovely melody playing in your heart today—listen to it.",
        "Goodness has a way of multiplying whenever you take a moment to notice it.",
        "Your heart feels light and open; welcome this sweet feeling.",
        "Bask in the quiet glow of things going well and smoothly."
    )

    private val goodHearts = listOf(
        "When your heart is light, the world reflects that radiance right back to you.",
        "Small moments of kindness create ripples that travel farther than you will ever know.",
        "Your positive energy is a gentle healing presence for everyone who crosses your path.",
        "Gratitude turns what we have into more than enough, and a smile makes the day golden.",
        "When you nurture peace inside yourself, you become a sanctuary for others.",
        "Cherish these sweet, bright hours; they build the joyful foundation of your memories.",
        "Kindness to yourself always overflows as genuine warmth to the people you love.",
        "Life is full of quiet wonders when we have the eyes to see and the heart to appreciate.",
        "Keep moving forward with this steady grace—you are creating a beautiful path.",
        "Joy is not complicated; it is simply being fully awake to the beauty of now."
    )

    private val goodClosures = listOf(
        "Let this peaceful contentment linger like sunlight on calm water.",
        "Keep shining gently, authentically, and warmly today.",
        "Celebrate the simple goodness of being alive and well.",
        "May this sunny feeling stay in your chest all throughout the evening.",
        "Share a gentle smile with someone today; your warmth is contagious.",
        "Your spirit is blooming beautifully—enjoy every minute of it.",
        "Walk with joy in your step and gratitude in your breath.",
        "Keep anchoring this happiness into your memory bank.",
        "May your good day gently ripple into an even sweeter tomorrow.",
        "Carry this peace like a lantern wherever you go."
    )

    // --- GREAT MOOD (Radiant, celebratory, inspired, elevated joy) ---
    private val greatIntros = listOf(
        "You are glowing with vibrant, beautiful energy today!",
        "Celebrate this wonderful high tide of joy with an open, thankful heart.",
        "The world is noticeably brighter simply because you are feeling so radiant.",
        "Ride this magnificent wave of inspiration and let your heart dance.",
        "There is a sparkling vitality in your spirit today—let it illuminate your path.",
        "Your soul is singing with clarity, power, and joyful gratitude today.",
        "What a magnificent gift to feel this level of brightness and life!",
        "Let your laughter echo and your heart expand into every corner of today.",
        "You are a beacon of hope and radiant light in this world.",
        "Today is a celebration of life, growth, and how far you've come."
    )

    private val greatHearts = listOf(
        "Your heart is capable of boundless joy, courageous dreams, and deep fulfillment.",
        "Every joyful moment you experience today becomes a lasting anchor for your entire life.",
        "When you step boldly into your joy, you unconsciously give others permission to do the same.",
        "Gratitude turns moments of happiness into sacred celebrations of the human spirit.",
        "Your radiant energy can spark hope in someone who was looking for a sign of goodness.",
        "Joy is your natural birthright; embrace it without holding back a single ounce.",
        "Cherish this brilliant vibrancy and let it remind you of what is possible in this life.",
        "Great days are the stars by which we navigate when the skies turn dim.",
        "You have grown so much, and today is proof that your heart knows how to flourish.",
        "Life is pure magic when lived with passion, presence, and loving joy."
    )

    private val greatClosures = listOf(
        "May this brilliant joy illuminate every path you walk today!",
        "Keep soaring with this beautiful, uplifting, unstoppable spirit.",
        "You are truly radiant—let your happiness inspire the world.",
        "Anchor this feeling deeply into your heart; it is yours forever.",
        "Celebrate yourself, celebrate this day, and shine bright!",
        "Let your joy overflow into every interaction you have today.",
        "May this wonderful feeling keep lighting your way for days to come.",
        "You are a masterpiece in motion—enjoy every radiant second.",
        "Smile big, laugh often, and revel in this glorious energy.",
        "The universe is smiling with you today!"
    )

    // --- GENERAL / UNSELECTED MOOD (Mindful, calm, grounding) ---
    private val generalIntros = listOf(
        "Peace begins with a single mindful, unhurried breath.",
        "Small steps taken consistently shape the grand landscape of your future.",
        "Be gentle with your expectations of yourself today.",
        "One thoughtful, present action at a time is enough.",
        "Celebrate what you finish, and release what you cannot control.",
        "You do not need to hurry; what is meant for you will not pass.",
        "Rest is not a reward for work—it is just as vital as progress.",
        "Today is a quiet, fresh opportunity to nurture your inner self.",
        "Listen to the quiet voice within that whispers: take it easy.",
        "The most powerful choice you can make today is to be kind to yourself."
    )

    private val generalHearts = listOf(
        "Quiet persistence always overcomes loud urgency in the long run.",
        "Your life is a precious journey, not a frantic race against the clock.",
        "When you forgive yourself for not knowing earlier, you create room for wisdom.",
        "Every sunrise brings an unspoken promise that you can begin anew.",
        "The secret to a peaceful life is doing small things with great love.",
        "Cultivate a quiet sanctuary within your thoughts where you always feel at home.",
        "Nature never rushes, yet everything is accomplished in its own perfect time.",
        "Give your spirit room to breathe and expand without demanding perfection.",
        "A gentle routine is a shield against the chaos of the noisy world.",
        "True strength is soft, quiet, patient, and full of gentle grace."
    )

    private val generalClosures = listOf(
        "May your day be peaceful, calm, and grounded in kindness.",
        "Take a slow breath and walk with confidence in your gentle path.",
        "You are worthy of peace, comfort, and quiet happiness today.",
        "Rest well in the knowledge that you are doing your very best.",
        "Let tranquility be your compass throughout this day.",
        "Remember that taking care of yourself is a gift to everyone you love.",
        "Move at the pace of your own peace, not the world's hurry.",
        "May gentle clarity guide your heart through every moment today.",
        "Honor your pace, trust your journey, and breathe easy.",
        "Step forward softly and enjoy the gift of today."
    )

    /**
     * Deterministically produces over 1,000 unique quotes per mood category.
     * Uses dayOfYear, date hash, and mood index to provide a fresh, touching,
     * emotional line every day, perfectly tuned to the user's logged mood.
     */
    fun getDailyMoodQuote(mood: String?, dayOfYear: Int, dateIso: String = ""): MoodQuote {
        val safeDay = if (dayOfYear <= 0) 1 else dayOfYear
        val dateHash = if (dateIso.isNotBlank()) dateIso.hashCode() else safeDay * 37
        val seed = Math.abs(safeDay * 73 + dateHash)

        // Combinations: 10 intros * 10 hearts * 10 closures = 1,000 distinct quotes!
        val introIdx = (seed / 100) % 10
        val heartIdx = (seed / 10) % 10
        val closureIdx = seed % 10

        return when (mood?.trim()?.lowercase()) {
            "low" -> {
                val quoteText = "${lowIntros[introIdx]} ${lowHearts[heartIdx]} ${lowClosures[closureIdx]}"
                MoodQuote(
                    quote = quoteText,
                    source = "Gentle Comfort",
                    moodTag = "Low",
                    emoji = "💙",
                    prompt = "Take a slow breath. Your feelings are safe here."
                )
            }
            "stressed" -> {
                val quoteText = "${stressedIntros[introIdx]} ${stressedHearts[heartIdx]} ${stressedClosures[closureIdx]}"
                MoodQuote(
                    quote = quoteText,
                    source = "Quiet Sanctuary",
                    moodTag = "Stressed",
                    emoji = "🕊️",
                    prompt = "Release your shoulders. You only need to take this one breath."
                )
            }
            "okay" -> {
                val quoteText = "${okayIntros[introIdx]} ${okayHearts[heartIdx]} ${okayClosures[closureIdx]}"
                MoodQuote(
                    quote = quoteText,
                    source = "Steady Balance",
                    moodTag = "Okay",
                    emoji = "🌿",
                    prompt = "An ordinary day holds its own quiet magic."
                )
            }
            "good" -> {
                val quoteText = "${goodIntros[introIdx]} ${goodHearts[heartIdx]} ${goodClosures[closureIdx]}"
                MoodQuote(
                    quote = quoteText,
                    source = "Warm Gratitude",
                    moodTag = "Good",
                    emoji = "🌸",
                    prompt = "Notice the pleasant warmth in your heart today."
                )
            }
            "great" -> {
                val quoteText = "${greatIntros[introIdx]} ${greatHearts[heartIdx]} ${greatClosures[closureIdx]}"
                MoodQuote(
                    quote = quoteText,
                    source = "Radiant Joy",
                    moodTag = "Great",
                    emoji = "☀️",
                    prompt = "Celebrate this bright light and share your smile."
                )
            }
            else -> {
                val quoteText = "${generalIntros[introIdx]} ${generalHearts[heartIdx]} ${generalClosures[closureIdx]}"
                MoodQuote(
                    quote = quoteText,
                    source = "Daily Wisdom",
                    moodTag = "Mindful",
                    emoji = "🌱",
                    prompt = "A peaceful day unfolds one gentle intention at a time."
                )
            }
        }
    }
}
