package com.example.web

data class PostUsersResponse(val id: Int)

data class GetUsersResponse(val result: List<GetUserResponse>)

data class GetUserResponse(val id: Int, val name: String, val gender: String, val phone: String)

data class ErrorResponse(val message: String)