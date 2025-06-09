package com.puc.pyp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puc.pyp.databinding.ItemViewLanBinding

class AdapterText (private val lanitems: List<TextItem>,
                  private val itemClickListener: (TextItem)-> Unit) : RecyclerView.Adapter<AdapterText.LanViewHolder>() {

    inner class LanViewHolder(private val binding: ItemViewLanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TextItem) {
            binding.descriptionLan.text = item.descLan
            binding.root.setOnClickListener{
                itemClickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemViewLanBinding.inflate(inflater, parent, false)
        return LanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanViewHolder, position: Int) {
        holder.bind(lanitems[position])
    }

    override fun getItemCount(): Int = lanitems.size
}