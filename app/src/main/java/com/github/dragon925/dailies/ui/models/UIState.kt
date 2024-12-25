package com.github.dragon925.dailies.ui.models

data class UIState<T>(
    val isLoading: Boolean = false,
    val error: String? = null,
    val state: T? = null
) {
    val isError = error != null
    val isCorrect = isLoading || isError || state != null
}
