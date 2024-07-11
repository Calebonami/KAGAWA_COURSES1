package com.onami.kagawacourses

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun NavGraph(navController: NavHostController, startDestination: String = "frontpage") {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("signUpScreen") { SignUpScreen(navController) }
        composable("frontpage") { FrontPage(navController) }
        composable("loginScreen") { LoginScreen(navController) }
        composable("mainActivity") { MainScreen(navController) }
        composable("myCoursesScreen") { MyCoursesScreen(navController) }
        composable("profileCompletionScreen") { ProfileCompletionScreen(navController) }
        composable(
            route = "courseDetails/{title}/{price}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("price") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title")
            val price = backStackEntry.arguments?.getString("price")
            CourseDetailsScreen(title = title, price = price, navController = navController)
        }
        composable(
            route = "payment/{title}/{price}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("price") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title")
            val price = backStackEntry.arguments?.getString("price")
            if (title != null && price != null) {
                PaymentScreen(courseTitle = title, coursePrice = price, navController = navController)
            }
        }
        composable("settingsScreen") { SettingsScreen(navController) }
        composable("profileScreen") { ProfileScreen(navController) }

        // Settings options
        composable("editProfile") { EditProfileScreen(navController) }
        composable("changeEmail") { ChangeEmailScreen(navController) }
        composable("changePassword") { ChangePasswordScreen(navController) }
        composable("privacyPolicy") { PrivacyPolicyScreen(navController) }
        composable("SecurityScreen") { SecurityScreen(navController) }
        composable("emailNotifications") { EmailNotificationsScreen(navController) }
        composable("pushNotifications") { PushNotificationsScreen(navController) }
        composable("soundAndVibration") { SoundAndVibrationScreen(navController) }

        // Security options
        composable("twoFactorAuthentication") { TwoFactorAuthenticationScreen(navController) }
        composable("securityQuestions") { SecurityQuestionsScreen(navController) }
        composable("accountActivityLogs") { AccountActivityLogsScreen(navController) }
        composable("biometricAuthentication") { BiometricAuthenticationScreen(navController) }
        composable("sessionManagement") { SessionManagementScreen(navController) }
    }
}
