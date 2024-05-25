package com.example.ppo_kursach.users_deal_package

class UsersDealClass(
    val idUsersDeal: Int,
    val idUser: Int,
    val idDeal: Int
) {
    constructor() : this(0, 0, 0) {}
//    constructor(idUser: Int) : this(idUser,"", "", "", "", "")  {}
//    constructor(idUser: Int, login: String, uid: String) : this(idUser,"", uid, "", login, "")  {}
}