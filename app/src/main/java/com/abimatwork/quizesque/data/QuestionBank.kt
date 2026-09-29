package com.abimatwork.quizesque.data

import com.abimatwork.quizesque.model.Question
import com.abimatwork.quizesque.model.QuizCategory
import com.abimatwork.quizesque.model.QuizCategory.*

object QuestionBank {

    val all: List<Question> by lazy { gkQuestions + wordQuestions + sportsQuestions }

    fun byCategory(category: QuizCategory): List<Question> =
        all.filter { it.category == category }

    // ---------- 1. GENERAL KNOWLEDGE (10) ----------
    private val gkQuestions = listOf(
        Question(
            id = 1, category = GK,
            question = "What is the capital of Australia?",
            options = listOf("Sydney", "Melbourne", "Canberra", "Perth"),
            correctIndex = 2,
            explanation = "Canberra is the capital of Australia, not Sydney."
        ),
        Question(
            id = 2, category = GK,
            question = "Which planet is known as the Red Planet?",
            options = listOf("Venus", "Mars", "Jupiter", "Mercury"),
            correctIndex = 1,
            explanation = "Mars appears red due to iron oxide (rust) on its surface."
        ),
        Question(
            id = 3, category = GK,
            question = "Who wrote the play 'Romeo and Juliet'?",
            options = listOf("Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain"),
            correctIndex = 1,
            explanation = "William Shakespeare wrote Romeo and Juliet in the 1590s."
        ),
        Question(
            id = 4, category = GK,
            question = "What is the largest ocean on Earth?",
            options = listOf("Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean"),
            correctIndex = 3,
            explanation = "The Pacific Ocean covers about one-third of Earth's surface."
        ),
        Question(
            id = 5, category = GK,
            question = "How many continents are there in the world?",
            options = listOf("5", "6", "7", "8"),
            correctIndex = 2,
            explanation = "There are 7 continents: Asia, Africa, North & South America, Antarctica, Europe, Australia."
        ),
        Question(
            id = 6, category = GK,
            question = "Which gas do plants absorb for photosynthesis?",
            options = listOf("Oxygen", "Nitrogen", "Carbon dioxide", "Hydrogen"),
            correctIndex = 2,
            explanation = "Plants absorb carbon dioxide and release oxygen."
        ),
        Question(
            id = 7, category = GK,
            question = "In which year did India gain independence?",
            options = listOf("1945", "1947", "1950", "1952"),
            correctIndex = 1,
            explanation = "India gained independence on 15 August 1947."
        ),
        Question(
            id = 8, category = GK,
            question = "What is the chemical symbol for Gold?",
            options = listOf("Go", "Gd", "Au", "Ag"),
            correctIndex = 2,
            explanation = "Au comes from the Latin word 'Aurum' meaning gold."
        ),
        Question(
            id = 9, category = GK,
            question = "Which is the longest river in the world?",
            options = listOf("Amazon", "Nile", "Yangtze", "Mississippi"),
            correctIndex = 1,
            explanation = "The Nile (approx. 6,650 km) is traditionally considered the longest river."
        ),
        Question(
            id = 10, category = GK,
            question = "How many sides does a hexagon have?",
            options = listOf("5", "6", "7", "8"),
            correctIndex = 1,
            explanation = "Hexa- means six, so a hexagon has 6 sides."
        )
    )

