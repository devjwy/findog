package com.example.findog

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.bumptech.glide.Glide
import com.example.findog.databinding.ReportMissedBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.AggregateQuerySnapshot
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ReportMissedActivity : AppCompatActivity(){

    lateinit var binding: ReportMissedBinding
    var dateString = ""
    var axisString = ""
    private var bitmap: Bitmap? = null
    private var imageString: String? = null
    private var queue: RequestQueue? = null
    private var uri:Uri? = null
    lateinit var storage: FirebaseStorage
    lateinit var firestore: FirebaseFirestore


    //콜백 인스턴스 생성
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val intent = Intent(this@ReportMissedActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReportMissedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.progressBar.visibility = View.GONE

        //파이어베이스
        storage= FirebaseStorage.getInstance()
        firestore=FirebaseFirestore.getInstance()

        //캘린더
        binding.DateText.setOnClickListener {
            val cal = Calendar.getInstance()
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

        //업로드
//        binding.UploadBtn.setOnClickListener {
//            uri?.let { it1 -> uploadImageToFirebase(it1!!) }
//        }
        binding.UploadBtn.setOnClickListener {
            uri?.let { it1 -> uploadImageToFirebase(it1!!) }
            sendImage()
        }

        //뒤로가기
        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    //사진
//    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()){
//        if(it.resultCode == RESULT_OK && it.data != null){
//            //val uri = it.data!!.data
//            uri = it.data!!.data
//            Glide.with(this)
//                .load(uri)
//                .into(binding.SearchPhoto)
//        }
//    }
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
            .url("http://172.20.25.241:5000/img_back")
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
                        val intent = Intent(this@ReportMissedActivity, SearchResult2Activity::class.java)

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
                axisString = result.data?.getStringExtra("axis").toString()
            }
            else {
                Toast.makeText(this, "수신 실패", Toast.LENGTH_SHORT).show()
            }
        }
        return resultLauncher
    }

    //발견 신고 업로드
    fun uploadImageToFirebase(uri: Uri){

        binding.progressBar.visibility = View.VISIBLE

        //파이어베이스 스토리지 접근 위해 선언
        var storage: FirebaseStorage? = FirebaseStorage.getInstance()

        var fileName = "IMAGE_${SimpleDateFormat("yyyymmdd_HHmmss").format(Date())}_.png"

        var imageRef = storage!!.reference.child("images/").child(fileName)

        imageRef.putFile(uri!!).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask imageRef.downloadUrl
        }.addOnSuccessListener {

            var missedData: MissedData = MissedData()
            missedData.imgurl = it.toString()
            missedData.place = binding.PlaceText.text.toString()
            missedData.axis = axisString
            missedData.date = binding.DateText.text.toString()
            missedData.content = binding.FeatureText.text.toString()
            missedData.type = "발견제보"
            missedData.emb = "new"


            //위의 이미지와 내용 입력이 완료되었으면 파이어스토어에 내용이 들어가도록
            FirebaseFirestore.getInstance().collection("missedData").document().set(missedData)
            finish()

            binding.progressBar.visibility = View.GONE
//            val toast = Toast.makeText(this, "신고 완료", Toast.LENGTH_SHORT) // in Activity
//            toast.show()

            uploadTest()

            binding.progressBar.visibility = View.GONE
            //sendImage()

//            val intentFin = Intent(this, FinishUploadActivity::class.java)
//            intentFin.putExtra("type", "신고")
//            startActivity(intentFin)

        }.addOnFailureListener{
            println(it)
        }
    }

    private fun uploadTest() {

        // Create a new HTTP client.
        val client = OkHttpClient.Builder()
            .connectTimeout(1200, TimeUnit.SECONDS)
            .readTimeout(1200, TimeUnit.SECONDS)
            .writeTimeout(1200, TimeUnit.SECONDS)
            .build();


        // Create a new Request object.
        val request = Request.Builder()
            .url("http://172.20.25.241:5000/upload")
            .post(
                FormBody.Builder()
                    .add("test", "upload finish")
                    .build())
            .build()

        // Send the request.
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainActivity", "Request failed: $e")
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {

            }
        })
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


