package com.example.tryapp.service

import com.example.codeqrscanner.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface RetrofitService {
    @POST("apply")
    fun apply(@Body student: User): Call<ResponsePass>

//    @GET("findUser/{email}")
//    suspend fun findUser(@Path("email") email: String): Response<Compte>
    @GET("findUser/{id}")
    fun findUser(@Path("id") id: Int):  Call<Compte>
}