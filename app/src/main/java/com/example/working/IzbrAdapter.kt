package com.example.working
import android.content.Context
import android.graphics.Color
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
import com.example.working.databinding.VvodDatiKolvoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit


class IzbrAdapter(private val itemListIzbr: ArrayList<ItemsIzbr>, private val itemsDao: Dao, private val lifecycleOwner: LifecycleOwner, private val context: Context) : RecyclerView.Adapter<IzbrAdapter.ItemViewHolder>() {

    lateinit var binding: VvodDatiKolvoBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.izbr_in_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {


        val itemsIzbr = itemListIzbr[position]
        holder.bind(itemsIzbr.name, itemsIzbr.kkl.toString(), itemsIzbr.belki.toString(), itemsIzbr.ziri.toString(), itemsIzbr.uglevodi.toString())



        val buttonDobavIzbr : Button = holder.itemView.findViewById(R.id.button_dobavIzbrList)
        buttonDobavIzbr.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.vvod_dati_kolvo, null)
            val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker_Izbr)
            val weightEditText = dialogView.findViewById<EditText>(R.id.kolvo_Izbr)

            val today = Calendar.getInstance()
            datePicker.setMinDate(today.timeInMillis + 86400000)

            val builder = AlertDialog.Builder(context, R.style.CustomAlertDialogStyle)
            builder.setView(dialogView)
                .setPositiveButton("OK") { dialog, which ->
                    val day = datePicker.dayOfMonth
                    val month = datePicker.month
                    val year = datePicker.year
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, day)

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                    val dateString = dateFormat.format(calendar.time)

                    val formattedDate = calendar.timeInMillis
                    val kolvo = weightEditText.text.toString()

                    if (kolvo == "" || kolvo == "0" || kolvo == "." || kolvo == ".0" || kolvo == "0." || kolvo == "0.0")
                    {
                        Toast.makeText(context, "Заполните все поля данными", Toast.LENGTH_LONG).show()
                    }
                    else {
                        val items = Items(
                            null,
                            itemsIzbr.name,
                            dateString,
                            itemsIzbr.kkl,
                            itemsIzbr.belki,
                            itemsIzbr.ziri,
                            itemsIzbr.uglevodi,
                            kolvo.toDouble(),
                            formattedDate
                        )
                        Thread {
                            itemsDao.insertItem(items)
                        }.start()

                        val calendarR = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 12)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }

                        val currentTime = System.currentTimeMillis()

                        if (calendarR.timeInMillis > currentTime) {
                            val delay = calendarR.timeInMillis - currentTime
                            val notificationWorkRequest =
                                OneTimeWorkRequestBuilder<NotificationWorker>()
                                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                                    .build()
                            WorkManager.getInstance(context).enqueue(notificationWorkRequest)
                        }
                    }
                }
                .setNegativeButton("Отмена") { dialog, which ->
                    dialog.dismiss()
                }
            val alertDialog = builder.create()
            alertDialog.setOnShowListener {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
            }
            alertDialog.show()
        }

        val buttonDeleteIzbr : Button = holder.itemView.findViewById(R.id.button_deleteIzbrList)
        buttonDeleteIzbr.setOnClickListener {
            lifecycleOwner.lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    itemsDao.deleteItemIzbr(itemsIzbr)
                }
                val adapterPosition = holder.adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    itemListIzbr.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
            }
        }
    }
    override fun getItemCount(): Int {
        return itemListIzbr.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textName: TextView = itemView.findViewById(R.id.name_izbr)

        fun bind(
            title: String,
            kkl: String,
            belki: String,
            ziri: String,
            uglevodi: String
        ) {
            textName.text = title
        }
    }
}