package com.example.working

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ResetValuesReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val userDao = MainDb.getDb(context).getDao()

        Thread{userDao.resetValues()}.start()
    }
}