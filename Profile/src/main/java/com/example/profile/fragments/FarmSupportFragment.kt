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
import androidx.navigation.fragment.findNavController
import com.example.profile.R
import com.example.profile.adapter.AddUseAdapter
import com.example.profile.databinding.FragmentFarmSupportBinding
import com.example.profile.viewModel.EditProfileViewModel


class FarmSupportFragment : Fragment() {
 private lateinit var binding: FragmentFarmSupportBinding
    private val viewModel: EditProfileViewModel by lazy {
        ViewModelProviders.of(this).get(EditProfileViewModel::class.java)
    }
    var name  = ""

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

        viewModel.getUserDetails().observe(viewLifecycleOwner){
            var accountId:Int? = null
            accountId = it.data?.accountId!!
            mAddUseAdapter = AddUseAdapter(AddUseAdapter.OnClickListener {
                name = it.name.toString()
                it.id?.let { it1 -> accountId?.let { it2 -> deleteDialog(it1, it2) } }
            })
            binding.recycleView.adapter = mAddUseAdapter
            accountId?.let {
                viewModel.getFarmSupport(it).observe(viewLifecycleOwner){
                    mAddUseAdapter.submitList(it.data?.data)

                }
            }
            Log.d("Clicked", "onCreateView: ${it.data?.accountId.toString()}")
        }


        onClick()
    }

    private fun onClick() {
        binding.addUser.setOnClickListener() {
            this.findNavController().navigate(FarmSupportFragmentDirections.actionFarmSupportFragmentToAddFarmFragment())
        }
        binding.topAppBar.setNavigationOnClickListener(){
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
        val Username = dialog.findViewById(R.id.textView15) as TextView
        delete.setOnClickListener {
            viewModel.deleteFarmSupport(id).observe(viewLifecycleOwner) {
           // Toast.makeText(context,"Deleted",Toast.LENGTH_LONG).show()
            }
            viewModel.getFarmSupport(accountId).observe(viewLifecycleOwner){
                mAddUseAdapter.submitList(it.data?.data)
            }
            dialog.dismiss()
        }
        Username.text = "Are you Sure you want to delete $name account?"
        cancel.setOnClickListener { dialog.dismiss() }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
    }