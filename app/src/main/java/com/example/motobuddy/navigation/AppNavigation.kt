package com.example.motobuddy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import com.example.motobuddy.MotoBuddyScreen
import com.example.motobuddy.ui.screens.community.*
import com.example.motobuddy.ui.screens.home.*
import com.example.motobuddy.ui.screens.maintenance.*
import com.example.motobuddy.ui.screens.onboarding.*
import com.example.motobuddy.ui.screens.profile.*
import com.example.motobuddy.ui.screens.safety.*
import com.example.motobuddy.ui.screens.tripplanning.*

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.MotoBuddyLanding.route) {
        composable(Screen.MotoBuddyLanding.route) {
            MotoBuddyScreen(navController = navController)
        }
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Welcome.route) { WelcomeScreen(navController) }
        composable(Screen.Onboarding.route) { OnboardingScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController) }
        composable(Screen.UserProfileSetup.route) { UserProfileSetupScreen(navController) }

        composable(Screen.MainHost.route) { MainHostScreen(navController) }

        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.Notification.route) { NotificationScreen(navController) }
        composable(Screen.Search.route) { SearchScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }

        composable(Screen.TripPlanning.route) { TripPlanningScreen(navController) }
        composable(Screen.RouteSelection.route) { RouteSelectionScreen(navController) }
        composable(Screen.RouteDetails.route) { RouteDetailsScreen(navController) }
        composable(Screen.LiveNavigation.route) { LiveNavigationScreen(navController) }
        composable(Screen.ScenicRoutes.route) { ScenicRoutesScreen(navController) }
        composable(Screen.TouristPlaces.route) { TouristPlacesScreen(navController) }
        composable(Screen.TripHistory.route) { TripHistoryScreen(navController) }
        composable(Screen.FuelCostCalculator.route) { FuelCostCalculatorScreen(navController) }
        composable(Screen.WeatherRoadConditions.route) { WeatherRoadConditionsScreen(navController) }

        composable(Screen.MechanicAssistant.route) { MechanicAssistantScreen(navController) }
        composable(Screen.Chatbot.route) { ChatbotScreen(navController) }
        composable(Screen.BikeDiagnostics.route) { BikeDiagnosticsScreen(navController) }
        composable(Screen.MaintenanceSchedule.route) { MaintenanceScheduleScreen(navController) }
        composable(Screen.ServiceHistory.route) { ServiceHistoryScreen(navController) }
        composable(Screen.NearbyRepairShops.route) { NearbyRepairShopsScreen(navController) }
        composable(Screen.RepairShopDetails.route) { RepairShopDetailsScreen(navController) }
        composable(Screen.NearbyPetrolPumps.route) { NearbyPetrolPumpsScreen(navController) }

        composable(Screen.Safety.route) { SafetyScreen(navController) }
        composable(Screen.EmergencySOS.route) { EmergencySOSScreen(navController) }
        composable(Screen.EmergencyContacts.route) { EmergencyContactsScreen(navController) }
        composable(Screen.AccidentAssistance.route) { AccidentAssistanceScreen(navController) }
        composable(Screen.NearbyHospitals.route) { NearbyHospitalsScreen(navController) }
        composable(Screen.BikeDocuments.route) { BikeDocumentsScreen(navController) }

        composable(Screen.Community.route) { CommunityScreen(navController) }
        composable(Screen.GroupRides.route) { GroupRidesScreen(navController) }
        composable(Screen.CreateGroupRide.route) { CreateGroupRideScreen(navController) }
        composable(Screen.GroupRideDetails.route) { GroupRideDetailsScreen(navController) }
        composable(Screen.TripSharing.route) { TripSharingScreen(navController) }
        composable(Screen.RiderChat.route) { RiderChatScreen(navController) }

        composable(Screen.MyProfile.route) { MyProfileScreen(navController) }
        composable(Screen.EditProfile.route) { EditProfileScreen(navController) }
        composable(Screen.MyBike.route) { MyBikeScreen(navController) }
        composable(Screen.Achievements.route) { AchievementsScreen(navController) }
        composable(Screen.AppPreferences.route) { AppPreferencesScreen(navController) }
        composable(Screen.About.route) { AboutScreen(navController) }
    }
}
