package com.example.findog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Filter
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.example.findog.databinding.ListBinding
import com.example.findog.databinding.MissedListBinding
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.serialization.Serializable
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.util.HashMap
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MissedListActivity : AppCompatActivity() {

    data class Dog(var date : String? = null, var place : String? = null, var imgurl: String? = null, var content: String? = null)

    private lateinit var binding: MissedListBinding
    private var firestore : FirebaseFirestore? = null

    private var searchString: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        super.onCreate(savedInstanceState)
        binding = MissedListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.progressBar.visibility = View.GONE

        firestore = FirebaseFirestore.getInstance()

        val recyclerView = binding.recyclerview
        recyclerView.adapter = RecyclerViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.searchBtn.setOnClickListener{
            searchString = binding.searchTxt.text.toString()
            sendText()
        }
    }

    private fun sendText(){

        binding.progressBar.visibility = View.VISIBLE

        // Create a new HTTP client.
        val client = OkHttpClient.Builder()
            .connectTimeout(2000, TimeUnit.SECONDS)
            .readTimeout(2000, TimeUnit.SECONDS)
            .writeTimeout(2000, TimeUnit.SECONDS)
            .build();

        // Create a new Request object.
        val request = Request.Builder()
            .url("http://172.20.25.241:5000/txt")
            .post(
                FormBody.Builder()
                .add("searchTxt", searchString)
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
                            val intent = Intent(this@MissedListActivity, SearchResultActivity::class.java)

                            // Pass the list of integers to the SubActivity.
                            intent.putExtra("list", result)

                            // Start the SubActivity.
                            binding.progressBar.visibility = View.GONE
                            startActivity(intent)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            })
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var DogsList: ArrayList<Dog> = arrayListOf()

        init {

            firestore?.collection("missedData")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    DogsList.clear()

                    for (snapshot in querySnapshot!!.documents) {
                        val item = snapshot.toObject(Dog::class.java)
                        DogsList.add(item!!)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding = ListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        inner class ViewHolder(val binding: ListBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                itemView.setOnClickListener {}
            }
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder = holder as ViewHolder
            val item = DogsList[position]
            viewHolder.binding.place.text = item.place
            viewHolder.binding.date.text = item.date
            Glide.with(viewHolder.itemView.context)
                .load(item.imgurl)
                .into(viewHolder.binding.imageView)

            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView?.context, DataSpecActivity::class.java)
                intent.putExtra("image", item.imgurl)
                intent.putExtra("place", item.place)
                intent.putExtra("date", item.date)
                intent.putExtra("feature", item.content)
                ContextCompat.startActivity(holder.itemView.context, intent, null)
            }
        }

        override fun getItemCount(): Int {
            return DogsList.size
            return DogsList.size
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