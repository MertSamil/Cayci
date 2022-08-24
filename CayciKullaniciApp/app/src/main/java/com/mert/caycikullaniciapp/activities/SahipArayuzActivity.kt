package com.mert.caycikullaniciapp.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mert.caycikullaniciapp.R
import com.mert.caycikullaniciapp.adapters.ArayuzRecyclerAdapter
import com.mert.caycikullaniciapp.classes.Kullanici
import kotlinx.android.synthetic.main.activity_arayuz.*


class SahipArayuzActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore

    var kullaniciListesi = ArrayList<Kullanici>()
    var liste = ArrayList<String>()

    var context = this

    lateinit var sharedPreferences: SharedPreferences

    private lateinit var recyclerViewAdapter : ArayuzRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sahip_arayuz)

        sharedPreferences = this.getSharedPreferences("com.mert.caycikullaniciapp.activities", MODE_PRIVATE)

        sharedPreferences.edit().putInt("auth",1).apply()

        auth = FirebaseAuth.getInstance()

        database = FirebaseFirestore.getInstance()


        verileriAl()

            var layoutManager = LinearLayoutManager(this)
            //Recycler Row u kullanabilmek için bir layout manager oluşturduk
            //alt alta gösterilsin diye linear kullandık

            recyclerView.layoutManager = layoutManager

            //Yukarıda bir recyclerViewAdapter oluşturduk
            recyclerViewAdapter = ArayuzRecyclerAdapter(kullaniciListesi,context)
            //Artık her şeyi birbirine bağladık

            recyclerView.adapter = recyclerViewAdapter
            //Bu adaptörün recycler viewde kullanılacağını söyledik



    }

    fun bildirim()
    {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, SahipArayuzActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.drawable.siyah_cay)
            .setContentTitle("New Order")
            .setContentText("Yeni Bir Sipariş Mevcut!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            //.setWhen(System.currentTimeMillis())
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        builder.setSound(uri)

        createNotificationChannel()

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "New Order"
            val descriptionText = "Order"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun verileriAl()
    {
        //database deki tüm verilere ihtiyacım var Yoksa yolları farklı Dökümantasyonu oku
        database.collection("KullaniciSiparisi").orderBy("date",
            Query.Direction.ASCENDING).addSnapshotListener { snapshot, exception ->
            //ordeyby = sırala  descending = düşen
            //postların tarihe göre sıralanması için yaptık
            //Snapshot listener ile gerçek zamanlı bir okuma yapabiliriz
            if(exception != null)
            {
                println("ilk hata")
                //hata var demektir
                Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
            //000-cayocagi@kbb.cay
            //kbbcayocagi
            else
            {
                try {
                    if(snapshot != null)
                    {

                        if(snapshot.isEmpty == false)
                        {

                            //! değilse demektir

                            val documents = snapshot.documents
                            //dökümanları alıyoruz (liste şeklinde)

                            //sharedPreferences.edit().putInt("size",kullaniciListesi.size).apply()

                            kullaniciListesi.clear()
                            //listede önceden kalan bir şey varsa döngüye girmeden temizlenmesi gerekiyor

                            for (document in documents)
                            {
                                var userEmail = document.get("useremail") as String
                                var orderDate = document.getTimestamp("date")?.toDate()
                                var userOrder = document.get("userorder") as ArrayList<String>
                                var id = document.id
                                //println(orderDate)

                                //var userOrder = document.get("userorder") as ArrayList<String>

                                //000-cayocagi@kbb.cay

                                var email = Kullanici(userEmail,orderDate.toString(),userOrder,id)
                                kullaniciListesi.add(email)

                            }

                            var size = sharedPreferences.getInt("size",0)

                            println(size)

                            if(size < kullaniciListesi.size)
                            {
                                bildirim()
                            }

                            recyclerViewAdapter.notifyDataSetChanged()

                            //Yeni veri geldi kendini yenile dememiz gerekiyor

                            sharedPreferences.edit().putInt("size",kullaniciListesi.size).apply()

                        }
                    }
                }catch (e:Exception)
                {
                    println("2. hata " + e)
                }
                //gelen veriler snapshot ın içinde gelecek Kontrollerimizi yapalım

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //res de menu klasörü oluşturduk
        //içine option menu koyduk
        //xml üzerinden düzenlemeleri yaptık
        //Şimdi de menüyü bağlamak için bu fonksiyonu override ediyoruz

        val menuInflater = menuInflater
        //Inflater : XML leri kodla bağlamamızı sağlayan yapı

        menuInflater.inflate(com.mert.caycikullaniciapp.R.menu.menu,menu)
        //Oluşturduğumuz xml i inflate ettik

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Yukarıdaki fonksiyonu override ettik
        //Şimdi de neyin seçildiğini anlamak için bu fonksiyonu override edeceğiz

        if (item.itemId == com.mert.caycikullaniciapp.R.id.outButton)
        {
            auth.signOut()
            sharedPreferences.edit().putInt("auth",5).apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}