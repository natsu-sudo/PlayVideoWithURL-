package com.assignment.kutuki.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.assignment.kutuki.MediaPlayer.exoPlayer
import com.assignment.kutuki.MediaPlayer.initialize
import com.assignment.kutuki.MediaPlayer.pausePlayer
import com.assignment.kutuki.MediaPlayer.stopPlayer
import com.assignment.kutuki.adapter.ListOfVideoAdapter
import com.assignment.kutuki.databinding.FragmentVideoBinding
import com.assignment.kutuki.pojo.VideoDetail
import com.assignment.kutuki.preparePlayer
import com.assignment.kutuki.setSource
import com.assignment.kutuki.viewmodel.VideoListViewModel
import kotlinx.coroutines.launch

private const val TAG = "VideoFragment"
class VideoFragment : Fragment() {

    lateinit var videoViewModel: VideoListViewModel
    private var _binding:FragmentVideoBinding?=null
    private val binding get() = _binding!!
    var firstTime=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoViewModel=ViewModelProvider(this)[VideoListViewModel::class.java]
        videoViewModel.setCateGoryValue(VideoFragmentArgs.fromBundle(requireArguments()).category)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialize(view.context.applicationContext)
        exoPlayer?.preparePlayer(binding.playerView, false)
        lifecycleScope.launch {
            videoViewModel.fetchFromNetwork()
        }
        binding.videoListRecycleView.apply {
            layoutManager=LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter=ListOfVideoAdapter{
                setThumbNail(it)
            }


        }
        Log.d(TAG, "onViewCreated: " + VideoFragmentArgs.fromBundle(requireArguments()).category)
        videoViewModel.listOfVideo.observe(viewLifecycleOwner, Observer {
            (binding.videoListRecycleView.adapter as ListOfVideoAdapter).submitList(it)
            if (it.isNotEmpty() && firstTime) {
                firstTime = false
                setThumbNail(it[0])
            }
        })

        binding.materialButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setThumbNail(item: VideoDetail) {
        exoPlayer?.setSource(requireContext().applicationContext, item.videoURL)
        pausePlayer()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayer()
    }

}

