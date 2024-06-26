package com.example.working
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "users")
data class Users (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "weight")
    var weight: Double,
    @ColumnInfo(name = "height")
    var height: Double,
    @ColumnInfo(name = "old")
    var years: Int,
    @ColumnInfo(name = "gender")
    var gender: String,
    @ColumnInfo(name = "active")
    var active: String,
    @ColumnInfo(name = "todayKkl")
    var todayKkl: Double,
    @ColumnInfo(name = "todayBelki")
    var todayBelki: Double,
    @ColumnInfo(name = "todayZiri")
    var todayZiri: Double,
    @ColumnInfo(name = "todayUglevodi")
    var todayUglevodi: Double,
)