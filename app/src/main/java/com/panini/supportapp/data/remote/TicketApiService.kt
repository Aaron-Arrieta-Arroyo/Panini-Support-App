package com.panini.supportapp.data.remote

import com.panini.supportapp.data.dto.CreateTicketRequestDto
import com.panini.supportapp.data.dto.LoginRequestDto
import com.panini.supportapp.data.dto.LoginResponseDto
import com.panini.supportapp.data.dto.TicketDto
import com.panini.supportapp.data.dto.TicketListResponseDto
import com.panini.supportapp.data.dto.UpdatePriorityRequestDto
import com.panini.supportapp.data.dto.UpdateStatusRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface defining the full API contract.
 * All endpoints mirror the YAML contract defined in /contracts/tickets-api.yaml.
 *
 * When the backend is ready, swap MockTicketRepository → RemoteTicketRepository
 * in AppContainer. No other file needs to change.
 */
interface TicketApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @GET("tickets")
    suspend fun getTickets(
        @Query("status") status: String? = null,
        @Query("priority") priority: String? = null,
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): TicketListResponseDto

    @GET("tickets/{id}")
    suspend fun getTicketById(@Path("id") id: String): TicketDto

    @POST("tickets")
    suspend fun createTicket(@Body request: CreateTicketRequestDto): TicketDto

    @PATCH("tickets/{id}/status")
    suspend fun updateTicketStatus(
        @Path("id") id: String,
        @Body request: UpdateStatusRequestDto
    ): TicketDto

    @PATCH("tickets/{id}/priority")
    suspend fun updateTicketPriority(
        @Path("id") id: String,
        @Body request: UpdatePriorityRequestDto
    ): TicketDto
}
