package com.waycool.iwap.notification

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.databinding.FragmentNotificationBinding
import com.waycool.uicomponents.R
import kotlinx.coroutines.launch


class NotificationFragment : Fragment() {
   private lateinit var binding:FragmentNotificationBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private lateinit var mNotificationAdapter: NotificationAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater)
        setAdapter()


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val isSuccess = findNavController().navigateUp()
                    if (!isSuccess) activity?.let { it.finish()}
                }
            }
        activity?.let {
            it.onBackPressedDispatcher.addCallback(
                it,
                callback
            )
        }

        binding.back.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) activity?.let { it1 -> it1.finish() }
        }

        binding.topAppBar.isSelected = true
        newNotification()
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    private fun setAdapter(){
        mNotificationAdapter = NotificationAdapter(NotificationAdapter.OnClickListener { notification,dataNotification ->
                val eventBundle=Bundle()
                eventBundle.putString("",notification.title)
                EventItemClickHandling.calculateItemClickEvent("NotificationItemClick",eventBundle)
                val deepLink = notification.link
                Log.d("Notification","Notification Link ${notification.link}")
                    if(!deepLink.isNullOrEmpty()) {
                        try {
                            val packageName = "com.android.chrome"
                            val customTabIntent: CustomTabsIntent = CustomTabsIntent.Builder()
                                .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
                                .build()
                            customTabIntent.intent.setPackage(packageName)
                            customTabIntent.launchUrl(
                                requireContext(),
                                Uri.parse(deepLink)
                            )
                        }catch (e:Exception){
                            Log.d("link", "onBindViewHolder: $e")
                        }

                    }else context?.let {
                        ToastStateHandling.toastError(
                            it,
                            "No Link",
                            Toast.LENGTH_SHORT
                        )
                    }

            if(dataNotification.readAt == null){
            viewModel.updateNotification(dataNotification.id!!).observe(viewLifecycleOwner) {
                setAdapter()
                newNotification()

            }}
        }, requireContext())

    }

    private fun newNotification(){
        viewModel.viewModelScope.launch {
            val title = TranslationsManager().getString("str_alerts_and_notification")
            val new = TranslationsManager().getString("str_new")
            binding.topAppBar.text = title
        viewModel.getNotification().observe(viewLifecycleOwner){

            when(it){
                is Resource.Success ->{
                    binding.recycleViewHis.adapter = mNotificationAdapter
                    mNotificationAdapter.submitList(it.data?.data)
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
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("NotificationFragment")
    }

}