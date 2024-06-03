package com.example.ppo_kursach.user_package

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserClass(
    val idUser :Int,
    val name: String,
    val uid: String,
    val type:Int,
    val login:String,
    val userNumber:String
): Parcelable {
    constructor() : this(0, "", "", 0, "", "") {}
    constructor(idUser: Int) : this(idUser,"", "", 0, "", "")  {}
    constructor(idUser: Int, login: String, uid: String) : this(idUser,"", uid, 0, login, "")  {}

//    constructor(date:String, address:String) : this(0, date, address, "", "") {}

}