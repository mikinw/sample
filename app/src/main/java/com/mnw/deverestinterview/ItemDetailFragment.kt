package com.mnw.deverestinterview

import android.content.ClipData
import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.mnw.deverestinterview.databinding.FragmentItemDetailBinding
import com.mnw.deverestinterview.model.Movie
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ItemDetailFragment : Fragment() {

    private val viewModel: DetailsViewModel by viewModels()

    lateinit var itemDetailTextView: TextView
    private var toolbarLayout: CollapsingToolbarLayout? = null

    private var _binding: FragmentItemDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val dragListener = View.OnDragListener { v, event ->
        if (event.action == DragEvent.ACTION_DROP) {
            val clipDataItem: ClipData.Item = event.clipData.getItemAt(0)
            val dragData = clipDataItem.text
            viewModel.setItemId(Integer.parseInt(dragData.toString()))
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID) && it.getInt(ARG_ITEM_ID) > 0) {
                viewModel.setItemId(it.getInt(ARG_ITEM_ID))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        val rootView = binding.root

        toolbarLayout = binding.toolbarLayout
        itemDetailTextView = binding.itemDetail

        rootView.setOnDragListener(dragListener)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.item.observe(viewLifecycleOwner) {
            updateContent(it)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun updateContent(movie: Movie?) {

        toolbarLayout?.title = movie?.title

        // Show the placeholder content as text in a TextView.
        movie?.let {
            itemDetailTextView.text = it.overview

            val imageUrl = it.posterPath

            binding.imageDetails?.let { imageView ->
                Glide
                    .with(requireContext())
                    .load(imageUrl)
                    .centerCrop()
                    .into(imageView)

            }
        }
    }

    companion object {
        const val ARG_ITEM_ID = "item_id"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}