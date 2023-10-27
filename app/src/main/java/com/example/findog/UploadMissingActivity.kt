package com.example.findog

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.findog.databinding.ReportMissedBinding
import com.example.findog.databinding.UploadMissingBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class UploadMissingActivity : AppCompatActivity(){

    lateinit var binding: UploadMissingBinding
    var dateString = ""
    var axisString = ""

    private var uri:Uri? = null
    lateinit var storage: FirebaseStorage
    lateinit var firestore: FirebaseFirestore

    //콜백 인스턴스 생성
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val intent = Intent(this@UploadMissingActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UploadMissingBinding.inflate(layoutInflater)
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
        binding.UploadBtn.setOnClickListener {
            uri?.let { it1 -> uploadImageToFirebase(it1!!) }
        }

        //뒤로가기
        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK && it.data != null){
            //val uri = it.data!!.data
            uri = it.data!!.data
            Glide.with(this)
                .load(uri)
                .into(binding.SearchPhoto)
        }
    }

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

    fun uploadImageToFirebase(uri: Uri){

        binding.progressBar.visibility = View.VISIBLE

        //파이어베이스 스토리지 접근 위해 선언
        var storage: FirebaseStorage? = FirebaseStorage.getInstance()

        var fileName = "IMAGE_${SimpleDateFormat("yyyymmdd_HHmmss").format(Date())}_.png"

        var imageRef = storage!!.reference.child("images/").child(fileName)

        imageRef.putFile(uri!!).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask imageRef.downloadUrl
        }.addOnSuccessListener {

            var missingData: MissingData = MissingData()
            missingData.imageUrl = it.toString()
            missingData.place = binding.PlaceText.text.toString()
            missingData.axis = axisString
            missingData.date = binding.DateText.text.toString()
            missingData.description = binding.FeatureText.text.toString()
            missingData.type = "실종신고"

            //위의 이미지와 내용 입력이 완료되었으면 파이어스토어에 내용이 들어가도록
            FirebaseFirestore.getInstance().collection("missingData").document().set(missingData)
            finish()

            binding.progressBar.visibility = View.GONE

            val intentFin = Intent(this, FinishUploadActivity::class.java)
            intentFin.putExtra("type", "등록")
            startActivity(intentFin)

        }.addOnFailureListener{
            println(it)
        }
    }



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