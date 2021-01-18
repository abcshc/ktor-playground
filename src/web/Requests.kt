package com.example.web

data class PostUsersRequest(val name: String, val gender: Char, val phone: String)

data class PutUsersRequest(val id: Int, val name: String, val gender: Char, val phone: String)

data class DeleteUsersRequest(val id: Int)