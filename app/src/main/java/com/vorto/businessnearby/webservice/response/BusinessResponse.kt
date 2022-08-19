package com.vorto.businessnearby.webservice.response
import com.vorto.businessnearby.model.BusinessModel

data class BusinessResponse(
    val businesses: List<BusinessModel>?,
    val error: YelpError?,
)