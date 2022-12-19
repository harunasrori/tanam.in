package com.example.tanamin.Data

import com.example.tanamin.R
import com.example.tanamin.model.Carousel

object Dummy {

    fun getComments() = listOf<Carousel>(
        Carousel(
            0,
            "semua",
            R.drawable.carousel_product_default

        ),
        Carousel(
            1,
            "alat",
            R.drawable.carousel_alat

        ),
        Carousel(
            2,
            "bahan",
            R.drawable.carousel_bahan

        ),
        Carousel(
            3,
            "hasil",
            R.drawable.carousel_hasil

        )
    )
}