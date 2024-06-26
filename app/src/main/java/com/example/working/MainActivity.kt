package com.example.working

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.working.databinding.ActivityMainBinding
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("splash_screen_prefernce", MODE_PRIVATE)
        if (prefs.getBoolean("bypass_boolean", false)) {
            val intent = Intent(this, LK::class.java)
            startActivity(intent)
            finish()
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = MainDb.getDb (this)

        val button : Button = findViewById(R.id.vhod_button)

        val items = arrayOf("Высокий", "Средний", "Низкий")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Выберите значение")
        builder.setItems(items) { dialog, which ->
            val selectedValue = items[which]
            binding.buttonActive.text = selectedValue
            binding.buttonActive.setBackgroundColor(Color.parseColor("#45f235"))
        }
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffcc")))
            binding.buttonActive.setOnClickListener {
                dialog.show()
            }

        binding.checkBoxMale.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.checkBoxWomen.isChecked = false
            }
        }

        binding.checkBoxWomen.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.checkBoxMale.isChecked = false
            }
        }

        button.setOnClickListener{
            val name =  binding.userName.text.toString().trim()
            val proverka =  binding.userWeightt.text.toString().trim()
            val proverk = binding.userHeight.text.toString().trim()
            val old = binding.userYears.text.toString().trim()

            if(name == "" || proverka == "" || proverk== "" || proverka == "." || proverk== "." ||binding.buttonActive.text == "Выберите ваш уровень активности" || old == "" || (binding.checkBoxMale.isChecked == false && binding.checkBoxWomen.isChecked == false)) {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            }
            else {
                if (old.toInt() < 16) {
                    Toast.makeText(this, "Данное приложение можно использовать только лицам, достигшим 16 лет жизни!", Toast.LENGTH_LONG).show()
                } else {
                    if (proverka.toDouble() > 635 || proverka.toDouble() < 2.3) {
                        Toast.makeText(
                            this,
                            "Максимально зафиксированный вес человека составляет 635 кг, а минимальный 2.3 кг! Если вы весите больше/меньше, отпишите нам на почту ofihegeniyhavchik12@mail.ru!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (proverk.toDouble() > 251 || proverk.toDouble() < 63) {
                            Toast.makeText(
                                this,
                                "Максимально зафиксированный рост человека составляет 251 см, а минимальный 63 см! Если вы выше/ниже, отпишите нам на почту ofihegeniyhavchik12@mail.ru!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else {
                            if (binding.checkBoxMale.isChecked == true){
                                val users = Users(
                                    null,
                                    binding.userName.text.toString(),
                                    binding.userWeightt.text.toString().toDouble(),
                                    binding.userHeight.text.toString().toDouble(),
                                    binding.userYears.text.toString().toInt(),
                                    "М",
                                    binding.buttonActive.text.toString(),
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.0
                                )
                                Thread {
                                    db.getDao().insertUser(users)
                                }.start()
                                val editor =
                                    getSharedPreferences("splash_screen_prefernce", MODE_PRIVATE).edit()
                                editor.putBoolean("bypass_boolean", true)
                                editor.apply()
                                Toast.makeText(this, "Успешная регистрация, $name!", Toast.LENGTH_LONG)
                                    .show()

                                //чтобы у юзера каждый день статы обнулялись
                                val resetIntent = Intent(this, ResetValuesReceiver::class.java)
                                val resetPendingIntent = PendingIntent.getBroadcast(this, 0, resetIntent,
                                    PendingIntent.FLAG_MUTABLE)

                                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                val calendar = Calendar.getInstance().apply {
                                    timeInMillis = System.currentTimeMillis()
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                }

                                alarmManager.setRepeating(
                                    AlarmManager.RTC_WAKEUP,
                                    calendar.timeInMillis,
                                    AlarmManager.INTERVAL_DAY,
                                    resetPendingIntent
                                )
                                val intent = Intent(this, LK::class.java)
                                this.finishAffinity()
                                startActivity(intent)
                            }
                            if (binding.checkBoxWomen.isChecked == true){
                                val users = Users(
                                    null,
                                    binding.userName.text.toString(),
                                    binding.userWeightt.text.toString().toDouble(),
                                    binding.userHeight.text.toString().toDouble(),
                                    binding.userYears.text.toString().toInt(),
                                    "Ж",
                                    binding.buttonActive.text.toString(),
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.0
                                )
                                Thread {
                                    db.getDao().insertUser(users)
                                }.start()
                                val editor =
                                    getSharedPreferences("splash_screen_prefernce", MODE_PRIVATE).edit()
                                editor.putBoolean("bypass_boolean", true)
                                editor.apply()
                                Toast.makeText(this, "Успешная регистрация, $name!", Toast.LENGTH_LONG)
                                    .show()

                                //чтобы у юзера каждый день статы обнулялись
                                val resetIntent = Intent(this, ResetValuesReceiver::class.java)
                                val resetPendingIntent = PendingIntent.getBroadcast(this, 0, resetIntent,
                                    PendingIntent.FLAG_MUTABLE)

                                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                val calendar = Calendar.getInstance().apply {
                                    timeInMillis = System.currentTimeMillis()
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                }

                                alarmManager.setRepeating(
                                    AlarmManager.RTC_WAKEUP,
                                    calendar.timeInMillis,
                                    AlarmManager.INTERVAL_DAY,
                                    resetPendingIntent
                                )

                                val alarmManageR = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                val alarmIntenT = Intent(this, AlarmReceiver::class.java)
                                val pendingIntenT = PendingIntent.getBroadcast(
                                    this,
                                    0,
                                    alarmIntenT,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                                )
                                val calendaR = Calendar.getInstance()
                                calendaR.set(Calendar.HOUR_OF_DAY, 18)
                                calendaR.set(Calendar.MINUTE, 0)
                                calendaR.set(Calendar.SECOND, 0)

                                alarmManageR.setRepeating(AlarmManager.RTC_WAKEUP, calendaR.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntenT)

                                val intent = Intent(this, LK::class.java)
                                this.finishAffinity()
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }
}
