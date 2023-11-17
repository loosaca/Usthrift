package com.example.usthrift

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import java.util.Properties
import java.util.Random
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MainActivity : ComponentActivity() {
    data class User(val email: String, val verificationCode: String)
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)


        val sendbtn = findViewById<Button>(R.id.codeBtn)
        val loginbtn = findViewById<Button>(R.id.loginbutton)

        var toAddress:String
        var enterCode:String
        sendbtn.setOnClickListener {
            toAddress = findViewById<EditText>(R.id.username).text.toString()

            if (toAddress.isEmpty())
                Toast.makeText(this, "Empty account input", Toast.LENGTH_SHORT).show()
            else
                sendCode(toAddress + "@connect.ust.hk")
        }
        loginbtn.setOnClickListener {
            enterCode = findViewById<EditText>(R.id.loginCode).text.toString()
            toAddress = findViewById<EditText>(R.id.username).text.toString()

            if (toAddress.isEmpty() || enterCode.isEmpty())
                Toast.makeText(this, "Empty account or code input ", Toast.LENGTH_SHORT).show()
            else {
                if(verifyCode(enterCode, user.verificationCode)) {
                    //**************Successfully login**************************
                    //redirect users to forum page?
                    val intent = Intent(this, ForumActivity::class.java)
                }
                else{
                    Toast.makeText(this, "Wrong code input", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }



    fun sendCode(toAddress: String) {
        val thread = Thread {
            try {
                val pwd = "WUBWAXFQKGDWQIYL"
                val fromPerson = "13312989571@163.com"
//                val toAddress = "sikailin0@gmail.com"

                val properties = Properties()
                properties["mail.smtp.host"] = "smtp.163.com"
                properties["mail.smtp.port"] = "465"
                properties["mail.smtp.auth"] = "true"
                properties["mail.smtp.starttls.enable"] = "true"
                properties["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"


                val session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(fromPerson, pwd)
                    }
                })

                try {
                    val message = MimeMessage(session)
                    message.setFrom(InternetAddress(fromPerson))
                    message.addRecipient(Message.RecipientType.TO, InternetAddress(toAddress))
                    message.subject = "Usthrift"
                    user = User(toAddress, generateVerificationCode())
                    message.setText("Your code is " + user.verificationCode)
                    Transport.send(message)
                    println("Email sent successfully")
                } catch (e: MessagingException) {
                    e.printStackTrace()
                }
                // Your code goes here
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
    }

    fun generateVerificationCode(): String {
        val random = Random()
        val code = StringBuilder()
        for (i in 0..5) {
            code.append(random.nextInt(10))
        }
        return code.toString()
    }

    fun verifyCode(enteredCode: String, expectedCode: String): Boolean {
        return enteredCode == expectedCode
    }
}



