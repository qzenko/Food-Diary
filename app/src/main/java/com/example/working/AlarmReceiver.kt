package com.example.working

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager =
            ContextCompat.getSystemService(context!!, NotificationManager::class.java) as NotificationManager

        val notificationId = System.currentTimeMillis().toInt()

        val channelId = "channelId"
        val channelName = "channelName"
        val importance = NotificationManager.IMPORTANCE_HIGH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        val cal = Calendar.getInstance()
        if (cal.get(Calendar.HOUR_OF_DAY) == 18 && cal.get(Calendar.MINUTE) == 0) {
            val dao = MainDb.getDb(context).getDao()
            val user: Users = dao.getUser()

            if (user.gender == "М") {
                if(user.active == "Высокий" && ((user.weight * 10 + user.height * 6.25 - user.years + 5) * 1.9) != user.todayKkl)
                {
                    val builder = NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Пора перекусить!")
                        .setContentText("Не забудьте съесть норму калорий!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                    notificationManager.notify(notificationId, builder.build())
                }
                if (user.active == "Средний" && ((user.weight * 10 + user.height * 6.25 - user.years + 5) * 1.5) != user.todayKkl){
                    val builder = NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Пора перекусить!")
                        .setContentText("Не забудьте съесть норму калорий!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                    notificationManager.notify(notificationId, builder.build())
                }
               if (user.active == "Низкий" &&  ((user.weight * 10 + user.height * 6.25 - user.years + 5) * 1.2) != user.todayKkl){
                   val builder = NotificationCompat.Builder(context, channelId)
                       .setSmallIcon(R.drawable.ic_launcher_foreground)
                       .setContentTitle("Пора перекусить!")
                       .setContentText("Не забудьте съесть норму калорий!")
                       .setPriority(NotificationCompat.PRIORITY_HIGH)
                   notificationManager.notify(notificationId, builder.build())
               }
            }
            else{
                 if(user.active == "Высокий" && ((user.weight * 10 + user.height * 6.25 - user.years - 161) * 1.9) != user.todayKkl)
                 {
                     val builder = NotificationCompat.Builder(context, channelId)
                         .setSmallIcon(R.drawable.ic_launcher_foreground)
                         .setContentTitle("Пора перекусить!")
                         .setContentText("Не забудьте съесть норму калорий!")
                         .setPriority(NotificationCompat.PRIORITY_HIGH)
                     notificationManager.notify(notificationId, builder.build())
                 }
                 if (user.active == "Средний" && ((user.weight * 10 + user.height * 6.25 - user.years - 161) * 1.5) != user.todayKkl){
                     val builder = NotificationCompat.Builder(context, channelId)
                         .setSmallIcon(R.drawable.ic_launcher_foreground)
                         .setContentTitle("Пора перекусить!")
                         .setContentText("Не забудьте съесть норму калорий!")
                         .setPriority(NotificationCompat.PRIORITY_HIGH)
                     notificationManager.notify(notificationId, builder.build())
                 }
                 if (user.active == "Низкий" &&  ((user.weight * 10 + user.height * 6.25 - user.years - 161) * 1.2) != user.todayKkl){
                     val builder = NotificationCompat.Builder(context, channelId)
                         .setSmallIcon(R.drawable.ic_launcher_foreground)
                         .setContentTitle("Пора перекусить!")
                         .setContentText("Не забудьте съесть норму калорий!")
                         .setPriority(NotificationCompat.PRIORITY_HIGH)
                     notificationManager.notify(notificationId, builder.build())
                 }
            }
            if ((user.weight * 0.8) != user.todayBelki){
                val builderNutrients = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Пора перекусить!")
                    .setContentText("Не забудьте съесть норму белков!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                notificationManager.notify(notificationId + 1, builderNutrients.build())
            }
            if ((user.weight * 1.3) != user.todayZiri){
                val builderNutrients = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Пора перекусить!")
                    .setContentText("Не забудьте съесть норму жиров!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                notificationManager.notify(notificationId + 1, builderNutrients.build())
            }
            if ((user.weight * 4.0) != user.todayUglevodi){
                val builderNutrients = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Пора перекусить!")
                    .setContentText("Не забудьте съесть норму углеводов!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                notificationManager.notify(notificationId + 1, builderNutrients.build())
            }



        }
    }
}

