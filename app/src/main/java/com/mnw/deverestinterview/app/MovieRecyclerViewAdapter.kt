package com.mnw.deverestinterview.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mnw.deverestinterview.ItemDetailFragment
import com.mnw.deverestinterview.R
import com.mnw.deverestinterview.databinding.ItemListContentBinding
import com.mnw.deverestinterview.model.Movie

class MovieRecyclerViewAdapter(
    private val itemDetailFragmentContainer: View?
) :
    RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder>() {

    private var movies: MutableList<Movie> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemListContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = movies[position]
        holder.movieTitle.text = item.title
        holder.overview.text = item.overview
        holder.releaseDate.text = item.releaseDate
        holder.thumbnail.setImageBitmap(item.thumbnail)

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

    override fun getItemCount() = movies.size

    fun setMovies(it: List<Movie>) {
        movies = ArrayList(it)
        notifyDataSetChanged()

    }

    inner class ViewHolder(binding: ItemListContentBinding) : RecyclerView.ViewHolder(binding.root) {
        val movieTitle: TextView = binding.textTitle
        val overview: TextView = binding.textOverview
        val releaseDate: TextView = binding.textReleaseDate
        val thumbnail: ImageView = binding.imageThumbnail
    }

}