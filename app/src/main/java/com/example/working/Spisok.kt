package com.example.working

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.working.databinding.ActivitySpisokBinding
import com.example.working.databinding.ListIzbrBinding
import com.example.working.databinding.VvodDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

private lateinit var bindinggg: ActivitySpisokBinding
private lateinit var binding: VvodDialogBinding
private lateinit var bindingg: ListIzbrBinding

class Spisok : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private val itemList: ArrayList<Items> = ArrayList()
    private lateinit var editTextSearch: EditText
    private lateinit var buttonSearch: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindinggg =  ActivitySpisokBinding.inflate(layoutInflater)
        setContentView(bindinggg.root)
        val db = MainDb.getDb(this)
        val itemsDao = db.getDao()

        recyclerView = findViewById(R.id.itemsList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ItemAdapter(itemList, itemsDao, this, this)
        recyclerView.adapter = adapter

        editTextSearch = findViewById(R.id.search_Spisok) // Инициализация здесь
        buttonSearch = findViewById(R.id.button_Search) // Инициализация здесь

        buttonSearch.setOnClickListener {
            val query = editTextSearch.text.toString().lowercase(Locale.ROOT)
            if (query.isNotEmpty()) {
                val filteredList = itemList.filter { it.name.lowercase(Locale.getDefault()).contains(query)
                        || it.name.lowercase(Locale.ROOT).contains(query.substring(0, query.length - 2)) }
                adapter.updateList(filteredList)
            } else {
                Toast.makeText(this, "Заполните поле поиска!", Toast.LENGTH_LONG).show()
            }
        }

        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) {
                itemsDao.getItem()
            }
            // Добавление данных в itemList
            itemList.addAll(items)

            // Обновление RecyclerView
            adapter.notifyDataSetChanged()
        }

        bindinggg.imageViewBack.setOnClickListener{
            val intent = Intent(this, LK::class.java)
            startActivity(intent)
        }

        bindinggg.buttonSbros.setOnClickListener{
            lifecycleScope.launch {
                val items = withContext(Dispatchers.IO) {
                    itemsDao.getItem()
                }
                itemList.clear()
                itemList.addAll(items)

                // Обновление RecyclerView
                adapter.notifyDataSetChanged()
            }
        }


        bindinggg.buttonDobavIzbr.setOnClickListener {
            lateinit var recyclerViewW: RecyclerView
            lateinit var adapterR: IzbrAdapter
            val itemListIzbrr: ArrayList<ItemsIzbr> = ArrayList()
            bindingg = ListIzbrBinding.inflate(layoutInflater)
            val mBuilder = AlertDialog.Builder(this)
                .setView(bindingg.root)
                .setTitle("Ваш список избранных продуктов")
            val mAlertDialog = mBuilder.show()
            mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffcc")))
            recyclerViewW = bindingg.itemsListIzbr
            recyclerViewW.layoutManager = LinearLayoutManager(this)
            adapterR = IzbrAdapter(itemListIzbrr, itemsDao, this, this) // Pass the lifecycleOwner parameter
            recyclerViewW.adapter = adapterR

            lifecycleScope.launch {
                val itemsIzbr = withContext(Dispatchers.IO) {
                    itemsDao.getItemIzbr()
                }
                itemListIzbrr.addAll(itemsIzbr)

                adapterR.notifyDataSetChanged()
            }


            bindingg.buttonIzbrNazad.setOnClickListener {
                lifecycleScope.launch {
                    val items = withContext(Dispatchers.IO) {
                        itemsDao.getItem()
                    }
                    itemList.clear()

                    itemList.addAll(items)

                    adapter.notifyDataSetChanged()

                }
                mAlertDialog.dismiss()
            }
        }

        bindinggg.buttonDobav.setOnClickListener {
            binding = VvodDialogBinding.inflate(layoutInflater)
            val mBuilder = AlertDialog.Builder(this)
                .setView(binding.root)
                .setTitle("Заполните поля данными")
            val mAlertDialog = mBuilder.show()
            mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffcc")))


            val today = Calendar.getInstance()
            binding.datePicker.setMinDate(today.timeInMillis + 86400000)

            binding.btn1.setOnClickListener {
                val title = binding.titleText.text.toString()
                val day = binding.datePicker.dayOfMonth
                val month = binding.datePicker.month
                val year = binding.datePicker.year
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)

                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                val dateString = dateFormat.format(calendar.time)

                val formattedDate = calendar.timeInMillis
                val userKkl = binding.kklText.text.toString()
                val userBelki = binding.belkiText.text.toString()
                val userZiri = binding.ziriText.text.toString()
                val userUglevodi = binding.uglevodiText.text.toString()
                val userKolvo = binding.kolvoText.text.toString()
                if(title == "" || userBelki == "" || userKkl == "" || userUglevodi == "" || userZiri == "" || userBelki == "." || userKkl == "." || userUglevodi == "." || userZiri == "." || userKolvo == "" || userKolvo == "." || userKolvo == "0" || userKolvo == "0.0" || userKolvo == "0." || userKolvo == ".0")  {
                    Toast.makeText(this, "Не все поля заполнены!", Toast.LENGTH_LONG).show()
                }
                else{
                    val items = Items(null,
                        title,
                        dateString,
                        userKkl.toDouble(),
                        userBelki.toDouble(),
                        userZiri.toDouble(),
                        userUglevodi.toDouble(),
                        userKolvo.toDouble(),
                        formattedDate
                    )
                    Thread{
                        db.getDao().insertItem(items)
                    }.start()

                    Toast.makeText(this,"Продукт $title! был успешно добавлен", Toast.LENGTH_LONG).show()

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
                        WorkManager.getInstance(this).enqueue(notificationWorkRequest)
                    }
                    lifecycleScope.launch {
                        val items = withContext(Dispatchers.IO) {
                            itemsDao.getItem()
                        }
                        itemList.clear()

                        itemList.addAll(items)

                        adapter.notifyDataSetChanged()

                    }
                    mAlertDialog.dismiss()
                }
            }
            binding.btn2.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }
    }
}
