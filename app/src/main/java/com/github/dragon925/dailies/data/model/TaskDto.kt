package com.github.dragon925.dailies.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: Int,
    @SerialName("date_start") val dateStart: Long,
    @SerialName("date_finish") val dateEnd: Long,
    val name: String,
    val description: String
)
