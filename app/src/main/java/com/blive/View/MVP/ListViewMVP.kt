package com.blive.View.MVP

import com.blive.Models.ListUsers
import java.util.ArrayList

interface ListViewMVP {
    interface MainView {
        fun setError(error: String?)
        fun setAdapterData(data: ListUsers.listDetails)
    }
    interface MainPresenter {
        fun userList(
            userId: String,
            action: String,
            page : String,
            length: String
        )
    }
}