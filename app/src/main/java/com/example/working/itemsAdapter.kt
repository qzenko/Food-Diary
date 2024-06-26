package com.example.working

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit


class ItemAdapter(private var itemList: ArrayList<Items>, private val itemsDao: Dao, private val lifecycleOwner: LifecycleOwner, private val context: Context) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    fun updateList(newList: List<Items>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_in_list, parent, false)
        return ItemViewHolder(view)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = itemList[position]
        holder.bind(
            item.name,
            item.srok,
            item.kkl.toString(),
            item.belki.toString(),
            item.ziri.toString(),
            item.uglevodi.toString(),
            item.kolvo.toString(),
            item.podschet.toString()
        )

        holder.updateStatus(item.podschet)

        val buttonIzmen: Button = holder.itemView.findViewById(R.id.button_izmen)
        buttonIzmen.setOnClickListener {

            val dialogView = LayoutInflater.from(context).inflate(R.layout.vvod_dialog, null)
            val btn1 = dialogView.findViewById<Button>(R.id.btn1)
            val btn2 = dialogView.findViewById<Button>(R.id.btn2)
            btn1.text = "Изменить"
            val datePickerR = dialogView.findViewById<DatePicker>(R.id.datePicker)
            val titleEditText = dialogView.findViewById<EditText>(R.id.title_text)
            val kolvoEditText = dialogView.findViewById<EditText>(R.id.kolvo_text)
            val ziriEditText = dialogView.findViewById<EditText>(R.id.ziri_text)
            val belkiEditText = dialogView.findViewById<EditText>(R.id.belki_text)
            val uglevodiEditText = dialogView.findViewById<EditText>(R.id.uglevodi_text)
            val kklEditText = dialogView.findViewById<EditText>(R.id.kkl_text)

            val today = Calendar.getInstance()
            datePickerR.setMinDate(today.timeInMillis + 86400000)

            val mBuilder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setTitle("Заполните поля данными")
            val mAlertDialog = mBuilder.show()
            mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffcc")))

            titleEditText.setText(item.name)
            val calendar = Calendar.getInstance().apply {
                timeInMillis = item.podschet
            }
            datePickerR.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            kolvoEditText.setText(item.kolvo.toString())
            ziriEditText.setText(item.ziri.toString())
            belkiEditText.setText(item.belki.toString())
            uglevodiEditText.setText(item.uglevodi.toString())
            kklEditText.setText(item.kkl.toString())

            btn1.setOnClickListener {
                val title = dialogView.findViewById<EditText>(R.id.title_text).text.toString()
                val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)
                val day = datePicker.dayOfMonth
                val month = datePicker.month
                val year = datePicker.year
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)

                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                val dateString = dateFormat.format(calendar.time)
                val formattedDate = calendar.timeInMillis

                val kolvo = dialogView.findViewById<EditText>(R.id.kolvo_text).text.toString()
                val ziri = dialogView.findViewById<EditText>(R.id.ziri_text).text.toString()
                val belki = dialogView.findViewById<EditText>(R.id.belki_text).text.toString()
                val uglevodi = dialogView.findViewById<EditText>(R.id.uglevodi_text).text.toString()
                val kkl = dialogView.findViewById<EditText>(R.id.kkl_text).text.toString()

                if (title == "" || kolvo == "" || ziri == "" || belki == "" || uglevodi == "" || kkl == "0" || kolvo == "." || ziri == "." || belki == "." || uglevodi == "." || kkl == "." || kolvo == "0." || kolvo == ".0" || kolvo == "0.0") {
                    Toast.makeText(context, "Заполните все поля данными!", Toast.LENGTH_LONG).show()
                } else {
                    lifecycleOwner.lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            item.name = title
                            item.srok = dateString
                            item.kkl = kkl.toDouble()
                            item.belki = belki.toDouble()
                            item.ziri = ziri.toDouble()
                            item.uglevodi = uglevodi.toDouble()
                            item.kolvo = kolvo.toDouble()
                            item.podschet = formattedDate

                            itemsDao.updateItem(item)
                        }

                        Toast.makeText(context, "Продукт $title был успешно изменен!", Toast.LENGTH_LONG).show()

                        val updatedItems = withContext(Dispatchers.IO) {
                            itemsDao.getItem()
                        }

                        itemList.clear()
                        itemList.addAll(updatedItems)
                        notifyDataSetChanged()
                    }
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 12)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    val currentTime = System.currentTimeMillis()

                    if (calendar.timeInMillis > currentTime) {
                        val delay = calendar.timeInMillis - currentTime
                        val notificationWorkRequest =
                            OneTimeWorkRequestBuilder<NotificationWorker>()
                                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                                .build()
                        WorkManager.getInstance(context).enqueue(notificationWorkRequest)
                    }
                    mAlertDialog.dismiss()
                }
            }
        btn2.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

        val buttonIzbr : Button = holder.itemView.findViewById(R.id.button_izbr)
        buttonIzbr.setOnClickListener {
            val izbr = ItemsIzbr(
                null,
                item.name,
                item.kkl,
                item.belki,
                item.ziri,
                item.uglevodi
                )
            Thread {
                itemsDao.insertIzbr(izbr)
            }.start()
            Toast.makeText(context, "Продукт ${item.name} добавлен в избранное!", Toast.LENGTH_LONG).show()
        }



        val buttonEat : Button = holder.itemView.findViewById(R.id.button_eat)

        buttonEat.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context, R.style.CustomAlertDialogStyle)
            builder.setTitle("Введите количество (грамм). Доступно: ${item.kolvo}")
            val input = EditText(holder.itemView.context)
            input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            builder.setView(input)



            builder.setPositiveButton("OK") { dialog, which ->
                val amount = input.text.toString().toDouble()
                val todayKkl = item.kkl / 100 * amount
                val todayBelki = item.belki / 100 * amount
                val todayZiri = item.ziri / 100 * amount
                val todayUglevodi = item.uglevodi / 100 * amount

                if (input.text.toString() == "" || input.text.toString() == "0"|| input.text.toString() == "." || input.text.toString() == "0." || input.text.toString() == ".0" || input.text.toString() == "0.0")
                {
                    Toast.makeText(context, "Введите нормальное значение!", Toast.LENGTH_LONG).show()
                }
                else {
                    lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        val user = itemsDao.getFirstUser()
                        if (item.kolvo < amount) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "У вас столько сейчас нет!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            user.todayKkl += todayKkl
                            user.todayBelki += todayBelki
                            user.todayZiri += todayZiri
                            user.todayUglevodi += todayUglevodi
                            item.kolvo -= amount
                            if (item.kolvo == 0.0) {
                                itemsDao.updateUser(user)
                                itemsDao.deleteItem(item)
                                itemList.removeAt(position)
                                withContext(Dispatchers.Main) {
                                    notifyItemRemoved(position)
                                }
                            } else if (item.kolvo > 0) {
                                itemsDao.updateUser(user)
                                itemsDao.updateItem(item)
                                val updatedItems = withContext(Dispatchers.IO) {
                                    itemsDao.getItem()
                                }
                                (context as? Activity)?.runOnUiThread {
                                    itemList.clear()
                                    itemList.addAll(updatedItems)
                                    notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            }


            builder.setNegativeButton("Отмена") { dialog, which -> dialog.cancel() }

            val alertDialog = builder.create()
            alertDialog.setOnShowListener {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
            }
            alertDialog.show()
        }

        val buttonDel: Button = holder.itemView.findViewById(R.id.button_delete)

        buttonDel.setOnClickListener {
            lifecycleOwner.lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    itemsDao.deleteItem(item)
                }
                val adapterPosition = holder.adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    itemList.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textName: TextView = itemView.findViewById(R.id.text_nameUser)
        private val textData: TextView = itemView.findViewById(R.id.text_srok)
        private val textKkl: TextView = itemView.findViewById(R.id.text_kkl)
        private val textBelki: TextView = itemView.findViewById(R.id.text_belki)
        private val textZiri: TextView = itemView.findViewById(R.id.text_ziri)
        private val textUglevodi: TextView = itemView.findViewById(R.id.text_uglevodi)
        private val textKolvo: TextView = itemView.findViewById(R.id.text_kolvo)
        private val status: TextView = itemView.findViewById(R.id.statusItem)

        fun bind(title: String, date: String, kkl: String, belki: String, ziri: String, uglevodi: String, kolvo: String, podschet: String) {
            textName.text = title
            textData.text = date
            textKkl.text = kkl
            textBelki.text = belki
            textZiri.text = ziri
            textUglevodi.text = uglevodi
            textKolvo.text = kolvo

        }
        fun updateStatus(podschet: Long) {
            val status: TextView = itemView.findViewById(R.id.statusItem)
            val btn: Button = itemView.findViewById(R.id.button_eat)


            val today = Calendar.getInstance()
            val posletomorrow = Calendar.getInstance()
            posletomorrow.add(Calendar.DAY_OF_YEAR, 2)

            if (podschet <= today.timeInMillis) {
                status.text = "Испортился"
                status.setBackgroundColor(Color.parseColor("#FF0000"))
                btn.visibility = View.GONE

            } else if (podschet <= posletomorrow.timeInMillis) {
                status.text = "Скоро испортится"
                status.setBackgroundColor(Color.parseColor("#FFFF00"))
            }  else {
                status.text = "Свежий"
                status.setBackgroundColor(Color.parseColor("#00e600"))
            }
        }
    }
}