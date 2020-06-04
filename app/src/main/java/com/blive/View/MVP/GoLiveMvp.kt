package com.blive.View.MVP

import com.blive.Models.AudienceModel
import com.blive.Models.BroadProfileBody
import com.blive.Models.GuestResponse


interface GoLiveMvp {
     interface MainView {
        fun setError(error: String?)
        fun success(status: String?,message: String?)
        fun getprofilesuccess(status: String?,message: String?,broadProfileBody: BroadProfileBody)
        fun getaudiencesuccess(status: String?,message: String?,audienceModel: AudienceModel)
        fun addguestsuccess(status: String?,message: String?,guestResponse: GuestResponse)
    }

     interface MainPresenter {
        fun golive(
            action: String,
            broadcast_type: String,
            broadcasting_time : String,
            idle_time: String,
            actual_broadcasting_time: String
        )
        fun getprofile(
            action: String,
            user_id: String
        )
        fun getaudience(
            action: String,page: String,length: String,
            user_id: String
        )
        fun addguest(
            action: String,page: String,length: String,
            user_id: String
        )

     }
}