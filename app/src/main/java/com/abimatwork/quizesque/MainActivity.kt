package com.abimatwork.quizesque

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abimatwork.quizesque.model.QuizCategory
import com.abimatwork.quizesque.ui.QuizViewModel
import com.abimatwork.quizesque.ui.screens.HomeScreen
import com.abimatwork.quizesque.ui.screens.QuizScreen
import com.abimatwork.quizesque.ui.screens.ResultScreen
import com.abimatwork.quizesque.ui.screens.SplashScreen
import com.abimatwork.quizesque.ui.theme.QUIZesqueTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QUIZesqueTheme {
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
                },
                onEnter = { quizViewModel.prefetch() }
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
