package com.example.soiltesting.ui.checksoil

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentCheckSoilTestBinding
import com.example.soiltesting.model.checksoil.Data
import com.example.soiltesting.utils.Constant
import okhttp3.internal.notifyAll
import java.util.ArrayList

class CheckSoilTestFragment : Fragment(), CheckSoilTestListener {
    private var _binding: FragmentCheckSoilTestBinding? = null
    private val binding get() = _binding!!
    private var soilTestingLabsAdapter = SoilTestingLabsAdapter(this)

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckSoilTestBinding.inflate(inflater, container, false)
        var your_list  = arguments?.getParcelableArrayList<Data>("list")
        Log.d("TAG", "onCreateViewGettingList: ${your_list.toString()}")
        binding.recyclerviewStatusLab.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewStatusLab.adapter = soilTestingLabsAdapter
        soilTestingLabsAdapter.setMovieList(your_list!!)
        soilTestingLabsAdapter.notifyDataSetChanged()
//        binding.cardCheckHealth.setOnClickListener {
//            val bundle=Bundle()
//            bundle.putString("onp_id",your_list[0].onp_name.toString())
//            Log.d(Constant.TAG, "initViewsendingId: "+your_list[0].onp_name.toString())
//
//            findNavController().navigate(R.id.action_checkSoilTestFragment_to_newSoilTestFormFragment,bundle)
//
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewBackClick()

    }


    private fun initViewBackClick() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
//            soilTestingLabsAdapter.upDateList()
        }
    }

    private fun onBottomButtonBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }

        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun checkBoxSoilTest(data: Data) {
        binding.cardCheckHealth.isEnabled=true
        binding.cardCheckHealth.setOnClickListener {
            soilTestingLabsAdapter.upDateList()
            val bundle = Bundle()
            bundle.putString("soil_test_number",data.onp_name.toString())
            findNavController().navigate(R.id.action_checkSoilTestFragment_to_newSoilTestFormFragment,bundle)
//            soilTestingLabsAdapter.upDateList(ArrayList(data))


        }

    }

}