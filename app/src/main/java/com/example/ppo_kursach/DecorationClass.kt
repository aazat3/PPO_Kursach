package com.example.ppo_kursach

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DecorationClass(
    val idDecoration:Int,
    val name: String,
    val type: Int,
    val quantity:Int,
    val condition:Int,
    val price:Int,
    val difficultyInst: Int,
    val difficultyTr: Int,
    val photo: String
) : Parcelable {
    constructor() : this(0, "", 0, 0, 0, 0, 0, 0, "") {}
    constructor(idDecoration: Int) : this(idDecoration = idDecoration, name = "", type = 0, quantity = 0, condition = 0, price = 0, difficultyInst = 0, difficultyTr = 0, photo = "") {}

//    constructor(date:String, address:String) : this(0, date, address, "", "") {}

}