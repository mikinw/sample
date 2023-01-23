package com.mnw.deverestinterview

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.mnw.deverestinterview.app.MovieRecyclerViewAdapter
import com.mnw.deverestinterview.placeholder.PlaceholderContent;
import com.mnw.deverestinterview.databinding.FragmentItemListBinding
import com.mnw.deverestinterview.net.MoviesApi
import com.mnw.deverestinterview.net.MoviesClient
import kotlinx.coroutines.launch

/**
 * A Fragment representing a list of Pings. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link ItemDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */

class ItemListFragment : Fragment() {

    /**
     * Method to intercept global key events in the
     * item list fragment to trigger keyboard shortcuts
     * Currently provides a toast when Ctrl + Z and Ctrl + F
     * are triggered
     */
    private val unhandledKeyEventListenerCompat = ViewCompat.OnUnhandledKeyEventListenerCompat { v, event ->
        if (event.keyCode == KeyEvent.KEYCODE_Z && event.isCtrlPressed) {
            Toast.makeText(
                v.context,
                "Undo (Ctrl + Z) shortcut triggered",
                Toast.LENGTH_LONG
            ).show()
            true
        } else if (event.keyCode == KeyEvent.KEYCODE_F && event.isCtrlPressed) {
            Toast.makeText(
                v.context,
                "Find (Ctrl + F) shortcut triggered",
                Toast.LENGTH_LONG
            ).show()
            true
        }
        false
    }

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

        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat)

        val recyclerView: RecyclerView = binding.itemList

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        val itemDetailFragmentContainer: View? = view.findViewById(R.id.item_detail_nav_container)

        setupRecyclerView(recyclerView, itemDetailFragmentContainer)

        getMovieList()
    }

    private fun setupRecyclerView(
        recyclerView: RecyclerView,
        itemDetailFragmentContainer: View?
    ) {

        recyclerView.adapter = MovieRecyclerViewAdapter(
            PlaceholderContent.ITEMS, itemDetailFragmentContainer
        )
    }

    fun decodeBase64(imageString: String): Bitmap {
        val bytes: ByteArray = Base64.decode(imageString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun getMovieList() {
        var retrofit = MoviesClient.getInstance()
        var apiInterface = retrofit.create(MoviesApi::class.java)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                try {
                    val response = apiInterface.searchMovies("a")
                    if (response.isSuccessful) {

                        PlaceholderContent.clear()
                        response.body()?.movieList?.forEach { movie ->
                            val item = PlaceholderContent.PlaceholderItem(movie.thumbnail.substring(0,4), movie.title, movie.overview, movie.releaseDate)
                            PlaceholderContent.addItem(item)
                        }

                        (binding.itemList.adapter as MovieRecyclerViewAdapter).notifyDataSetChanged()

                    } else {
                        Toast.makeText(
                            context,
                            response.errorBody().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }catch (Ex:Exception){
                    Log.e("Error", Ex.localizedMessage)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}