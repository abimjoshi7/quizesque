package com.example.quizmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizmaster.model.QuizCategory
import com.example.quizmaster.ui.QuizViewModel
import com.example.quizmaster.ui.screens.HomeScreen
import com.example.quizmaster.ui.screens.QuizScreen
import com.example.quizmaster.ui.screens.ResultScreen
import com.example.quizmaster.ui.screens.SplashScreen
import com.example.quizmaster.ui.theme.QuizMasterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizMasterTheme {
                QuizApp()
            }
        }
    }
}

@Composable
fun QuizApp(quizViewModel: QuizViewModel = viewModel()) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onFinished = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onCategoryClick = { category ->
                    quizViewModel.startQuiz(category)
                    navController.navigate("quiz/${category.id}")
                }
            )
        }
        composable("quiz/{categoryId}") {
            QuizScreen(
                viewModel = quizViewModel,
                onQuizFinished = {
                    navController.navigate("result") {
                        popUpTo("home")
                    }
                },
                onExit = {
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }
        composable("result") {
            ResultScreen(
                viewModel = quizViewModel,
                onPlayAgain = {
                    quizViewModel.restart()
                    val id = quizViewModel.category?.id ?: QuizCategory.GK.id
                    navController.navigate("quiz/$id") {
                        popUpTo("home")
                    }
                },
                onHome = {
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }
    }
}
