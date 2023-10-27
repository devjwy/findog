package com.example.findog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn1 = findViewById<Button>(R.id.btn1)
        val btn2 = findViewById<Button>(R.id.btn2)
        val btn3 = findViewById<Button>(R.id.btn3)
        val btn4 = findViewById<Button>(R.id.btn4)


        //실종견 찾기
        btn1.setOnClickListener {
            //다음화면으로 이동하기 위한 인텐트 객체 생성
            val intent = Intent(this, SearchMissingActivity::class.java)


            startActivity(intent)   //intent에 저장되어 있는 엑티비티 쪽으로 이동한다
            //finish() //자기 자신 액티비티 파괴

        }

        //실종견 전단지
        btn4.setOnClickListener {
            //다음화면으로 이동하기 위한 인텐트 객체 생성
            val intent = Intent(this, MissingListActivity::class.java)


            startActivity(intent)   //intent에 저장되어 있는 엑티비티 쪽으로 이동한다
            //finish() //자기 자신 액티비티 파괴

        }

        //유기견 제보하기
        btn3.setOnClickListener {
            //다음화면으로 이동하기 위한 인텐트 객체 생성
            val intent = Intent(this, ReportMissedActivity::class.java)


            startActivity(intent)   //intent에 저장되어 있는 엑티비티 쪽으로 이동한다
            //finish() //자기 자신 액티비티 파괴

        }

        //유기견 목록
        btn2.setOnClickListener {
            //다음화면으로 이동하기 위한 인텐트 객체 생성
            val intent = Intent(this, MissedListActivity::class.java)


            startActivity(intent)   //intent에 저장되어 있는 엑티비티 쪽으로 이동한다
            //finish() //자기 자신 액티비티 파괴

        }



    }


}