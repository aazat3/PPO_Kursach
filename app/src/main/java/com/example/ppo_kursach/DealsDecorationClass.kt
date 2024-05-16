package com.example.ppo_kursach

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DealsDecorationClass(
    val idDealsDecoration:Int,
    val idDeal: Int,
    val idDecoration: Int,
    val quantity:Int,
    val price:Int,
): Parcelable {
    constructor() : this(0, 0, 0, 0, 0) {}
    constructor(idDealsDecoration: Int) : this(idDealsDecoration, 0, 0, 0, 0) {}
}