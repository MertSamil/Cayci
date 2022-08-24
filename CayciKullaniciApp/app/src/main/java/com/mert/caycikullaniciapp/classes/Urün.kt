package com.mert.caycikullaniciapp.classes

var urunList = mutableListOf<Urün>()

class Urün (
    var kapak : Long,
    var isim : String,
    var adet : Int,
    val id : Int? = urunList.size
            )