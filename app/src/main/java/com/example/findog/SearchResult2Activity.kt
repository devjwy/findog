package com.example.findog
import com.example.findog.DataSpecActivity
import com.example.findog.MissedListActivity
import com.example.findog.UploadMissingActivity
import com.example.findog.databinding.SearchResult2Binding



import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findog.databinding.ListBinding
import com.example.findog.databinding.MissedListBinding
import com.example.findog.databinding.SearchResultBinding
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class SearchResult2Activity : AppCompatActivity(){

    //data class Dog(var date : String? = null, var place : String? = null, var imageUrl: String? = null)

    private lateinit var binding: SearchResult2Binding
    private var firestore : FirebaseFirestore? = null
    private var resultList: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        super.onCreate(savedInstanceState)
        binding = SearchResult2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firestore = FirebaseFirestore.getInstance()

        // Get the list of integers from the Intent.
        var indexList = intent.getIntegerArrayListExtra("list")
        resultList = indexList!!.map { it.toString() }


        val recyclerView = binding.recyclerview
        recyclerView.adapter = RecyclerViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.gotolistBtn.setOnClickListener{
            val intent = Intent(this, MissingListActivity::class.java)
            startActivity(intent)
        }

        binding.gotouploadBtn.setOnClickListener{
            val intentFin = Intent(this, FinishUploadActivity::class.java)
            intentFin.putExtra("type", "신고")
            startActivity(intentFin)

        }
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var DogsList: ArrayList<MissedListActivity.Dog> = arrayListOf()

        init {
            firestore?.collection("missingData")
                ?.whereIn(FieldPath.documentId(), resultList!!)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    DogsList.clear()

                    for (snapshot in querySnapshot!!.documents) {
                        val item = snapshot.toObject(MissedListActivity.Dog::class.java)
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
        }
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
