package com.example.mandiprice.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mandiprice.R
import com.example.mandiprice.adapter.DistanceAdapter
import com.example.mandiprice.databinding.FragmentSearchBinding
import com.example.mandiprice.viewModel.MandiViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapterMandi: DistanceAdapter
    private val viewModel: MandiViewModel by lazy {
        ViewModelProviders.of(this).get(MandiViewModel::class.java)
    }
    private var sortBy: String = "asc"
    private var orderBy: String = "distance"
    private var search: String? = null
    private var cropCategory: String? = null
    private var state: String? = null
    private var crop: String? = null
    private val REQUEST_CODE_SPEECH_INPUT = 1
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
        binding = FragmentSearchBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.topAppBar.setNavigationOnClickListener {
            this.findNavController()
                .navigate(SearchFragmentDirections.actionSearchFragmentToMandiFragment())
        }
        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycleViewDis.layoutManager = LinearLayoutManager(requireContext())
        adapterMandi = DistanceAdapter(DistanceAdapter.DiffCallback.OnClickListener {
            val args = Bundle()
            it?.crop_master_id?.let { it1 -> args.putInt("cropId", it1) }
            it?.mandi_master_id?.let { it1 -> args.putInt("mandiId", it1) }
            it?.crop?.let { it1 -> args.putString("cropName", it1) }
            it?.market?.let { it1 -> args.putString("market", it1) }
            this.findNavController()
                .navigate(R.id.action_searchFragment_to_mandiGraphFragment, args)
        })
        binding.recycleViewDis.adapter = adapterMandi
        viewModel.viewModelScope.launch {
            viewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy, search)
                .observe(viewLifecycleOwner) {
                    // binding.viewModel = it
                    adapterMandi.submitData(lifecycle, it)
                    // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                }
        }
        filterMenu()
        tabs()
        searchView()
        autoComplete()
        speechToText()
        showKeypad(binding.searchBar)
        //notFound()
    }

    private fun speechToText() {
        binding.SpeechtextTo.setOnClickListener() {
            binding.searchBar.text.clear()
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            // on below line we are passing language model
            // and model free form in our intent
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
            )

            // on below line we are passing our
            // language as a default language.
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE, "en-IN"
            )

            // on below line we are specifying a prompt
            // message as speak to text on below line.
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            // on below line we are specifying a try catch block.
            // in this block we are calling a start activity
            // for result method and passing our result code.
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                // on below line we are displaying error message in toast
//                Toast
//                    .makeText(
//                        context, " " + e.message,
//                        Toast.LENGTH_SHORT
//                    )
//                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // in this method we are checking request
        // code with our result code.
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            // on below line we are checking if result code is ok
            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {

                // in that case we are extracting the
                // data from our array list
                val res: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>

                // on below line we are setting data
                // to our output text view.
                binding.searchBar.setText(
                    Objects.requireNonNull(res)[0]
                )
            }
        }
    }


    private fun tabs() {

        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("Distance").setCustomView(R.layout.item_tab)
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("Price").setCustomView(R.layout.item_tab)
        )
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (binding.tabLayout.selectedTabPosition) {
                    0 -> {
                        if (binding.filter.text == "Sort by") {
                            orderBy = "distance"
                            sortBy = "asc"
                            if (search.isNullOrEmpty()) {
                                binding.recycleViewDis.adapter = adapterMandi
                                viewModel.viewModelScope.launch {
                                    viewModel.getMandiDetails(
                                        cropCategory,
                                        state,
                                        crop,
                                        sortBy,
                                        orderBy,
                                        search
                                    ).observe(viewLifecycleOwner) {
                                        // binding.viewModel = it
                                        adapterMandi.submitData(lifecycle, it)
                                        // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                binding.recycleViewDis.adapter = adapterMandi
                                viewModel.viewModelScope.launch {
                                    viewModel.getMandiDetails(
                                        cropCategory,
                                        state,
                                        crop,
                                        sortBy,
                                        orderBy,
                                        search
                                    ).observe(viewLifecycleOwner) {
                                        // binding.viewModel = it
                                        adapterMandi.submitData(lifecycle, it)
                                        // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            orderBy = "distance"
                            if (search.isNullOrEmpty()) {
                                binding.recycleViewDis.adapter = adapterMandi
                                viewModel.viewModelScope.launch {
                                    viewModel.getMandiDetails(
                                        cropCategory,
                                        state,
                                        crop,
                                        sortBy,
                                        orderBy,
                                        search
                                    ).observe(viewLifecycleOwner) {
                                        // binding.viewModel = it
                                        adapterMandi.submitData(lifecycle, it)
                                        // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                binding.recycleViewDis.adapter = adapterMandi
                                viewModel.viewModelScope.launch {
                                    viewModel.getMandiDetails(
                                        cropCategory,
                                        state,
                                        crop,
                                        sortBy,
                                        orderBy,
                                        search
                                    ).observe(viewLifecycleOwner) {
                                        // binding.viewModel = it
                                        adapterMandi.submitData(lifecycle, it)
                                        // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                    }
                    1 -> {
                        //  Toast.makeText(context, "WORKED2", Toast.LENGTH_SHORT).show()
                        if (binding.filter.text == "Sort by") {
                            orderBy = "price"
                            sortBy = "desc"
                            if (search.isNullOrEmpty()) {
                                binding.recycleViewDis.adapter = adapterMandi
                                viewModel.viewModelScope.launch {
                                    viewModel.getMandiDetails(
                                        cropCategory,
                                        state,
                                        crop,
                                        sortBy,
                                        orderBy,
                                        search
                                    ).observe(viewLifecycleOwner) {
                                        // binding.viewModel = it
                                        adapterMandi.submitData(lifecycle, it)
                                        // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                binding.recycleViewDis.adapter = adapterMandi
                                viewModel.viewModelScope.launch {
                                    viewModel.getMandiDetails(
                                        cropCategory,
                                        state,
                                        crop,
                                        sortBy,
                                        orderBy,
                                        search
                                    ).observe(viewLifecycleOwner) {
                                        // binding.viewModel = it
                                        adapterMandi.submitData(lifecycle, it)
                                        // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            orderBy = "price"
                            if (search.isNullOrEmpty()) {
                                binding.recycleViewDis.adapter = adapterMandi
                                viewModel.viewModelScope.launch {
                                    viewModel.getMandiDetails(
                                        cropCategory,
                                        state,
                                        crop,
                                        sortBy,
                                        orderBy,
                                        search
                                    ).observe(viewLifecycleOwner) {
                                        // binding.viewModel = it
                                        adapterMandi.submitData(lifecycle, it)
                                        // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                binding.recycleViewDis.adapter = adapterMandi
                                viewModel.viewModelScope.launch {
                                    viewModel.getMandiDetails(
                                        cropCategory,
                                        state,
                                        crop,
                                        sortBy,
                                        orderBy,
                                        search
                                    ).observe(viewLifecycleOwner) {
                                        // binding.viewModel = it
                                        adapterMandi.submitData(lifecycle, it)
                                        // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun searchView() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                search = binding.searchBar.text.toString()
                if (i2 > 2) {
                    //binding.filter.text = "Sort By"
                    Handler().postDelayed({
                        if (!search.isNullOrEmpty())
                            binding.recycleViewDis.adapter = adapterMandi
                        viewModel.viewModelScope.launch {
                            viewModel.getMandiDetails(
                                cropCategory,
                                state,
                                crop,
                                sortBy,
                                orderBy,
                                search
                            ).observe(viewLifecycleOwner) {
                                // binding.viewModel = it
                                adapterMandi.submitData(lifecycle, it)
                                // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                            }
                        }
                        if (binding.searchBar.text.isNullOrEmpty())
                            binding.recycleViewDis.adapter = adapterMandi
                        viewModel.viewModelScope.launch {
                            viewModel.getMandiDetails(
                                cropCategory,
                                state,
                                crop,
                                sortBy,
                                orderBy,
                                search
                            ).observe(viewLifecycleOwner) {
                                // binding.viewModel = it
                                adapterMandi.submitData(lifecycle, it)
                                // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }, 1500)
                }
            }

            override fun afterTextChanged(editable: Editable) {
                //after the change calling the method and passing the search input
                // viewModel.getSearch(editable.toString())

            }
        })
    }

    private fun filterMenu() {
        binding.filter.setOnClickListener() {
            val popupMenu = PopupMenu(context, binding.filter)
            popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)


            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_crick -> {
                        if(item.isChecked){ item.setChecked(false)}
                        else {item.setChecked(true)}
                        sortBy = "asc"
                        binding.filter.text = "Low to High"
                        if (search == null) {
                            binding.recycleViewDis.adapter = adapterMandi
                            viewModel.viewModelScope.launch {
                                viewModel.getMandiDetails(
                                    cropCategory,
                                    state,
                                    crop,
                                    sortBy,
                                    orderBy,
                                    search
                                ).observe(viewLifecycleOwner) {
                                    adapterMandi.submitData(lifecycle, it)
                                }
                            }
                        } else {
                            binding.recycleViewDis.adapter = adapterMandi
                            viewModel.viewModelScope.launch {
                                viewModel.getMandiDetails(
                                    cropCategory,
                                    state,
                                    crop,
                                    sortBy,
                                    orderBy,
                                    search
                                ).observe(viewLifecycleOwner) {
                                    adapterMandi.submitData(lifecycle, it)
                                }
                            }
                        }

                    }
                    R.id.action_ftbal -> {
                        if(item.isChecked) item.setChecked(false)
                        else item.setChecked(true)
                        item.isChecked = true
                        sortBy = "desc"
                        if (search == null) {
                            binding.recycleViewDis.adapter = adapterMandi
                            viewModel.viewModelScope.launch {
                                viewModel.getMandiDetails(
                                    cropCategory,
                                    state,
                                    crop,
                                    sortBy,
                                    orderBy,
                                    search
                                ).observe(viewLifecycleOwner) {
                                    adapterMandi.submitData(lifecycle, it)
                                }
                            }
                        } else {
                            viewModel.viewModelScope.launch {
                                binding.recycleViewDis.adapter = adapterMandi
                                viewModel.getMandiDetails(
                                    cropCategory,
                                    state,
                                    crop,
                                    sortBy,
                                    orderBy,
                                    search
                                ).observe(viewLifecycleOwner) {
                                    adapterMandi.submitData(lifecycle, it)
                                }
                            }
                        }
                        binding.filter.text = "High to Low"

                    }
                }
                true
            })
            popupMenu.show()
        }
    }

    private fun notFound() {
        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                "true" -> {
                    binding.llNotFound.visibility = View.GONE
                    binding.recycleViewDis.visibility = View.VISIBLE
                }
                "Failed" -> {
                    binding.llNotFound.visibility = View.VISIBLE
                    binding.recycleViewDis.visibility = View.GONE
                }
            }
            //Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
            Log.d("status", "onClick:$it ")
        }
        val sdf = SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(Date())
        binding.textView2.text = "Today $sdf"
    }

    fun autoComplete() {
        viewModel.getAllCrops().observe(viewLifecycleOwner){
            val cropName = it?.data?.map { data ->
                data.cropName
            }?: emptyList()
        val text = resources.getStringArray(R.array.autoComplete)
        val arrayAdapter = ArrayAdapter<String>(requireContext(), R.layout.item_auto_complete,cropName)
        binding.searchBar.setAdapter(arrayAdapter)
    }}

    override fun onStart() {
        super.onStart()
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                search = binding.searchBar.text.toString()
                if (i2 > 2) {
                    //binding.filter.text = "Sort By"
                    Handler().postDelayed({
                        if (!search.isNullOrEmpty())
                            binding.recycleViewDis.adapter = adapterMandi
                        viewModel.viewModelScope.launch {
                            viewModel.getMandiDetails(
                                cropCategory,
                                state,
                                crop,
                                sortBy,
                                orderBy,
                                search
                            ).observe(viewLifecycleOwner) {
                                // binding.viewModel = it
                                adapterMandi.submitData(lifecycle, it)
                                // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                            }
                        }
                        if (binding.searchBar.text.isNullOrEmpty())
                            binding.recycleViewDis.adapter = adapterMandi
                        viewModel.viewModelScope.launch {
                            viewModel.getMandiDetails(
                                cropCategory,
                                state,
                                crop,
                                sortBy,
                                orderBy,
                                search
                            ).observe(viewLifecycleOwner) {
                                // binding.viewModel = it
                                adapterMandi.submitData(lifecycle, it)
                                // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }, 1500)
                }
            }

            override fun afterTextChanged(editable: Editable) {
                //after the change calling the method and passing the search input
                // viewModel.getSearch(editable.toString())

            }
        })
    }

    private fun showKeypad(editText: EditText){
   val imm:InputMethodManager= context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText.rootView,InputMethodManager.SHOW_IMPLICIT)
        editText.requestFocus()
    }
}