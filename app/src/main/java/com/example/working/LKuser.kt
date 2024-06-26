package com.example.working

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.db.williamchart.view.BarChartView
import com.example.working.databinding.ActivityLkuserBinding
import com.example.working.databinding.UserEditBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

private lateinit var binding: ActivityLkuserBinding
private lateinit var bindingg: UserEditBinding

class LKuser : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLkuserBinding.inflate(layoutInflater)

        setContentView(binding.root)

        fun setColoredText(textView: TextView, text: String, n: Int) {
            if (n > 0 && n <= text.length) {
                val highlightColor = Color.parseColor("#f0e891")
                val spannableString = SpannableString(text)
                spannableString.setSpan(BackgroundColorSpan(highlightColor), 0, n, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                textView.text = spannableString
            } else {
                textView.text = text
            }
        }

        val dao = MainDb.getDb(this).getDao()

        binding.imageViewBack.setOnClickListener{
            val intent = Intent(this, LK::class.java)
            startActivity(intent)
        }

        binding.buttonEdit.setOnClickListener {
            bindingg = UserEditBinding.inflate(layoutInflater)
            val mBuilder = AlertDialog.Builder(this)
                .setView(bindingg.root)
                .setTitle("Заполните поля данными")
            val mAlertDialog = mBuilder.show()
            mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffcc")))

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val user = dao.getFirstUser()
                    bindingg.titleText.setText(user.name)
                    bindingg.weightText.setText(user.weight.toString())
                    bindingg.heightText.setText(user.height.toString())
                    bindingg.yearsText.setText(user.years.toString())
                    if (user.gender == "М")
                        bindingg.checkBoxMale2.isChecked = true
                    if (user.gender == "Ж")
                        bindingg.checkBoxWomen2.isChecked = true

                }
            }

            bindingg.checkBoxMale2.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    bindingg.checkBoxWomen2.isChecked = false
                }
            }

            bindingg.checkBoxWomen2.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    bindingg.checkBoxMale2.isChecked = false
                }
            }

            val items = arrayOf("Высокий", "Средний", "Низкий")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Выберите значение")
            builder.setItems(items) { dialog, which ->
                val selectedValue = items[which]
                bindingg.buttonActive.text = selectedValue
                bindingg.buttonActive.setBackgroundColor(Color.parseColor("#45f235"))
            }

            val dialog = builder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffcc")))
            bindingg.buttonActive.setOnClickListener {
                dialog.show()
            }

            bindingg.btn1.setOnClickListener {
                val name = bindingg.titleText.text.toString().trim()
                val weight = bindingg.weightText.text.toString().trim()
                val height = bindingg.heightText.text.toString().trim()
                val years = bindingg.yearsText.text.toString().trim()
                val active = bindingg.buttonActive.text.toString().trim()
                val checkMale = bindingg.checkBoxMale2.isChecked
                val checkWomen = bindingg.checkBoxWomen2.isChecked
                if (name == "" || weight == "" || height == "" || years == "" || active == "Выберите ваш уровень активности" || weight == "." || height == "." || (checkMale == false && checkWomen == false)) {
                    Toast.makeText(this, "Не все поля были заполнены!", Toast.LENGTH_LONG).show()
                } else {
                    if (years.toInt() < 16) {
                        Toast.makeText(
                            this,
                            "Данное приложение можно использовать только лицам, достигшим 16 лет жизни!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (weight.toDouble() > 635 || weight.toDouble() < 2.3) {
                            Toast.makeText(
                                this,
                                "Максимально зафиксированный вес человека составляет 635 кг, а минимальный 2.3 кг!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            if (height.toDouble() > 251 || height.toDouble() < 63) {
                                Toast.makeText(
                                    this,
                                    "Максимально зафиксированный рост человека составляет 251 см, а минимальный 63 см!",
                                    Toast.LENGTH_LONG
                                ).show()}
                                else {
                                    if (checkMale == true){
                                        lifecycleScope.launch {
                                            withContext(Dispatchers.IO) {
                                                val user = dao.getFirstUser()
                                                user.name = name
                                                user.weight = weight.toDouble()
                                                user.height = height.toDouble()
                                                user.years = years.toInt()
                                                user.gender = "М"
                                                user.active = active
                                                dao.updateUser(user)

                                            }
                                        }
                                        recreate()
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
                                        mAlertDialog.dismiss()
                                    }
                                if (checkWomen == true){
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.IO) {
                                            val user = dao.getFirstUser()
                                            user.name = name
                                            user.weight = weight.toDouble()
                                            user.height = height.toDouble()
                                            user.years = years.toInt()
                                            user.gender = "Ж"
                                            user.active = active
                                            dao.updateUser(user)

                                        }
                                    }
                                    recreate()
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

                                    mAlertDialog.dismiss()
                                }
                            }
                        }
                    }
                }
            }
            bindingg.btn2.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

                        CoroutineScope(Dispatchers.IO).launch {
                            val user: Users = dao.getUser()
                            withContext(Dispatchers.Main) {

                                var res: String
                                if (user.gender == "М"){
                                    if (user.active == "Высокий") {
                                        res =
                                            ((user.weight * 10 + user.height * 6.25 - user.years + 5) * 1.9).toString()
                                        binding.textNormKkl.text ="Норма: ${res}"
                                    } else if (user.active == "Средний") {
                                        res =
                                            ((user.weight * 10 + user.height * 6.25 - user.years + 5) * 1.5).toString()
                                        binding.textNormKkl.text ="Норма: ${res}"
                                    } else if (user.active == "Низкий") {
                                        res =
                                            ((user.weight * 10 + user.height * 6.25 - user.years + 5) * 1.2).toString()
                                        binding.textNormKkl.text ="Норма: ${res}"
                                    }
                                }

                                if (user.gender == "Ж"){
                                    if (user.active == "Высокий") {
                                        res =
                                            ((user.weight * 10 + user.height * 6.25 - user.years - 161) * 1.9).toString()
                                        binding.textNormKkl.text ="Норма: ${res}"
                                    } else if (user.active == "Средний") {
                                        res =
                                            ((user.weight * 10 + user.height * 6.25 - user.years - 161) * 1.5).toString()
                                        binding.textNormKkl.text = "Норма: ${res}"
                                    } else if (user.active == "Низкий") {
                                        res =
                                            ((user.weight * 10 + user.height * 6.25 - user.years - 161) * 1.2).toString()
                                        binding.textNormKkl.text = "Норма: ${res}"
                                    }
                                }

                                val KgNormText = String.format("%.1f",user.weight / ((user.height * 0.01) * (user.height * 0.01)))
                                val NormBelki = String.format("%.1f", user.weight * 0.8)
                                val NormZiri = String.format("%.1f", user.weight * 1.3)
                                val NormUglevodi = String.format("%.1f", user.weight * 4.0)

                                binding.textNormBelki.text ="Норма: ${NormBelki}"
                                binding.textNormZiri.text ="Нормa: ${NormZiri}"
                                binding.textNormUgl.text ="Норма: ${NormUglevodi}"
                                binding.textNormKg.text ="ИМТ: ${KgNormText}"

                                val nameText = binding.nameTextLk
                                setColoredText(nameText, "Ваше имя: ${user.name}", 9)

                                val weightText = binding.weightTextLk
                                setColoredText(weightText, "Ваш вес (кг): ${user.weight}", 8)

                                val heightText = binding.heightTextLk
                                setColoredText(heightText, "Ваш рост (см): ${user.height}", 9)

                                val yearsText = binding.yearsTextLk
                                setColoredText(yearsText, "Ваш возраст: ${user.years}", 12)

                                val activeText = binding.activeTextLk
                                setColoredText(activeText, "Ваш уровень активности: ${user.active}", 23)

                                val belkiText = binding.belkiTextLk
                                setColoredText(belkiText, "Белков на сегодня(гр): ${String.format("%.1f", user.todayBelki)};", 29)

                                val ziriText = binding.ziriTextLk
                                setColoredText(ziriText, "Жиров на сегодня(гр): ${String.format("%.1f", user.todayZiri)};", 28)

                                val uglevodiText = binding.uglevodiTextLk
                                setColoredText(uglevodiText, "Углеводов на сегодня(гр): ${String.format("%.1f", user.todayUglevodi)};", 32)

                                val kklText = binding.kklTextLk
                                setColoredText(kklText, "Килокалорий на сегодня(гр): ${String.format("%.1f", user.todayKkl)};", 34)


                                val barChart = findViewById<BarChartView>(R.id.barChart)
                                val barSet = listOf(
                                    "Белки" to user.todayBelki.toFloat(),
                                    "Жиры" to user.todayZiri.toFloat(),
                                    "Углеводы" to user.todayUglevodi.toFloat()
                                )
                                barChart.animate(barSet)
                            }
                        }
        }
}