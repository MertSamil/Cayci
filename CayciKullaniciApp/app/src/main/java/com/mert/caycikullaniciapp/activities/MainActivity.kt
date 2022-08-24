package com.mert.caycikullaniciapp.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mert.caycikullaniciapp.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    //sonradan kullanılmak üzere tanımladık

    private lateinit var database : FirebaseFirestore

    var authorization = "0"

    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //println(R.drawable.soda)

        //Her ürün ekleneceğinde kullanılan fonskiyon
        auth = FirebaseAuth.getInstance()
        //singleton gibi görev yapıyor
        //singleton : tek nesne oluşacak

        val currentUser = auth.currentUser
        //Güncel kullanıcı

        database = FirebaseFirestore.getInstance()

        sharedPreferences = this.getSharedPreferences("com.mert.caycikullaniciapp.activities", MODE_PRIVATE)

        var yetki = sharedPreferences.getInt("auth",5)

        if(yetki == 0)
        {
            if(currentUser != null)
            {
                //com.google.firebase.auth.internal.zzx@7483cbc
                val intent = Intent(this, ArayuzActivity::class.java)
                //Toast.makeText(applicationContext,authorization,Toast.LENGTH_LONG).show()
                startActivity(intent)
                finish()
            }
        }
        else if(yetki == 1)
        {
            //com.google.firebase.auth.internal.zzx@7483cbc
            val intent = Intent(this, SahipArayuzActivity::class.java)
            //Toast.makeText(applicationContext,authorization,Toast.LENGTH_LONG).show()
            startActivity(intent)
            finish()
        }



        //println("Current = " + currentUser)

        //Eğer kullanıcı daha önce giriş yaptıysa her girişte logine yönlendirilmesin


    }


    fun kayitOl(view: View)
    {
        if(emailText.text != null && passwordText.text != null)
        {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                //asenkron çalışıyor (internetle çalıştığımız için bloklamamak için)
                //buraya girdiyse cevap gelmiş demektir

                if(task.isSuccessful)
                {
                    val kullaniciHashMap = hashMapOf<String,Any>()

                    kullaniciHashMap.put("id",0)
                    kullaniciHashMap.put("yetki","0")
                    kullaniciHashMap.put("email",email)
                    kullaniciHashMap.put("sifre",password)

                    database.collection("Kullanicilar").add(kullaniciHashMap).addOnCompleteListener { task->
                        if(task.isSuccessful)
                        {
                            Toast.makeText(this,"Kayıt tamamlandı!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, ArayuzActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }.addOnFailureListener { exception->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }
            }.addOnFailureListener { exception ->
                //Eğer herhangi bir başarısız durum olursa yapılacaklar
                Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
                //localizedMessage : Kullanıcının anlayabileceği dile çevrilmiş mesaj

            }
        }
        else
        {
            Toast.makeText(applicationContext,"Lütfen alanları doldurunuz!",Toast.LENGTH_LONG).show()
        }
    }

    fun girisYap(view : View)
    {
        if(emailText.text != null && passwordText.text != null)
        {
            auth.signInWithEmailAndPassword(emailText.text.toString(),passwordText.text.toString()).addOnCompleteListener { task ->
                //asenkron Bloklamaması için
                if(task.isSuccessful)
                {
                    try {
                        val currentUser = auth.currentUser?.email.toString()

                        database.collection("Kullanicilar").addSnapshotListener { snapshot, exception ->

                            if (exception != null) {
                                //hata var demektir
                                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                            } else {
                                if (snapshot != null) {
                                    if (snapshot.isEmpty == false) {
                                        val documents = snapshot.documents

                                        for (document in documents) {
                                            try {
                                                val email = document.get("email") as String
                                                if(email == currentUser)
                                                {
                                                    authorization = document.get("yetki") as String

                                                    if(authorization.toInt() == 0)
                                                    {
                                                        Toast.makeText(this,"Hoşgeldiniz ${currentUser}", Toast.LENGTH_LONG).show()

                                                        val intent = Intent(this, ArayuzActivity::class.java)
                                                        //Toast.makeText(applicationContext,authorization,Toast.LENGTH_LONG).show()
                                                        intent.putExtra("authorization",authorization)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                    else if(authorization.toInt() == 1)
                                                    {
                                                        authorization = "1"
                                                        Toast.makeText(this,"Hoşgeldiniz ${currentUser}", Toast.LENGTH_LONG).show()

                                                        //Toast.makeText(applicationContext,authorization,Toast.LENGTH_LONG).show()
                                                        val intent = Intent(this, SahipArayuzActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                }

                                            } catch (e: Exception) {
                                                Toast.makeText(applicationContext,e.localizedMessage, Toast.LENGTH_LONG).show()
                                            }

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
                    //println("Giriş Yaptan Geldim " + ürünList.size)
                    //urunEkle()
                    //başarılı olduysa


                    //currentUser : Güncel kullanıcı

                    //diğer aktiviteye geçiş


                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
        else
        {
            Toast.makeText(applicationContext,"Lütfen alanları doldurunuz!",Toast.LENGTH_LONG).show()
        }
    }
}