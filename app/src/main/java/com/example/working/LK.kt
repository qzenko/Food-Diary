package com.example.working

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LK : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lk)

        val buttonO : Button = findViewById(R.id.buttonLK)
        val button1 : Button = findViewById(R.id.buttonSpisok)

        button1.setOnClickListener{
            val intent = Intent(this, Spisok::class.java)
            startActivity(intent)
        }
        buttonO.setOnClickListener {
            val intent = Intent(this, LKuser::class.java)
            startActivity(intent)
        }

        val textView: TextView = findViewById(R.id.textView19)

        val random = (1..19).random()

        val textArray = arrayOf(
            "Грецкий орех – единственный орех, содержащий большое количество Омега-3 жирных кислот.",
            "Морковь была фиолетовой до XVII века. Оранжевый цвет был выведен голландскими селекционерами.",
            "Шоколад в небольших количествах помогает улучшить настроение.",
            "Арахис – один из самых богатых источников белка среди орехов.",
            "Помидоры являются фруктами, а не овощами.",
            "Черный перец помогает улучшить усвоение питательных веществ из пищи.",
            "Бананы содержат природный антидепрессант – триптофан, который помогает улучшить настроение.",
            "Оливковое масло снижает риск сердечно-сосудистых заболеваний.",
            "Горький шоколад может помочь улучшить функцию мозга.",
            "Чай содержит антиоксиданты, которые могут помочь в борьбе с раком.",
            "Миндаль является одним из самых питательных орехов.",
            "Кофеин в кофе может повысить уровень энергии и улучшить физическую производительность.",
            "Яйца являются одним из самых питательных продуктов, содержащих множество важных питательных веществ.",
            "Капуста содержит вещество, которое может помочь в профилактике рака.",
            "Гранатовый сок богат антиоксидантами и полезен для сердца.",
            "Кукуруза является одним из самых распространенных зерновых культур в мире.",
            "Масло льна содержит Омега-3 жирные кислоты, полезные для здоровья сердца.",
            "Сыр – хороший источник кальция, необходимого для костей и зубов.",
            "Гранаты содержат вещество, которое может помочь снизить давление."
        )

        textView.text = textArray[random - 1]
    }

}