package com.example.tanamin.UI.home.carousel

import com.example.tanamin.R
import com.example.tanamin.model.Carousel

object carouselData {

    private val kategori = arrayOf(
        "semua",
        "alat",
        "bahan",
        "hasil"
    )

    private val carouselImages = intArrayOf(
        R.drawable.carousel_product_default,
        R.drawable.carousel_alat,
        R.drawable.carousel_bahan,
        R.drawable.carousel_hasil
    )

    val listData: ArrayList<Carousel>
        get() {
            val list = arrayListOf<Carousel>()
            for (position in carouselImages.indices) {
                val carousel = Carousel()
                carousel.kategori = kategori[position]
                carousel.photo = carouselImages[position]
                list.add(carousel)
            }
            return list
        }
}