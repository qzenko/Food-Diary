package com.example.working

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "izbr")
data class ItemsIzbr (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "kkl")
    var kkl: Double,
    @ColumnInfo(name = "belki")
    var belki: Double,
    @ColumnInfo(name = "ziri")
    var ziri: Double,
    @ColumnInfo(name = "uglevodi")
    var uglevodi: Double
)