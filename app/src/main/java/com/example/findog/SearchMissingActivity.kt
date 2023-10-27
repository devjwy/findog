package com.example.findog

import android.R
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.findog.databinding.SearchMissingBinding
import okhttp3.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class SearchMissingActivity : AppCompatActivity() {

    lateinit var binding: SearchMissingBinding
    var dateString = ""

    private var bitmap: Bitmap? = null
    private var imageString: String? = null
    private var queue: RequestQueue? = null

    //콜백 인스턴스 생성
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val intent = Intent(this@SearchMissingActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchMissingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.progressBar.visibility = View.GONE


        // 캘린더
        binding.DateText.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    dateString = "${year}년 ${month + 1}월 ${dayOfMonth}일"
                    binding.DateText.text = dateString
                }
            DatePickerDialog(
                this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        //지도
        val activityLauncher= openActivityResultLauncher()
        binding.PlaceText.setOnClickListener{
            val intent = Intent(this, mapNew::class.java)
            activityLauncher.launch(intent)
        }

        //사진
        binding.PhotoBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activityResult.launch(intent)
        }

        //검색
        binding.SearchBtn.setOnClickListener{
            sendImage()
//            val intent = Intent(this,TextResultActivity::class.java)
//            startActivity(intent)
        }

        //뒤로가기
        this.onBackPressedDispatcher.addCallback(this, callback)
    }


    //사진
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK && it.data != null){
//            val uri = it.data!!.data
//            Glide.with(this)
//                .load(uri)
//                .into(binding.SearchPhoto)
            val galleryURI = it.data!!.data
            //img.setImageURI(galleryURI);
            getBitmap(galleryURI)
            binding.SearchPhoto.setImageBitmap(bitmap)
        }
    }
    //이미지 플라스크로 전송
    private fun sendImage() {

        binding.progressBar.visibility = View.VISIBLE

        //비트맵 이미지를 byte로 변환 -> base64형태로 변환
        val baos = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        // Create a new HTTP client.
        val client = OkHttpClient.Builder()
            .connectTimeout(2000, TimeUnit.SECONDS)
            .readTimeout(2000, TimeUnit.SECONDS)
            .writeTimeout(2000, TimeUnit.SECONDS)
            .build();


        // Create a new Request object.
        val request = Request.Builder()
            .url("http://172.20.25.241:5000/img")
            .post(
                FormBody.Builder()
                    .add("image", imageString)
                    .build())
            .build()

        // Send the request.
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainActivity", "Request failed: $e")
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {

                //val body = response.body()?.string() ?: ""
                val jsonData = response.body()?.string()
                runOnUiThread {
                    try {
                        val jsonObject = JSONObject(jsonData)
                        val numList = jsonObject.getJSONArray("list")

                        val result = ArrayList<Int>()
                        for (i in 0 until numList.length()) {
                            result.add(numList.getInt(i))
                        }

                        // result 리스트를 사용하여 원하는 작업 수행
                        val intent = Intent(this@SearchMissingActivity, SearchResultActivity::class.java)

                        // Pass the list of integers to the SubActivity.
                        intent.putExtra("list", result)

                        // Start the SubActivity.
                        binding.progressBar.visibility = View.GONE
                        startActivity(intent)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
             //Log.d("MainActivity", "Response body: $body")
            }
        })
    }

    //Uri에서 bisap
    private fun getBitmap(picturePhotoURI: Uri?) {
        try {
            //서버로 이미지를 전송하기 위한 비트맵 변환하기
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, picturePhotoURI)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //지도
    private fun openActivityResultLauncher(): ActivityResultLauncher<Intent> {
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                //Toast.makeText(this, "수신 성공", Toast.LENGTH_SHORT).show()
                binding.PlaceText.text = result.data?.getStringExtra("address")
            }
            else {
                Toast.makeText(this, "수신 실패", Toast.LENGTH_SHORT).show()
            }
        }
        return resultLauncher
    }

    //뒤로가기
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}