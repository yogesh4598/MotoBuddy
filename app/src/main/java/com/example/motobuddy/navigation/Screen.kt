package com.example.motobuddy.navigation

sealed class Screen(val route: String) {
    // A. Onboarding and account
    object Splash : Screen("SplashScreen")
    object Welcome : Screen("WelcomeScreen")
    object Onboarding : Screen("OnboardingScreen")
    object Login : Screen("LoginScreen")
    object Register : Screen("RegisterScreen")
    object ForgotPassword : Screen("ForgotPasswordScreen")
    object UserProfileSetup : Screen("UserProfileSetupScreen")

    // Landing from old code
    object MotoBuddyLanding : Screen("MotoBuddyScreen")
    object MainHost : Screen("MainHostScreen")

    // B. Home and dashboard
    object Dashboard : Screen("DashboardScreen")
    object Notification : Screen("NotificationScreen")
    object Search : Screen("SearchScreen")
    object Settings : Screen("SettingsScreen")

    // C. Trip planning and navigation
    object TripPlanning : Screen("TripPlanningScreen")
    object RouteSelection : Screen("RouteSelectionScreen")
    object RouteDetails : Screen("RouteDetailsScreen")
    object LiveNavigation : Screen("LiveNavigationScreen")
    object ScenicRoutes : Screen("ScenicRoutesScreen")
    object TouristPlaces : Screen("TouristPlacesScreen")
    object TripHistory : Screen("TripHistoryScreen")
    object FuelCostCalculator : Screen("FuelCostCalculatorScreen")
    object WeatherRoadConditions : Screen("WeatherRoadConditionsScreen")

    // D. Bike maintenance and mechanic assistant
    object MechanicAssistant : Screen("MechanicAssistantScreen")
    object Chatbot : Screen("ChatbotScreen")
    object BikeDiagnostics : Screen("BikeDiagnosticsScreen")
    object MaintenanceSchedule : Screen("MaintenanceScheduleScreen")
    object ServiceHistory : Screen("ServiceHistoryScreen")
    object NearbyRepairShops : Screen("NearbyRepairShopsScreen")
    object RepairShopDetails : Screen("RepairShopDetailsScreen")
    object NearbyPetrolPumps : Screen("NearbyPetrolPumpsScreen")

    // E. Safety and emergency services
    object Safety : Screen("SafetyScreen")
    object EmergencySOS : Screen("EmergencySOSScreen")
    object EmergencyContacts : Screen("EmergencyContactsScreen")
    object AccidentAssistance : Screen("AccidentAssistanceScreen")
    object NearbyHospitals : Screen("NearbyHospitalsScreen")
    object BikeDocuments : Screen("BikeDocumentsScreen")

    // F. Community and group rides
    object Community : Screen("CommunityScreen")
    object GroupRides : Screen("GroupRidesScreen")
    object CreateGroupRide : Screen("CreateGroupRideScreen")
    object GroupRideDetails : Screen("GroupRideDetailsScreen")
    object TripSharing : Screen("TripSharingScreen")
    object RiderChat : Screen("RiderChatScreen")

    // G. Profile and preferences
    object MyProfile : Screen("MyProfileScreen")
    object EditProfile : Screen("EditProfileScreen")
    object MyBike : Screen("MyBikeScreen")
    object Achievements : Screen("AchievementsScreen")
    object AppPreferences : Screen("AppPreferencesScreen")
    object About : Screen("AboutScreen")
}
