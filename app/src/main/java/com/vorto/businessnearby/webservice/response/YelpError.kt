package com.vorto.businessnearby.webservice.response

data class YelpError(
    val description: String?,
    val code: String?,
    val field: String?,
    val instance: Any?,
)