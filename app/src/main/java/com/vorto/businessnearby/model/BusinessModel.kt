package com.vorto.businessnearby.model

data class BusinessModel(
    val id: String?,
    val name: String?,
    val rating: Double?,
    val distance: Double?,
    var deliver_at: String?,
    var display_phone: String?,
    var review_count: Int?,
    var image_url: String?,
    var location: BusinessLocation?,
    var coordinates: BusinessGPS?
)