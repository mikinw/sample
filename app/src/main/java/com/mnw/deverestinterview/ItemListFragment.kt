package com.mnw.deverestinterview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.mnw.deverestinterview.app.MovieRecyclerViewAdapter
import com.mnw.deverestinterview.databinding.FragmentItemListBinding


class ItemListFragment : Fragment() {

    private val viewModel: ItemListViewModel by viewModels()

    private var _binding: FragmentItemListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.itemList

        val itemDetailFragmentContainer: View? = view.findViewById(R.id.item_detail_nav_container)

        setupRecyclerView(recyclerView, itemDetailFragmentContainer)

        viewModel.movieList.observe(viewLifecycleOwner) {
            (binding.itemList.adapter as MovieRecyclerViewAdapter).setMovies(it)
        }

        viewModel.getMovieList()

    }

    private fun setupRecyclerView(
        recyclerView: RecyclerView,
        itemDetailFragmentContainer: View?
    ) {

        recyclerView.adapter = MovieRecyclerViewAdapter(
            itemDetailFragmentContainer
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}