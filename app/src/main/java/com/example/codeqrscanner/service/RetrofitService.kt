package com.example.tryapp.service

import com.example.codeqrscanner.User
import com.example.codeqrscanner.Compte
import com.example.codeqrscanner.ResponsePass
import com.example.codeqrscanner.StudentInfo
import retrofit2.Response
import retrofit2.http.*


interface RetrofitService {
    @POST("apply")
    suspend fun apply(@Body student: User): Response<ResponsePass>

    @GET("findUser/{email}")
    suspend fun findUser(@Path("email") email: String): Response<Compte>
}