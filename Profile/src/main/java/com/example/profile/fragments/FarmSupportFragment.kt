package com.example.profile.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.profile.R
import com.example.profile.adapter.AddUseAdapter
import com.example.profile.databinding.FragmentFarmSupportBinding
import com.example.profile.viewModel.EditProfileViewModel
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import kotlinx.coroutines.launch


class FarmSupportFragment : Fragment() {
 private lateinit var binding: FragmentFarmSupportBinding
    private val viewModel: EditProfileViewModel by lazy {
        ViewModelProviders.of(this)[EditProfileViewModel::class.java]
    }
    var name  = ""
    private lateinit var title:String
    private lateinit var addUser:String
    private lateinit var deleteDes:String

    private lateinit var mAddUseAdapter: AddUseAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFarmSupportBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserDetails().observe(viewLifecycleOwner){ resource ->
            if(resource.data?.roleId==31){
                binding.addUser.visibility = View.INVISIBLE
            }else{
                binding.addUser.visibility = View.VISIBLE
            }
            val accountId: Int? = resource.data?.accountId!!
            mAddUseAdapter = AddUseAdapter(AddUseAdapter.OnClickListener {
                name = it.name.toString()
                it.id?.let { it1 -> accountId?.let { it2 -> deleteDialog(it1, it2) } }
            }, resource.data!!)
            binding.recycleView.adapter = mAddUseAdapter
            accountId?.let { it ->
                viewModel.getFarmSupport(it).observe(viewLifecycleOwner){
                    mAddUseAdapter.submitList(it.data?.data)

                }
            }
            viewModel.viewModelScope.launch {
                title = TranslationsManager().getString("str_farm_support")
                binding.topAppBar.title = title
                addUser = TranslationsManager().getString("str_add_user")
                binding.addUser.text = addUser
            }
            Log.d("Clicked", "onCreateView: ${resource.data?.accountId.toString()}")
        }


        onClick()
    }

    private fun onClick() {
        binding.addUser.setOnClickListener {
            this.findNavController().navigate(FarmSupportFragmentDirections.actionFarmSupportFragmentToAddFarmFragment())
        }
        binding.topAppBar.setNavigationOnClickListener {
            this.findNavController().navigateUp()
        }
    }
    private fun deleteDialog(id:Int,accountId:Int){
        val dialog = Dialog(requireContext())

        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_support_delete)
        // val body = dialog.findViewById(R.id.body) as TextView
        val cancel = dialog.findViewById(R.id.cancel) as Button
        val delete = dialog.findViewById(R.id.delete) as Button
        val username = dialog.findViewById(R.id.textView15) as TextView
        val areYouSure = dialog.findViewById(R.id.textView14)as TextView
        delete.setOnClickListener {
            viewModel.deleteFarmSupport(id).observe(viewLifecycleOwner) {
           // Toast.makeText(context,"Deleted",Toast.LENGTH_LONG).show()
            }
            viewModel.getFarmSupport(accountId).observe(viewLifecycleOwner){
                when(it){
                    is Resource.Success->{
                        mAddUseAdapter.submitList(it.data?.data)
                        binding.progressBar.visibility = View.GONE
                    }
                    is Resource.Loading->{
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Error->{}
                }

            }
            dialog.dismiss()
        }
        viewModel.viewModelScope.launch {
           deleteDes = TranslationsManager().getString("str_delete_desc")
            username.text = deleteDes

        }
        TranslationsManager().loadString("delete_farm_support",areYouSure,"Are you Sure you want to \n" +
                "delete this account? ")
        TranslationsManager().loadString("str_delete",delete,"Delete")
        TranslationsManager().loadString("str_cancel",cancel,"Cancel")

        cancel.setOnClickListener { dialog.dismiss() }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
    }