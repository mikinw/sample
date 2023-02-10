package com.mnw.deverestinterview

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors
import com.mnw.deverestinterview.databinding.FragmentNetworkStateListDialogBinding
import com.mnw.deverestinterview.model.NetworkState
import com.mnw.deverestinterview.model.NetworkStateModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class NetworkStateFragment : BottomSheetDialogFragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FragmentContainerView>
    private var _binding: FragmentNetworkStateListDialogBinding? = null

    @Inject
    lateinit var networkStateModel: NetworkStateModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNetworkStateListDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.networkStateRoot.parent as FragmentContainerView)

        bottomSheetBehavior.addBottomSheetCallback(disableUserInteraction)

        networkStateModel.networkState.observe(viewLifecycleOwner, setViewState)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val setViewState: (t: NetworkState) -> Unit = {
        Log.i("ASD", "new state: $it")
        when (it) {
            NetworkState.NO_ACTIVITY -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            NetworkState.REFRESHING -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.textMessage.setText(R.string.network_refreshing)
                val color = MaterialColors.getColor(requireContext(), androidx.transition.R.attr.colorPrimary, Color.GRAY)
                binding.networkStateRoot.setBackgroundColor(color)
            }
            NetworkState.OFFLINE -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.textMessage.setText(R.string.network_offline)
                val color = MaterialColors.getColor(requireContext(), androidx.transition.R.attr.colorPrimary, Color.GRAY)
                binding.networkStateRoot.setBackgroundColor(color)
            }
            NetworkState.ERROR -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.textMessage.setText(R.string.network_error)
                binding.networkStateRoot.setBackgroundResource(R.color.exclamation)
            }
        }
    }

    private val disableUserInteraction = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_EXPANDED || newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}

    }
}