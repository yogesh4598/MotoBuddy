package com.example.motobuddy.data.remote.api

import com.example.motobuddy.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface MotoBuddyApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponseData>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponseData>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @GET("user/profile")
    suspend fun getUserProfile(): Response<ApiResponse<UserProfileDto>>

    @GET("motorcycles")
    suspend fun getMotorcycles(): Response<ApiResponse<List<MotorcycleDto>>>

    @POST("motorcycles")
    suspend fun addMotorcycle(@Body motorcycle: MotorcycleDto): Response<ApiResponse<MotorcycleDto>>

    @GET("trips")
    suspend fun getTrips(): Response<ApiResponse<List<TripDto>>>

    @POST("trips")
    suspend fun addTrip(@Body trip: TripDto): Response<ApiResponse<TripDto>>

    @DELETE("trips/{id}")
    suspend fun deleteTrip(@Path("id") id: String): Response<ApiResponse<Unit>>

    @GET("maintenance")
    suspend fun getMaintenanceRecords(): Response<ApiResponse<List<MaintenanceDto>>>

    @POST("maintenance")
    suspend fun addMaintenanceRecord(@Body record: MaintenanceDto): Response<ApiResponse<MaintenanceDto>>

    @POST("mechanic/chat")
    suspend fun sendMechanicChatMessage(@Body request: ChatRequestDto): Response<ApiResponse<ChatResponseDto>>
}
