package com.waycool.iwap.notification

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.irrigationplanner.adapter.HistoryAdapter
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentNotificationBinding
import kotlinx.coroutines.launch


class NotificationFragment : Fragment() {
   private lateinit var binding:FragmentNotificationBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private lateinit var mNotificationAdapter: NotificationAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    this@NotificationFragment.findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater)
        setAdapter()
        binding.back.setOnClickListener(){
            findNavController().popBackStack()
        }
        newNotification()
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    private fun setAdapter(){
        mNotificationAdapter = NotificationAdapter(NotificationAdapter.OnClickListener { notification,dataNotification ->
            viewModel.getNotification().observe(viewLifecycleOwner){
                val deepLink = notification.link
                    if(!deepLink.isNullOrEmpty()) {
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
                        startActivity(i)
                }
                else if(deepLink.isNullOrEmpty()){
                        context?.let { it1 -> ToastStateHandling.toastError(it1,"Deeplink is null",Toast.LENGTH_SHORT) }
                }
            }
            if(dataNotification.readAt == null){
            viewModel.updateNotification(dataNotification.id!!).observe(viewLifecycleOwner){
              setAdapter()
                newNotification()

            }}
        })
        binding.recycleViewHis.adapter = mNotificationAdapter
        viewModel.getNotification().observe(viewLifecycleOwner){
            mNotificationAdapter.submitList(it.data?.data)
            Log.d("Notifcation", "setAdapter: ${it}")
        }
    }

    private fun newNotification(){
        viewModel.viewModelScope.launch {
            val title = TranslationsManager().getString("str_alerts_and_notification")
            val new = TranslationsManager().getString("str_new")
            binding.topAppBar.text = title
        viewModel.getNotification().observe(viewLifecycleOwner){

            when(it){
                is Resource.Success ->{
                    binding.progressBar.visibility = View.GONE
                    var data = it.data?.data?.filter { itt->
                        itt.readAt== null
                    }
                    data?.size
                    binding.tvSize.text = "$new ${data?.size}"
                    binding.tvSize.visibility = View.VISIBLE
                    Log.d("Notifcation", "setAdapter: ${it.data?.data?.size}")
                }
                is Resource.Loading->{
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvSize.visibility = View.GONE
                }
                is Resource.Error->{

                }
            }

        }
    }}


}