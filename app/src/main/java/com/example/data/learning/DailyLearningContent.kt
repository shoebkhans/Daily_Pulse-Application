package com.example.data.learning

data class DailyLearningCard(
    val id: String,
    val title: String,
    val category: String,
    val iconName: String,
    val summary: String,
    val deepDive: String,
    val practicalTip: String,
    val quote: String = ""
)

object DailyLearningContent {
    val cards: List<DailyLearningCard> = listOf(
        DailyLearningCard(
            id = "learn_01",
            title = "The 20-Second Rule for Habits",
            category = "Habit Science",
            iconName = "timer",
            summary = "Lowering the activation energy of a good habit makes it significantly easier to start consistently.",
            deepDive = "Researcher Shawn Achor discovered that reducing the time it takes to start an action by just 20 seconds drastically increases the likelihood you'll do it. Conversely, adding 20 seconds of barrier to an unwanted habit reduces friction.",
            practicalTip = "Leave your book open on the nightstand, or fill your water bottle before sleeping tonight.",
            quote = "“You do not rise to the level of your goals. You fall to the level of your systems.” — James Clear"
        ),
        DailyLearningCard(
            id = "learn_02",
            title = "Physiological Sigh for Instant Calm",
            category = "Mindfulness & Body",
            iconName = "air",
            summary = "Two quick inhales through the nose followed by one long exhale through the mouth resets nervous arousal rapidly.",
            deepDive = "Neuroscience studies show that the double inhale re-inflates collapsed alveoli in the lungs and increases oxygen exchange, triggering the vagus nerve to slow heart rate in under 10 seconds.",
            practicalTip = "Try 2 to 3 physiological sighs whenever you feel overwhelmed or rushed today.",
            quote = "“Conscious breathing is my anchor in the rush of life.” — Thich Nhat Hanh"
        ),
        DailyLearningCard(
            id = "learn_03",
            title = "Morning Sunlight & Circadian Clock",
            category = "Sleep & Energy",
            iconName = "wb_sunny",
            summary = "Viewing natural sunlight within 30-60 minutes of waking triggers cortisol release for daytime alertness and sets melatonin timing for sleep.",
            deepDive = "Intrinsically photosensitive retinal ganglion cells (ipRGCs) detect photon density in morning sky light, signaling the suprachiasmatic nucleus to synchronize your body clock.",
            practicalTip = "Step outside for 5 to 10 minutes without sunglasses shortly after waking.",
            quote = "“Morning light is the conductor of your body's biological symphony.” — Dr. Satchin Panda"
        ),
        DailyLearningCard(
            id = "learn_04",
            title = "The Zeigarnik Effect & Brain Clutter",
            category = "Focus & Mental Flow",
            iconName = "psychology",
            summary = "Unfinished tasks occupy working memory until you acknowledge them or capture them on paper.",
            deepDive = "Psychologist Bluma Zeigarnik observed that human memory holds intrusive tension for incomplete tasks. Writing down clear next actions releases this mental loops immediately.",
            practicalTip = "Empty swirling thoughts into today's task list so your brain doesn't have to keep juggling them.",
            quote = "“Your mind is for having ideas, not holding them.” — David Allen"
        ),
        DailyLearningCard(
            id = "learn_05",
            title = "Micro-Breaks for Sustained Focus",
            category = "Productivity & Health",
            iconName = "coffee",
            summary = "A 60-second gaze at distant objects relaxes the ciliary muscles in your eyes and prevents cognitive fatigue.",
            deepDive = "The 20-20-20 rule recommends looking at something 20 feet away for 20 seconds every 20 minutes to restore optical focus and soothe tension in neck and cranial nerves.",
            practicalTip = "Glance out the window at trees, clouds, or the horizon between deep work blocks.",
            quote = "“Almost everything will work again if you unplug it for a few minutes, including you.” — Anne Lamott"
        ),
        DailyLearningCard(
            id = "learn_06",
            title = "Hydration and Cognitive Speed",
            category = "Health & Nutrition",
            iconName = "water_drop",
            summary = "Mild dehydration of just 1-2% slows attention span, working memory, and mood regulation.",
            deepDive = "Brain tissue is approximately 73% water. Even minor fluid deficit drops cerebral blood volume and triggers mild headaches or foggy thinking.",
            practicalTip = "Keep a warm mug of herbal tea or room-temperature water within arm's reach.",
            quote = "“Water is the driving force of all nature and thought.” — Leonardo da Vinci"
        ),
        DailyLearningCard(
            id = "learn_07",
            title = "Habit Stacking",
            category = "Habit Science",
            iconName = "layers",
            summary = "Anchor a new desirable behavior immediately onto an already established automatic habit.",
            deepDive = "Formulated by Dr. BJ Fogg and popularized by James Clear, pairing 'After I [current habit], I will [new habit]' uses existing neural pathways to effortlessly build momentum.",
            practicalTip = "'After I brew my morning tea, I will write down 3 gentle priorities for the day.'",
            quote = "“Small deeds done are better than great deeds planned.” — Peter Marshall"
        ),
        DailyLearningCard(
            id = "learn_08",
            title = "The Power of Gentle Gratitude",
            category = "Emotional Well-being",
            iconName = "favorite",
            summary = "Noticing one specific pleasant detail re-tunes the brain's default negativity bias over time.",
            deepDive = "Neuroplasticity shows that intentionally acknowledging small comforts—a warm blanket, morning stillness, or fresh air—releases dopamine and serotonin, soothing amygdala reactivity.",
            practicalTip = "Identify one tiny sensory pleasure you experienced in the last hour.",
            quote = "“Joy is what happens to us when we allow ourselves to recognize how good things really are.” — Marianne Williamson"
        ),
        DailyLearningCard(
            id = "learn_09",
            title = "Non-Sleep Deep Rest (NSDR)",
            category = "Rest & Recovery",
            iconName = "spa",
            summary = "A 10-20 minute guided stillness session restores mental clarity without the grogginess of deep naps.",
            deepDive = "Similar to Yoga Nidra, NSDR slows brainwaves from beta to alpha and theta states, accelerating motor learning and dopamine replenishment.",
            practicalTip = "Sit back comfortably for 10 minutes, soften your gaze, and listen to ambient quiet.",
            quote = "“Rest is not idleness, and to lie sometimes on the grass is by no means a waste of time.” — John Lubbock"
        ),
        DailyLearningCard(
            id = "learn_10",
            title = "Parkinson's Law & Simple Timeboxing",
            category = "Time & Peace",
            iconName = "schedule",
            summary = "Work expands to fill the time allotted for its completion. Shorter windows encourage gentle focus.",
            deepDive = "Allocating open-ended afternoons to simple chores often causes procrastination and low-grade anxiety. Assigning a modest 25-minute focus window brings swift closure.",
            practicalTip = "Pick one lingering task today and give yourself an intentional 20-minute finish window.",
            quote = "“Simplicity boils down to two steps: Identify the essential. Eliminate the rest.” — Leo Babauta"
        ),
        DailyLearningCard(
            id = "learn_11",
            title = "Walking in Nature (Shinrin-yoku)",
            category = "Mind & Nature",
            iconName = "park",
            summary = "Walking amid greenery lowers cortisol, blood pressure, and rumination.",
            deepDive = "Plants release antimicrobial organic compounds called phytoncides. Inhaling these naturally stimulates natural killer cell activity and calms the sympathetic nervous system.",
            practicalTip = "Take a short 10-minute stroll around neighborhood trees without your phone.",
            quote = "“In every walk with nature one receives far more than he seeks.” — John Muir"
        ),
        DailyLearningCard(
            id = "learn_12",
            title = "The Two-Minute Rule",
            category = "Habit Science",
            iconName = "hourglass_empty",
            summary = "When you start a new habit, scale it down until it takes less than two minutes to perform.",
            deepDive = "Mastering the art of showing up is more important than optimizing an intense routine that burns out. A habit must be established before it can be expanded.",
            practicalTip = "Don't aim for 100 pages—read just one paragraph. Consistency creates identity.",
            quote = "“A journey of a thousand miles begins with a single step.” — Lao Tzu"
        ),
        DailyLearningCard(
            id = "learn_13",
            title = "Digital Sunset for Better Sleep",
            category = "Sleep & Energy",
            iconName = "bedtime",
            summary = "Dimming screen brightness and overhead LEDs 60 minutes before bed protects melatonin secretion.",
            deepDive = "Blue and bright white light suppresses pineal melatonin production, tricking the brain into thinking it is still daytime and delaying deep slow-wave restorative sleep.",
            practicalTip = "Switch phone to grayscale or warm night light mode an hour before retiring.",
            quote = "“Sleep is the golden chain that ties health and our bodies together.” — Thomas Dekker"
        ),
        DailyLearningCard(
            id = "learn_14",
            title = "Self-Compassion Beats Self-Criticism",
            category = "Mindset & Growth",
            iconName = "sentiment_satisfied",
            summary = "Kindness toward your setbacks generates higher resilience and accountability than harsh self-judgment.",
            deepDive = "Dr. Kristin Neff's research indicates that self-criticism activates the threat defense system (fight or flight), while self-compassion activates the mammalian caregiving system.",
            practicalTip = "If a task wasn't finished today, simply carry it over tomorrow with an understanding smile.",
            quote = "“Talk to yourself like you would to someone you love.” — Brené Brown"
        )
    )

    fun getTodayCard(dayOfYear: Int): DailyLearningCard {
        val index = (dayOfYear % cards.size + cards.size) % cards.size
        return cards[index]
    }
}
