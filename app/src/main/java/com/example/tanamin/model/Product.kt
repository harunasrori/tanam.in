package com.example.tanamin.model

data class Product(
    var pid: String = "",
    var nama_product: String = "",
    var kategori: String = "",
    var deskripsi: String = "",
    var link_tokopedia: String = "",
    var harga: String = "",
    var photo: String = "",
    var favourite : Boolean = false
)