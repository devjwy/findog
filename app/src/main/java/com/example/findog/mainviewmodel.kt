package com.example.findog

import androidx.lifecycle.ViewModel

class mainviewmodel : ViewModel() {
    val profileList = arrayListOf(
        Profiles(R.drawable.phoneman, "이지수"),
        Profiles(R.drawable.phoneman, "이채윤"),
        Profiles(R.drawable.phoneman, "윤지우"),
        Profiles(R.drawable.phoneman, "졸프"),
        Profiles(R.drawable.phoneman, "힘들다"),
        Profiles(R.drawable.phoneman, "벌써.."))

}