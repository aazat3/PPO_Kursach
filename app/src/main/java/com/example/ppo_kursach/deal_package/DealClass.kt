package com.example.ppo_kursach.deal_package

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.w3c.dom.Comment

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
    val status: Int,
    val comment: String
): Parcelable {
    constructor() : this(0, 0, "", "", "", "", 0, 0, 0, "") {}
    constructor(idDeal: Int) : this(idDeal = idDeal, idUser = 0, date = "", address = "", client = "", clientNumber = "", price = 0, difficulty = 0, status = 0, comment = "") {}
    constructor(idDeal: Int, idUser: Int) : this(idDeal = idDeal, idUser = idUser, date = "", address = "", client = "", clientNumber = "", price = 0, difficulty = 0, status = 0, comment = "") {}

//    constructor(date:String, address:String) : this(0, date, address, "", "") {}

}