    // ---------- 2. WORD / VOCABULARY (10) ----------
    private val wordQuestions = listOf(
        Question(
            id = 101, category = WORD,
            question = "Choose the correct meaning of 'Benevolent'.",
            options = listOf("Cruel", "Kind and generous", "Angry", "Lazy"),
            correctIndex = 1,
            explanation = "'Benevolent' means well-meaning and kindly."
        ),
        Question(
            id = 102, category = WORD,
            question = "Which word is a synonym of 'Happy'?",
            options = listOf("Sad", "Joyful", "Angry", "Tired"),
            correctIndex = 1,
            explanation = "'Joyful' means feeling or expressing great happiness."
        ),
        Question(
            id = 103, category = WORD,
            question = "Which word is an antonym of 'Brave'?",
            options = listOf("Courageous", "Bold", "Cowardly", "Strong"),
            correctIndex = 2,
            explanation = "'Cowardly' is the opposite of brave."
        ),
        Question(
            id = 104, category = WORD,
            question = "Choose the correct spelling.",
            options = listOf("Occassion", "Ocassion", "Occasion", "Occasionn"),
            correctIndex = 2,
            explanation = "The correct spelling is O-C-C-A-S-I-O-N."
        ),
        Question(
            id = 105, category = WORD,
            question = "What does 'Ephemeral' mean?",
            options = listOf("Everlasting", "Short-lived", "Powerful", "Ancient"),
            correctIndex = 1,
            explanation = "'Ephemeral' means lasting for a very short time."
        ),
        Question(
            id = 106, category = WORD,
            question = "Which word is a synonym of 'Quick'?",
            options = listOf("Slow", "Rapid", "Late", "Idle"),
            correctIndex = 1,
            explanation = "'Rapid' means happening quickly."
        ),
        Question(
            id = 107, category = WORD,
            question = "Choose the antonym of 'Transparent'.",
            options = listOf("Clear", "Opaque", "Obvious", "Bright"),
            correctIndex = 1,
            explanation = "'Opaque' means not transparent."
        ),
        Question(
            id = 108, category = WORD,
            question = "Fill in the blank: She has a ___ vocabulary.",
            options = listOf("vast", "vastly", "vaster", "vastness"),
            correctIndex = 0,
            explanation = "Adjective 'vast' correctly describes the noun 'vocabulary'."
        ),
        Question(
            id = 109, category = WORD,
            question = "What does the idiom 'Break the ice' mean?",
            options = listOf(
                "To break frozen water",
                "To start a conversation",
                "To end a friendship",
                "To feel cold"
            ),
            correctIndex = 1,
            explanation = "'Break the ice' means to initiate conversation in a social setting."
        ),
        Question(
            id = 110, category = WORD,
            question = "Which word is the odd one out?",
            options = listOf("Apple", "Mango", "Potato", "Banana"),
            correctIndex = 2,
            explanation = "Potato is a vegetable; the rest are fruits."
        )
    )

    // ---------- 3. SPORTS (10) ----------
    private val sportsQuestions = listOf(
        Question(
            id = 201, category = SPORTS,
            question = "How many players does a cricket team have on the field?",
            options = listOf("9", "10", "11", "12"),
            correctIndex = 2,
            explanation = "A cricket team fields 11 players."
        ),
        Question(
            id = 202, category = SPORTS,
            question = "In which sport is the term 'Love' used for zero?",
            options = listOf("Badminton", "Tennis", "Squash", "Table Tennis"),
            correctIndex = 1,
            explanation = "In tennis, 'love' means a score of zero."
        ),
        Question(
            id = 203, category = SPORTS,
            question = "How often are the Olympic Games held?",
            options = listOf("Every 2 years", "Every 3 years", "Every 4 years", "Every 5 years"),
            correctIndex = 2,
            explanation = "The Olympics are held every 4 years."
        ),
        Question(
            id = 204, category = SPORTS,
            question = "In football, how many players does each team have on the pitch?",
            options = listOf("9", "10", "11", "12"),
            correctIndex = 2,
            explanation = "Each football team plays with 11 players."
        ),
        Question(
            id = 205, category = SPORTS,
            question = "Which country invented table tennis?",
            options = listOf("China", "Japan", "England", "USA"),
            correctIndex = 2,
            explanation = "Table tennis originated in England in the late 19th century."
        ),
        Question(
            id = 206, category = SPORTS,
            question = "What is the national sport of Japan?",
            options = listOf("Karate", "Judo", "Sumo wrestling", "Baseball"),
            correctIndex = 2,
            explanation = "Sumo wrestling is Japan's national sport."
        ),
        Question(
            id = 207, category = SPORTS,
            question = "How many rings are there on the Olympic flag?",
            options = listOf("4", "5", "6", "7"),
            correctIndex = 1,
            explanation = "Five interlocking rings represent the five continents."
        ),
        Question(
            id = 208, category = SPORTS,
            question = "In which sport would you perform a 'slam dunk'?",
            options = listOf("Volleyball", "Tennis", "Basketball", "Baseball"),
            correctIndex = 2,
            explanation = "A slam dunk is a basketball shot."
        ),
        Question(
            id = 209, category = SPORTS,
            question = "What is the maximum break in snooker?",
            options = listOf("147", "155", "100", "180"),
            correctIndex = 0,
            explanation = "147 is the maximum possible standard break in snooker."
        ),
        Question(
            id = 210, category = SPORTS,
            question = "Which trophy is awarded for Test cricket series between England and Australia?",
            options = listOf("World Cup", "The Ashes", "Champions Trophy", "Border-Gavaskar Trophy"),
            correctIndex = 1,
            explanation = "England vs Australia Test series is played for The Ashes."
        )
    )
}
