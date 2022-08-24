package com.mert.caycikullaniciapp.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mert.caycikullaniciapp.adapters.CardAdapter
import com.mert.caycikullaniciapp.R
import com.mert.caycikullaniciapp.classes.Urün
import com.mert.caycikullaniciapp.classes.urunList
import com.mert.caycikullaniciapp.databinding.ActivityArayuzBinding

class ArayuzActivity : AppCompatActivity()
{

    private lateinit var binding: ActivityArayuzBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore

    var report = 0

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArayuzBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = this.getSharedPreferences("com.mert.caycikullaniciapp.activities", MODE_PRIVATE)

        sharedPreferences.edit().putInt("auth",0).apply()

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        database = FirebaseFirestore.getInstance()



        //       val intent = intent

        //      var authorization = intent.getStringExtra("authorization")
        //Toast.makeText(applicationContext,authorization,Toast.LENGTH_LONG).show()

//        println("YETKİ : "+authorization)

        /*if (authorization != null) {
            if(authorization.toInt() == 1){
                val intent = Intent(this, SahipArayuzActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        else {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
         */




        //setContentView(R.layout.activity_arayuz)

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(applicationContext,3)
            adapter = CardAdapter(urunList)
            //Toast.makeText(applicationContext, urunList.size.toString(), Toast.LENGTH_LONG).show()
        }

        urunEkle()


        /////Sıkıntı çıkarsa recyclerAdapter kullan
    }

    fun urunEkle() {

        try {
            database.collection("Urünler").orderBy("id", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, exception ->

                    if (exception != null) {
                        //hata var demektir
                        Toast.makeText(
                            applicationContext,
                            exception.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (snapshot != null) {
                            if (snapshot.isEmpty == false) {
                                val documents = snapshot.documents

                                urunList.clear()

                                for (document in documents) {
                                    try {
                                        val gorsel = document.get("gorsel") as Long
                                        //type casting yaptık yani tip değişimi denebilir
                                        //var id = document.get("id") as Int
                                        val isim = document.get("isim") as String

                                        val alinanVeri = Urün(gorsel, isim, 0)
                                        //Sınıf kullanarak her alınan veriyi tek seferde hallettik
                                        urunList.add(alinanVeri)
                                        // println("yüklüyorum" + urunList.size)
                                    } catch (e: Exception) {
                                        Toast.makeText(applicationContext,e.localizedMessage, Toast.LENGTH_LONG).show()
                                    }

                                }
                                try {
                                    binding.recyclerView.adapter?.notifyDataSetChanged()
                                }catch (e : Exception)
                                {
                                    Toast.makeText(applicationContext,e.localizedMessage, Toast.LENGTH_LONG).show()
                                }

                            }
                        }
                    }
                }
            //Toast.makeText(applicationContext,"urunleri yukledim", Toast.LENGTH_LONG).show()
            //println("Yüklemeyi tamamladım : " + urunList.size)
        } catch (e: Exception) {
            Toast.makeText(applicationContext,e.localizedMessage, Toast.LENGTH_LONG).show()
            //println(e.localizedMessage)
        }
    }

    /*fun verileriAl()
    {
        //database deki tüm verilere ihtiyacım var Yoksa yolları farklı Dökümantasyonu oku
        database.collection("Urünler").orderBy("id",Query.Direction.ASCENDING).addSnapshotListener { snapshot, exception ->
            //ordeyby = sırala  descending = düşen  ascending = artan
            //ürünlerin idye göre sıralanması için yaptık
            //Snapshot listener ile gerçek zamanlı bir okuma yapabiliriz
            if(exception != null)
            {
                //hata var demektir
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
            else
            {
                //gelen veriler snapshot ın içinde gelecek Kontrollerimizi yapalım
                if(snapshot != null)
                {
                    //println("aaa")
                    if(snapshot.isEmpty == false)
                    {
                        //println("bbb")

                        //! değilse demektir

                        val documents = snapshot.documents
                        //dökümanları alıyoruz (liste şeklinde)

                        postList.clear()
                        //listede önceden kalan bir şey varsa döngüye girmeden temizlenmesi gerekiyor

                        for (document in documents)
                        {
                            val userEmail = document.get("useremail") as String
                            //type casting yaptık yani tip değişimi denebilir
                            val userComment = document.get("usercomment") as String
                            val imageUrl = document.get("imageurl") as String

                            val downloadPost = Post(userEmail,userComment,imageUrl)
                            //Sınıf kullanarak her alınan veriyi tek seferde hallettik
                            postList.add(downloadPost)
                        }

                        recyclerViewAdapter.notifyDataSetChanged()
                        //Yeni veri geldi kendini yenile dememiz gerekiyor

                    }
                }
            }
        }
    }



     */

    fun silme(view : View)
    {
        sifirlama()

    }

    fun gonder(view: View)
    {
        var alert = AlertDialog.Builder(this)
        alert.setTitle("Sipariş Tamamla")
        alert.setMessage("Siparişi tamamlamak istediğinize emin misiniz ?")
        alert.setPositiveButton("Evet", DialogInterface.OnClickListener { dialogInterface, i ->
            try {

                var uzunluk = 0

                val currentUserEmail = auth.currentUser!!.email.toString()
                //giriş yaptıysa zaten kullanıcı olması lazım o yüzden (!!)

                val userOrder = mutableListOf<String>()

                while (uzunluk < urunList.size)
                {
                    if(urunList[uzunluk].adet!=0)
                    {
                        userOrder.add("${urunList[uzunluk].adet} adet ${urunList[uzunluk].isim}")

                    }
                    uzunluk++
                }


                if(userOrder.size!=0)
                {
                    report = 0
                    val date = Timestamp.now()

                    //println(date.toDate())

                    //VERİTABANI İŞLEMLERİ
                    val siparisHashMap = hashMapOf<String,Any>()
                    //Veritabanındaki tarz bu şekilde olduğu için bu şekilde bir hashmap oluşturduk

                    siparisHashMap.put("useremail",currentUserEmail)
                    siparisHashMap.put("userorder",userOrder)
                    siparisHashMap.put("date",date)
                    //hashmap e eklemeleri yaptık

                    database.collection("KullaniciSiparisi").add(siparisHashMap).addOnCompleteListener { task->
                        //Post adında bir koleksiyon oluşturduk
                        if(task.isSuccessful)
                        {
                            sifirlama()
                            Toast.makeText(this,"Sipariş tamamlandı!", Toast.LENGTH_LONG).show()
                            //başarılı olduysa bitir Diğeri zaten açıktı
                        }
                    }.addOnFailureListener { exception->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }
                else
                {
                    report++
                    if(report == 1)
                    {
                        Toast.makeText(this,"Herhangi bir şey seçmediniz!", Toast.LENGTH_LONG).show()
                    }
                    else
                    {
                        Toast.makeText(this,"Lütfen bir şey seçin!!!", Toast.LENGTH_LONG).show()
                    }
                }



            }
            catch (e : Exception)
            {
                println("hata : " + e)
                Toast.makeText(applicationContext,e.localizedMessage,Toast.LENGTH_LONG).show()
            }

        })
        alert.setNegativeButton("Hayır", DialogInterface.OnClickListener { dialogInterface, i ->
            Toast.makeText(this,"Sipariş iptal edildi!", Toast.LENGTH_LONG).show()
        })

        alert.setIcon(R.drawable.siyah_cay)
        alert.setCancelable(false)
        alert.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //res de menu klasörü oluşturduk
        //içine option menu koyduk
        //xml üzerinden düzenlemeleri yaptık
        //Şimdi de menüyü bağlamak için bu fonksiyonu override ediyoruz

        val menuInflater = menuInflater
        //Inflater : XML leri kodla bağlamamızı sağlayan yapı

        menuInflater.inflate(R.menu.menu,menu)
        //Oluşturduğumuz xml i inflate ettik

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Yukarıdaki fonksiyonu override ettik
        //Şimdi de neyin seçildiğini anlamak için bu fonksiyonu override edeceğiz

        if (item.itemId == R.id.outButton)
        {
            auth.signOut()
            //sharedPreferences.edit().putInt("auth",5).apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun sifirlama()
    {
        var uzunluk = 0

        //println(urunList.size)


        while (uzunluk < urunList.size)
        {
            //println("List=${urunList.size}  + Uzun=${uzunluk}")
            //println(" : " + ürünList.size)

            urunList[uzunluk].adet = 0

            //println("Size:"+recyclerView.size)
            //text3.text = ürünList[uzunluk].adet.toString()
            //binding.recyclerView.get(uzunluk).text3.text
            //println("ss : " + binding.recyclerView.size)
            /*try {
                binding.recyclerView[uzunluk].text3.text = urunList[uzunluk].adet.toString()
            }
            catch (e : Exception)
            {
                println("hata : " + e)
            }
            */

            uzunluk++


        }

        val intent = Intent(this, ArayuzActivity::class.java)
        // intent.putExtra("authorization","0")
        startActivity(intent)
        finish()


    }

}