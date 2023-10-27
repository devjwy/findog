package com.example.findog

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.findog.databinding.FinishUploadBinding
import com.example.findog.databinding.UploadMissingBinding

class FinishUploadActivity : AppCompatActivity(){

    lateinit var binding: FinishUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FinishUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.gotohome.setOnClickListener{
            var intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        binding.gotolist.setOnClickListener {
            if(intent.getStringExtra("type")=="등록"){
                var intent = Intent(this,MissingListActivity::class.java)
                startActivity(intent)
            }else{
                var intent = Intent(this,MissedListActivity::class.java)
                startActivity(intent)
            }
        }
    }
}