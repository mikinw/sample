package com.mnw.deverestinterview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.mnw.deverestinterview.app.MovieRecyclerViewAdapter
import com.mnw.deverestinterview.databinding.FragmentItemListBinding
import com.mnw.deverestinterview.model.NetworkState
import com.mnw.deverestinterview.model.NetworkStateModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ItemListFragment : Fragment() {

    private val viewModel: ItemListViewModel by viewModels()

    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var networkStateModel: NetworkStateModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.itemList

        val itemDetailFragmentContainer: View? = view.findViewById(R.id.item_detail_nav_container)

        setupRecyclerView(recyclerView, itemDetailFragmentContainer)

        viewModel.movieList.observe(viewLifecycleOwner) {
            (binding.itemList.adapter as MovieRecyclerViewAdapter).submitList(it)
        }

        binding.swipeContainer.setOnRefreshListener {
            viewModel.getMovieList()
        }

        networkStateModel.networkState.observe(viewLifecycleOwner) {
            if (it == NetworkState.NO_ACTIVITY) {
                binding.swipeContainer.isRefreshing = false
            }
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
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            DividerItemDecoration.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}