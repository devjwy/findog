package com.example.findog

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.method.ScrollingMovementMethod
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.findog.databinding.DataSpecBinding
import com.google.common.util.concurrent.Futures.addCallback


class DataSpecActivity: AppCompatActivity() {

    private lateinit var binding: DataSpecBinding

    //콜백 인스턴스 생성
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val intent = Intent(this@DataSpecActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        super.onCreate(savedInstanceState)
        binding = DataSpecBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        Glide.with(this).load(intent.getStringExtra("image")).into(binding.SearchPhoto)
        binding.DateText.text =intent.getStringExtra("date")
        binding.PlaceText.text = intent.getStringExtra("place")
        binding.FeatureText.text = intent.getStringExtra("feature")

        binding.okayBtn.setOnClickListener{
            finish()
        }

        binding.FeatureText.setMovementMethod(ScrollingMovementMethod())
    }
}