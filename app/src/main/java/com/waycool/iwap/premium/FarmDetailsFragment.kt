package com.waycool.iwap.premium

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.cropinformation.adapter.MyCropsAdapter
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentFarmDetails2Binding
import com.waycool.iwap.databinding.FragmentFarmDetailsBinding
import com.waycool.iwap.databinding.FragmentHomePagePremiumBinding


class FarmDetailsFragment : Fragment() {
    private var _binding: FragmentFarmDetails2Binding? = null
    private val binding get() = _binding!!
    private lateinit var myCropAdapter: MyCropsAdapter
    private val viewModel by lazy { ViewModelProvider(requireActivity())[MainViewModel::class.java] }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFarmDetails2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewClick()
        initMyObserve()
        initObserveDevice()
        myCrop()



    }
    private fun myCrop() {
        myCropAdapter = MyCropsAdapter(MyCropsAdapter.DiffCallback.OnClickListener {
//            val intent = Intent(activity, IrrigationPlannerActivity::class.java)
//            startActivity(intent)
        })
        binding.rvMyCrops.adapter = myCropAdapter
        viewModel.getUserDetails().observe(viewLifecycleOwner) { it ->
            if (it.data != null) {
                var accountId: Int? = null

                accountId = it.data?.accountId

//                var accountId: Int = it.data!!.account[0].id!!
                if (accountId != null)
                    viewModel.getMyCrop2(accountId).observe(viewLifecycleOwner) {
                        Log.d("MyCrops", "myCrop: ${it.data}")
                        myCropAdapter.submitList(it.data)
                        if ((it.data != null)) {
                            binding.tvCount.text = it.data!!.size.toString()
                        } else {
                            binding.tvCount.text = "0"
                        }
                        if (it.data!!.isNotEmpty()) {
                            binding.cvEditCrop.visibility = View.VISIBLE
                            binding.cardAddForm.visibility = View.GONE
                        } else {
                            binding.cvEditCrop.visibility = View.GONE
                            binding.cardAddForm.visibility = View.VISIBLE
                        }
                        if (it.data?.size!! < 8) {
                            binding.addLl.visibility = View.VISIBLE
                        } else binding.addLl.visibility = View.GONE
                    }
            }
        }
    }

    private fun initObserveDevice() {

    }

    private fun initMyObserve() {

    }

    private fun initViewClick() {

    }


}