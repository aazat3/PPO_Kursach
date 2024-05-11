package com.example.ppo_kursach

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DealClass(
    val idDeal :Int,
    val idUser: Int,
    val date:String,
    val address:String,
    val client:String,
    val clientNumber: String,
    val price: Int,
    val difficulty: Int,
    val status: Int
): Parcelable {
    constructor() : this(0, 0, "", "", "", "", 0, 0, 0) {}
    constructor(idDeal: Int) : this(idDeal = idDeal, idUser = 0, date = "", address = "", client = "", clientNumber = "", price = 0, difficulty = 0, status = 0) {}

//    constructor(date:String, address:String) : this(0, date, address, "", "") {}

}