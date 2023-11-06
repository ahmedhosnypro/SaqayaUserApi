package com.saqaya.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime


class ErrorResponse(
    private val title: String,
    private val message: String? = null,
    private val details: MutableMap<String, String> = mutableMapOf(),
) {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private val timestamp: LocalDateTime = LocalDateTime.now()
}