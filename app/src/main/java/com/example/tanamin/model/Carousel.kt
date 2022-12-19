package com.example.tanamin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "carousel", indices = [Index(value = ["kategori"], unique = true)])
data class Carousel(@PrimaryKey(autoGenerate = true) var carousel_id: Long = 0,
                    @ColumnInfo(name = "kategori") var kategori: String = "",
                    @ColumnInfo(name = "photo")var photo: Int = 0
)