package com.example.findog//package com.example.findog
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//
////class ProfileAdapter(val profileList: ArrayList<Profiles>): RecyclerView.Adapter<ProfileAdapter.CustomViewHolder>() {
//
////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
////        val view = LayoutInflater.from(parent.context).inflate(R.layout.list, parent, false)
////        return CustomViewHolder(view)
////    }
////
////    override fun getItemCount(): Int {
////        return profileList.size
////    }
////
////    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
////        holder.image.setImageResource(profileList.get(position).imageUrl)
////        holder.date.text = profileList.get(position).date
////    }
////
////    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
////        val image = itemView.findViewById<ImageView>(R.id.iv_custom)
////        val date = itemView.findViewById<TextView>(R.id.tv_custom) //이름
////    }
//class ProfileAdapter(private val arrayList: ArrayList<Profiles>) :
//    RecyclerView.Adapter<ProfileAdapter.CustomViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.list, parent, false)
//        return CustomViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        // Profiles : Profiles = arrayList[position]
//        //Glide.with(holder.itemView).load(Profiles.imageUrl).into(holder.image)
//        holder.image.setImageResource(arrayList[position].imageUrl)
//        holder.date.text = arrayList[position].date
//    }
//
//    override fun getItemCount(): Int {
//        return arrayList.size
//    }
//
//    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val image: ImageView = itemView.findViewById(R.id.iv_custom)
//        val date : TextView = itemView.findViewById(R.id.tv_custom)
//    }
//}
////}