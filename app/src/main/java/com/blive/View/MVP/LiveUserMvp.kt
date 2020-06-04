package com.blive.View.MVP

import com.blive.Models.ActiveUsers


interface LiveUserMvp {
     interface MainView {
        fun setError(error: String?)
        fun settoken(token: String?)
        fun setAdapterData(data: ArrayList<ActiveUsers.Datauser>)
    }

     interface MainPresenter {
        fun loadData(
            action: String,
            page: String,
            type : String,
            geo: String,
            country: String,
            city: String,
            length: String
        )

         fun gettoken()
     }
}