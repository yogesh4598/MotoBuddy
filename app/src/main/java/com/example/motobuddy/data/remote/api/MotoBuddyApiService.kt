package com.example.motobuddy.data.remote.api

import com.example.motobuddy.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface MotoBuddyApiService {

    @POST("api/v1/auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponseData>>

    @POST("api/v1/auth/register.php")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponseData>>

    @POST("api/v1/auth/logout.php")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @GET("api/v1/user/profile.php")
    suspend fun getUserProfile(): Response<ApiResponse<UserProfileDto>>

    @GET("api/v1/motorcycles/index.php")
    suspend fun getMotorcycles(): Response<ApiResponse<List<MotorcycleDto>>>

    @POST("api/v1/motorcycles/index.php")
    suspend fun addMotorcycle(@Body motorcycle: MotorcycleDto): Response<ApiResponse<MotorcycleDto>>

    @GET("api/v1/trips/index.php")
    suspend fun getTrips(): Response<ApiResponse<List<TripDto>>>

    @POST("api/v1/trips/index.php")
    suspend fun addTrip(@Body trip: TripDto): Response<ApiResponse<TripDto>>

    @DELETE("api/v1/trips/index.php")
    suspend fun deleteTrip(@Query("id") id: String): Response<ApiResponse<Unit>>

    @GET("api/v1/maintenance/index.php")
    suspend fun getMaintenanceRecords(): Response<ApiResponse<List<MaintenanceDto>>>

    @POST("api/v1/maintenance/index.php")
    suspend fun addMaintenanceRecord(@Body record: MaintenanceDto): Response<ApiResponse<MaintenanceDto>>

    @POST("api/v1/mechanic/chat.php")
    suspend fun sendMechanicChatMessage(@Body request: ChatRequestDto): Response<ApiResponse<ChatResponseDto>>
}
