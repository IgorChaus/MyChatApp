package com.example.mychatapp

data class Message(
    val author: String = "",
    val textOfMessage: String = "",
    val date: Long = 0
)