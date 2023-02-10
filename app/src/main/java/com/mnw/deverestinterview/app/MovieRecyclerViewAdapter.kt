package com.mnw.deverestinterview.app

import android.icu.text.DecimalFormat
import android.icu.text.DecimalFormatSymbols
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mnw.deverestinterview.ItemDetailFragment
import com.mnw.deverestinterview.R
import com.mnw.deverestinterview.databinding.ItemListContentBinding
import com.mnw.deverestinterview.model.Movie
import java.util.*


private class DiffCallback : DiffUtil.ItemCallback<Movie>() {

    override fun areItemsTheSame(oldItem: Movie, newItem: Movie) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie) =
        oldItem == newItem
}


class MovieRecyclerViewAdapter(
    private val itemDetailFragmentContainer: View?
) : ListAdapter<Movie, MovieRecyclerViewAdapter.ViewHolder>(DiffCallback()) {

    private val decimalFormat = DecimalFormat("#,###", DecimalFormatSymbols(Locale.ROOT).apply {
        groupingSeparator = ' '
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemListContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.movieTitle.text = item.title
        holder.overview.text = item.overview
        holder.releaseDate.text = item.releaseDate
        if (item.budget == null) {
            holder.budget.visibility = View.GONE
        } else {
            holder.budget.visibility = View.VISIBLE
            holder.budget.text = "$${decimalFormat.format(item.budget)}"
        }

        if (item.posterPath.isNotBlank()) {
            Glide
                .with(holder.itemView)
                .load(item.posterPath)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .into(holder.thumbnail)

        }

        with(holder.itemView) {
            tag = item
            setOnClickListener { itemView ->
                val item = itemView.tag as Movie
                val bundle = Bundle()
                bundle.putInt(
                    ItemDetailFragment.ARG_ITEM_ID,
                    item.id
                )
                if (itemDetailFragmentContainer != null) {
                    itemDetailFragmentContainer.findNavController()
                        .navigate(R.id.fragment_item_detail, bundle)
                } else {
                    itemView.findNavController().navigate(R.id.show_item_detail, bundle)
                }
            }

        }
    }


    inner class ViewHolder(binding: ItemListContentBinding) : RecyclerView.ViewHolder(binding.root) {
        val movieTitle: TextView = binding.textTitle
        val overview: TextView = binding.textOverview
        val releaseDate: TextView = binding.textReleaseDate
        val budget: TextView = binding.textBudget
        val thumbnail: ImageView = binding.imageThumbnail
    }

}