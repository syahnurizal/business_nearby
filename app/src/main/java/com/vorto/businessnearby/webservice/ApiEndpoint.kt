package com.vorto.businessnearby.webservice

import com.vorto.businessnearby.model.BusinessModel
import com.vorto.businessnearby.webservice.response.BusinessResponse
import io.reactivex.Observable
import retrofit2.http.*

interface ApiEndpoint {

    @Headers("Accept: application/json")
    @GET("businesses/search")
    fun searchBusiness(
        @Header("Authorization") token: String,
        @Query("term") term: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("limit") limit: String,
        @Query("radius") radius: String
    ): Observable<BusinessResponse>

    @Headers("Accept: application/json")
    @GET("businesses/{id}")
    fun businessDetail(
        @Header("Authorization") token: String,
        @Path("id") term: String
    ): Observable<BusinessModel?>
}