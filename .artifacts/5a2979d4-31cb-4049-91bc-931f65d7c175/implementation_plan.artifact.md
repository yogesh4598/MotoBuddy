# MotoBuddy Enhancement Plan

This plan details the comprehensive upgrade of the MotoBuddy app to align with the provided design guidelines, implement missing functionality, and build robust features like trip planning, maintenance tracking, and an AI mechanic assistant.

## User Review Required

> [!IMPORTANT]
> - **AI Integration**: To implement a true AI assistant, we'll need an API Key (like a Google Gemini API Key). If not provided, we will build an offline mock chatbot, but the architecture will support the real API.
> - **Maps & Places Integration**: For routing, trip planning, and nearby services, Google Maps and Places SDKs are typically used, which require an API Key. Should I implement intent-based handoffs (opening Google Maps app) as a free/keyless alternative, or do you have a Maps API key?

## Open Questions

1.  Do you have a preferred API (e.g., Google Gemini) for the AI Mechanic, and are you comfortable providing an API key during the development process (we can store it securely in `local.properties`)?
2.  For "Nearby Services", is it acceptable to use Android `Intent`s to launch Google Maps with specific search queries (e.g., "petrol pumps near me") to avoid requiring Google Maps API billing?

## Proposed Changes

### 1. Theme and UI Upgrade
*   **[MODIFY]** `app/src/main/java/com/example/motobuddy/ui/theme/Color.kt`
    *   Add new premium palette: Midnight Navy, Dark Graphite, Electric Cyan, Vibrant Orange, etc.
*   **[MODIFY]** `app/src/main/java/com/example/motobuddy/ui/theme/Theme.kt`
    *   Update `MaterialTheme` color scheme to use the new colors globally.
*   **[MODIFY]** `app/src/main/java/com/example/motobuddy/MainActivity.kt`
    *   Refactor inline hardcoded colors to use `MaterialTheme.colorScheme`.

### 2. Architecture & Persistence (Room Database setup)
*   **[MODIFY]** `app/build.gradle.kts`
    *   Add Room dependencies, ViewModel dependencies, and API client libraries (like Ktor or Retrofit, if needed for AI).
*   **[NEW]** `app/src/main/java/com/example/motobuddy/data/AppDatabase.kt`
    *   Create Room database setup.
*   **[NEW]** `app/src/main/java/com/example/motobuddy/data/entities/`
    *   `Trip.kt`: Entity for planned trips.
    *   `MaintenanceRecord.kt`: Entity for service history.

### 3. Home Dashboard Enhancements
*   **[MODIFY]** `app/src/main/java/com/example/motobuddy/ui/screens/home/DashboardScreen.kt`
    *   Apply new theme colors.
    *   Wire up real navigation intents for "Fuel", "Weather" (placeholder), and "Safety".
    *   Make the "Recent Plan" card dynamic or properly mock it to point to a Trip details screen.

### 4. Functional Trip Planning & Fuel Calculator
*   **[MODIFY]** `app/src/main/java/com/example/motobuddy/ui/screens/tripplanning/TripPlanningScreen.kt`
    *   Update UI.
*   **[MODIFY]** `app/src/main/java/com/example/motobuddy/ui/screens/tripplanning/RouteSelectionScreen.kt`
    *   Implement input fields for Start/End.
    *   Calculate distance/time using dummy logic (or real API if requested).
    *   Integrate the Fuel Calculator directly into the trip summary.
*   **[MODIFY]** `app/src/main/java/com/example/motobuddy/ui/screens/tripplanning/FuelCostCalculatorScreen.kt`
    *   Implement working formula: `(Distance / Mileage) * Fuel Price`.

### 5. Motorcycle Maintenance & AI Chatbot
*   **[MODIFY]** `app/src/main/java/com/example/motobuddy/ui/screens/maintenance/MaintenanceScheduleScreen.kt`
    *   Build UI to view and add maintenance records (Oil, Chain, etc.) connected to the Room database.
*   **[MODIFY]** `app/src/main/java/com/example/motobuddy/ui/screens/maintenance/MechanicAssistantScreen.kt`
    *   Refactor the static chat into a dynamic `LazyColumn`.
    *   Implement an offline rule-based bot fallback (e.g., if user types "battery", respond with battery advice) OR integrate Gemini API for real AI responses.

### 6. Nearby Services (Intent-based)
*   **[MODIFY]** `app/src/main/java/com/example/motobuddy/ui/screens/maintenance/NearbyPetrolPumpsScreen.kt` & `NearbyRepairShopsScreen.kt`
    *   Implement UI that triggers `geo:0,0?q=petrol+pumps` intent to open Google Maps securely without API keys.

### 7. Navigation Cleanup
*   **[MODIFY]** `app/src/main/java/com/example/motobuddy/navigation/AppNavigation.kt`
    *   Ensure all screens are properly registered.
*   **[MODIFY]** `app/src/main/java/com/example/motobuddy/navigation/Screen.kt`
    *   Audit and clean up unused routes.

## Verification Plan

### Automated/Build Verification
- Run `gradlew app:assembleDebug` to ensure all UI changes and Room database implementations compile successfully.
- Ensure no unresolved references remain after adding new libraries.

### Manual Verification
- Deploy to emulator/device.
- Verify App theme uses Midnight Navy and Cyan/Orange accents consistently.
- Test Trip Planner inputs -> verify Fuel Cost calculation works.
- Test adding a maintenance record -> verify it appears in history.
- Test AI Chatbot -> verify it responds to "bike won't start".
- Click "Nearby Petrol Pumps" -> verify it launches the Maps intent.