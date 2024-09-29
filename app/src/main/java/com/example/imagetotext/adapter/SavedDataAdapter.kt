package com.example.imagetotext.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imagetotext.databinding.ItemSavedDataBinding
import com.example.imagetotext.model.SavedImageData
import com.example.imagetotext.view.YourScansActivity

class SavedDataAdapter : RecyclerView.Adapter<SavedDataAdapter.SavedDataViewHolder>() {

    private var savedDataList = listOf<SavedImageData>()

    // ViewHolder class using View Binding
    inner class SavedDataViewHolder(private val binding: ItemSavedDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(savedData: SavedImageData) {
            // Bind data to UI elements using View Binding
            binding.textView.text = savedData.text
            Glide.with(binding.imageView.context)
                .load(savedData.imagePath)
                .into(binding.imageView)
            // Set an OnClickListener to send data to another activity
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, YourScansActivity::class.java).apply {
                    // Pass data to the next activity
                    putExtra("IMAGE_PATH", savedData.imagePath)
                    putExtra("TEXT", savedData.text)
                }
                context.startActivity(intent)
            }
        }
    }

    // Inflate the layout using View Binding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedDataViewHolder {
        val binding = ItemSavedDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedDataViewHolder(binding)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: SavedDataViewHolder, position: Int) {
        holder.bind(savedDataList[position])
    }

    // Return the size of the list
    override fun getItemCount() = savedDataList.size

    // Submit new list data to the adapter
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(data: List<SavedImageData>) {
        savedDataList = data
        notifyDataSetChanged()
    }
}
