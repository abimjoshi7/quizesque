package com.abimatwork.quizesque.data

import com.abimatwork.quizesque.model.Question
import com.abimatwork.quizesque.model.QuizCategory
import com.abimatwork.quizesque.model.QuizCategory.*

object QuestionBank {

    val all: List<Question> by lazy { gkQuestions + wordQuestions + riddleQuestions }

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

    // ---------- 3. RIDDLES (10) ----------
    private val riddleQuestions = listOf(
        Question(
            id = 201, category = RIDDLE,
            question = "I speak without a mouth and hear without ears. I have no body, but I come alive with wind. What am I?",
            options = listOf("Echo", "Shadow", "Smoke", "Whistle"),
            correctIndex = 0,
            explanation = "An echo 'speaks' by bouncing sound back and needs no body of its own."
        ),
        Question(
            id = 202, category = RIDDLE,
            question = "The more you take, the more you leave behind. What am I?",
            options = listOf("Memories", "Footsteps", "Photographs", "Souvenirs"),
            correctIndex = 1,
            explanation = "Every step you take leaves another footprint behind you."
        ),
        Question(
            id = 203, category = RIDDLE,
            question = "What has keys but no locks, space but no room, and you can enter but cannot go inside?",
            options = listOf("A theatre", "A keyboard", "A lift", "A library"),
            correctIndex = 1,
            explanation = "A keyboard has keys and space, yet you cannot physically enter it."
        ),
        Question(
            id = 204, category = RIDDLE,
            question = "I have cities but no houses, forests but no trees, and water but no fish. What am I?",
            options = listOf("A dream", "A map", "A photograph", "A book"),
            correctIndex = 1,
            explanation = "A map shows places and features, but they are only drawings."
        ),
        Question(
            id = 205, category = RIDDLE,
            question = "What gets wetter the more it dries?",
            options = listOf("A sponge", "A towel", "The rain", "Soap"),
            correctIndex = 1,
            explanation = "A towel absorbs water, so it becomes wet as it dries something else."
        ),
        Question(
            id = 206, category = RIDDLE,
            question = "What has one eye but cannot see?",
            options = listOf("A needle", "A storm", "A potato", "A camera"),
            correctIndex = 0,
            explanation = "The hole of a needle is called its eye, though it has no sight."
        ),
        Question(
            id = 207, category = RIDDLE,
            question = "What runs but never walks, has a bed but never sleeps, and has a mouth but never speaks?",
            options = listOf("A dog", "A river", "A clock", "A road"),
            correctIndex = 1,
            explanation = "A river runs, has a riverbed, and a mouth where it meets the sea."
        ),
        Question(
            id = 208, category = RIDDLE,
            question = "Forward I am heavy, backward I am not. What am I?",
            options = listOf("Ton", "Note", "Peek", "Pots"),
            correctIndex = 0,
            explanation = "'Ton' is a heavy weight; spelled backwards it reads 'not'."
        ),
        Question(
            id = 209, category = RIDDLE,
            question = "I am an odd number. Take away a letter and I become even. What number am I?",
            options = listOf("Nine", "Three", "Seven", "Five"),
            correctIndex = 2,
            explanation = "Remove the leading 's' from 'seven' and 'even' remains."
        ),
        Question(
            id = 210, category = RIDDLE,
            question = "What can travel around the world while staying in one corner?",
            options = listOf("A stamp", "A kite", "A rumour", "Sunlight"),
            correctIndex = 0,
            explanation = "A postage stamp stays in the corner of an envelope yet crosses the world."
        )
    )
}
