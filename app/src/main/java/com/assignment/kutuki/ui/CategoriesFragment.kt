package com.assignment.kutuki.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.assignment.kutuki.R
import com.assignment.kutuki.adapter.ListOfCategoryAdapter
import com.assignment.kutuki.databinding.FragmentCategoriesBinding
import com.assignment.kutuki.pojo.ErrorCode
import com.assignment.kutuki.pojo.Status
import com.assignment.kutuki.viewmodel.CategoryViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


private const val TAG = "CategoriesFragment"
class CategoriesFragment : Fragment() {
    private var _binding:FragmentCategoriesBinding?=null
    private val binding get() = _binding!!
    private  lateinit var categoryViewModel:CategoryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryViewModel=ViewModelProvider(this)[CategoryViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        binding.recyclerCateList.apply {
            layoutManager=GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
            adapter= ListOfCategoryAdapter{
                findNavController().navigate(CategoriesFragmentDirections.actionCategoriesFragmentToVideoFragment(it))
            }

        }
        val snapHelper=LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerCateList)
        lifecycleScope.launch {
            categoryViewModel.fetchFromNetwork()
        }
        categoryViewModel.listOfCategory.observe(viewLifecycleOwner, Observer {
            ( binding.recyclerCateList.adapter as ListOfCategoryAdapter ).submitList(it)
        })

        categoryViewModel.status.observe(viewLifecycleOwner, Observer {loadingStatus->
            when (loadingStatus?.status) {
                (Status.LOADING) -> {
                    binding.progressBar.visibility = View.VISIBLE
                    Log.d(TAG, "onViewCreated: Status loading")
                }
                (Status.SUCCESS) -> {
                    binding.progressBar.visibility = View.GONE
                    binding.loadingStatusText.visibility=View.GONE
                }
                (Status.ERROR) -> {
                    binding.loadingStatusText.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    showError(loadingStatus.error, loadingStatus.message)
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
            binding.swipeUp.isRefreshing = false
        })

        binding.swipeUp.setOnRefreshListener {
            if (isOnline(requireActivity())){
                lifecycleScope.launch {
                    categoryViewModel.fetchFromNetwork()
                }
            }else{
                Snackbar.make(binding.root, getString(R.string.network_error), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.ok)) {

                        }
                        .show()
                binding.swipeUp.isRefreshing=false
            }

        }

    }

    private fun showError(errorCode: ErrorCode?, message: String?) {
        Log.d(TAG, "showError: ")
        when (errorCode) {
            ErrorCode.NO_DATA -> binding.loadingStatusText.text = getString(R.string.error_no_data)
            ErrorCode.NETWORK_ERROR -> binding.loadingStatusText.text =
                    getString(R.string.error_network)
            ErrorCode.UNKNOWN_ERROR -> binding.loadingStatusText.text =
                    getString(R.string.error_unknown, message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }





}