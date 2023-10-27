package com.example.findog

//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.MenuItem
//import android.view.ViewGroup
//import androidx.annotation.RequiresApi
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.example.findog.databinding.ListBinding
//import com.example.findog.databinding.MissedListBinding
//import com.example.findog.databinding.SearchResult2Binding
//import com.example.findog.databinding.SearchResultBinding
//import com.google.firebase.firestore.FieldPath
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.gson.Gson
//import kotlinx.serialization.Serializable
//import okhttp3.Call
//import okhttp3.Callback
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.Response
//import org.json.JSONArray
//import java.io.IOException
//
//class TextResultActivity: AppCompatActivity()  {
//
//    //data class Dog(var Date : String? = null, var Place : String? = null, var ImgUrl: String? = null, var Content: String? = null)
//
//    private lateinit var binding: SearchResultBinding
//    private var firestore : FirebaseFirestore? = null
//    private var resultList: List<String>? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        firestore = FirebaseFirestore.getInstance()
//
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        super.onCreate(savedInstanceState)
//        binding = SearchResultBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
//
//        // Get the list of integers from the Intent.
//        var indexList = intent.getIntegerArrayListExtra("list")
//        resultList = indexList!!.map { it.toString() }
//
//
//        val recyclerView = binding.recyclerview
//        recyclerView.adapter = RecyclerViewAdapter()
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
////        // Display the list of integers on the TextView.
////        val textView = binding.textView5
////        textView.text = list.toString()
//
//    }
//    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//        private var DogsList: ArrayList<MissedListActivity.Dog> = arrayListOf()
//
//        init {
//
//            resultList?.let {
//                firestore?.collection("missedData")
//                    ?.whereIn(FieldPath.documentId(), resultList!!)
//                    ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                        DogsList.clear()
//
//                        for (snapshot in querySnapshot!!.documents) {
//                            val item = snapshot.toObject(MissedListActivity.Dog::class.java)
//                            DogsList.add(item!!)
//                        }
//                        notifyDataSetChanged()
//                    }
//            }
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            val binding = ListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            return ViewHolder(binding)
//        }
//
//        inner class ViewHolder(val binding: ListBinding) : RecyclerView.ViewHolder(binding.root) {
//            init {
//                itemView.setOnClickListener {}
//            }
//        }
//
//
//        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            val viewHolder = holder as ViewHolder
//            val item = DogsList[position]
//            viewHolder.binding.place.text = item.Place
//            viewHolder.binding.date.text = item.Date
//            Glide.with(viewHolder.itemView.context)
//                .load(item.ImgUrl)
//                .into(viewHolder.binding.imageView)
//
//            holder.itemView.setOnClickListener {
//                val intent = Intent(holder.itemView?.context, DataSpecActivity::class.java)
//                intent.putExtra("image", item.ImgUrl)
//                intent.putExtra("place", item.Place)
//                intent.putExtra("date", item.Date)
//                intent.putExtra("feature", item.Content)
//                ContextCompat.startActivity(holder.itemView.context, intent, null)
//            }
//        }
//
//        override fun getItemCount(): Int {
//            return DogsList.size
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                finish()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//}

