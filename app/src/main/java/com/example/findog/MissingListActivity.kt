package com.example.findog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findog.databinding.ListBinding
import com.example.findog.databinding.MissedListBinding
import com.example.findog.databinding.MissingListBinding
import com.google.firebase.firestore.FirebaseFirestore

class MissingListActivity : AppCompatActivity() {

    data class Dog(var date : String? = null, var place : String? = null, var imgurl: String? = null, var content: String? = null)

    private lateinit var binding: MissingListBinding
    private var firestore : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        super.onCreate(savedInstanceState)
        binding = MissingListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firestore = FirebaseFirestore.getInstance()

        val recyclerView = binding.recyclerview
        recyclerView.adapter = RecyclerViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.uploadBtn.setOnClickListener {
            val intent = Intent(this, UploadMissingActivity::class.java)
            startActivity(intent)
        }
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var DogsList: ArrayList<Dog> = arrayListOf()

        init {
            firestore?.collection("missingData")
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
                itemView.setOnClickListener{}
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

            holder.itemView.setOnClickListener{
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