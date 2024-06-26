package com.example.working

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface Dao {
    @Insert
    fun insertUser(users: Users)
    @Insert
    fun insertItem(items: Items)
    @Insert
    fun insertIzbr(itemsIzbr: ItemsIzbr)
    @Query("SELECT * FROM products")
    fun getItem(): List<Items>
    @Query("SELECT * FROM izbr")
    fun getItemIzbr(): List<ItemsIzbr>
    @Query("SELECT * FROM users")
    fun getUser(): Users
    @Delete
    fun deleteItem(item: Items)
    @Delete
    fun deleteItemIzbr(itemsIzbr: ItemsIzbr)
    @Query("SELECT * FROM users LIMIT 1")
    fun getFirstUser(): Users
    @Update
    fun updateItem(item: Items)
    @Update
    fun updateUser(users: Users)
    @Query("SELECT * FROM products WHERE podschet <= :tomorrow")
    fun getProductsExpiringTomorrow(tomorrow: Long): List<Items>
    @Query("UPDATE users SET todayKkl = 0, todayBelki = 0, todayZiri = 0, todayUglevodi = 0")
    fun resetValues()
}