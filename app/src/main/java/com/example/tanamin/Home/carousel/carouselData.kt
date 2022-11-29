package com.example.tanamin.Home.carousel

import com.example.tanamin.R

object carouselData {

    private val kategori = arrayOf(
        "alat",
        "bahan",
        "product"
    )

    private val carouselImages = intArrayOf(
        R.drawable.carousel_alat,
        R.drawable.carousel_bahan,
        R.drawable.carousel_product
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