package com.example.mandiprice.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mandiprice.R
import com.example.mandiprice.adapter.DistanceAdapter
import com.example.mandiprice.databinding.FragmentSearchBinding
import com.example.mandiprice.viewModel.MandiViewModel
import com.google.android.material.tabs.TabLayout
import com.waycool.data.Local.LocalSource
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.SpeechToText
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
    private var lat: String? = null
    private var long: String? = null
    private var accountId = 0
    private lateinit var distance: String
    private lateinit var price: String
    private var handler: Handler? = null
    private val REQUEST_CODE_SPEECH_INPUT = 11019
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
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            lat = it.data?.profile?.lat.toString()
            long = it.data?.profile?.long.toString()
            accountId = it.data?.accountId!!
        }
        binding.topAppBar.setNavigationOnClickListener {
            this.findNavController()
                .navigateUp()
        }
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    this@SearchFragment.findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )
        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
//        binding.searchBar.setOnEditorActionListener(object : TextView.OnEditorActionListener {
//            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    Log.d("search", "onEditorAction: Working")
//                    callingData()
//                    return true
//                }
//                return false
//            }
//        })
        translation()
        searchView()
        EventClickHandling.calculateClickEvent("Mandi_graph_landing")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycleViewDis.layoutManager = LinearLayoutManager(requireContext())
        viewModel.viewModelScope.launch {
            adapterMandi = DistanceAdapter(DistanceAdapter.DiffCallback.OnClickListener {
                val bundle = Bundle()
                bundle.putString("", "Mandi${it.crop}")
                bundle.putString("", "Mandi${it.market}")
                EventItemClickHandling.calculateItemClickEvent("Mandi_landing", bundle)
                val args = Bundle()
//            it?.crop_master_id?.let { it1 -> args.putInt("cropId", it1) }
//            it?.mandi_master_id?.let { it1 -> args.putInt("mandiId", it1) }
//            adapterMandi.cropName.let { it1 -> args.putString("cropName", it1) }
//            adapterMandi.marketName.let { it1 -> args.putString("market", it1) }
//            it?.sub_record_id?.let { it1->args.putString("sub_record_id",it1) }
                args.putParcelable("mandiRecord", it)
                findNavController()
                    .navigate(R.id.action_searchFragment_to_mandiGraphFragment, args)
            }, LocalSource.getLanguageCode() ?: "en")
            binding.recycleViewDis.adapter = adapterMandi
        }
        filterMenu()
        tabs()
        //searchView()
        autoComplete()
        speechToText()
        showKeypad(binding.searchBar)
        dateFormat()
        binding.searchBar.setOnClickListener() {
            EventClickHandling.calculateClickEvent("Mandi_search")
        }
    }

    private fun speechToText() {
        //val lang = SpeechToText.langCode
        binding.SpeechtextTo.setOnClickListener() {
            binding.searchBar.text.clear()
            EventClickHandling.calculateClickEvent("Mandi_STT")
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            // on below line we are passing language model
            // and model free form in our intent
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
            )

            // on below line we are passing our
            // language as a default language.
            viewModel.viewModelScope.launch {
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, SpeechToText.getLangCode())
            }


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
        // SpeechToText.speechToText(requireContext())
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
        viewModel.viewModelScope.launch {
            distance = TranslationsManager().getString("distance")
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText(distance).setCustomView(R.layout.item_tab)
            )
            price = TranslationsManager().getString("Price")
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText(price).setCustomView(R.layout.item_tab)
            )
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (binding.tabLayout.selectedTabPosition) {
                    0 -> {
                        if (binding.filter.text == "Sort by") {
                            orderBy = "distance"
                            sortBy = "asc"
                            if (search.isNullOrEmpty()) {
                                submitAdapter()
                            } else {
                                submitAdapter()
                            }
                        } else {
                            orderBy = "distance"
                            if (search.isNullOrEmpty()) {
                                submitAdapter()
                            } else {
                                submitAdapter()
                            }
                        }

                    }
                    1 -> {
                        //  Toast.makeText(context, "WORKED2", Toast.LENGTH_SHORT).show()
                        if (binding.filter.text == "Sort by") {
                            orderBy = "price"
                            sortBy = "desc"
                            if (search.isNullOrEmpty()) {
                                submitAdapter()
                            } else {
                                binding.recycleViewDis.adapter = adapterMandi
                                submitAdapter()
                            }
                        } else {
                            orderBy = "price"
                            if (search.isNullOrEmpty()) {
                                submitAdapter()
                            } else {
                                submitAdapter()
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
        handler = Handler(Looper.myLooper()!!)
        val searchRunnable =
            Runnable {
                submitAdapter()
            }
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                search = binding.searchBar.text.toString()
                //binding.filter.text = "Sort By"
//                if (!search.isNullOrEmpty()) {
//                    binding.recycleViewDis.adapter = adapterMandi
//                    handler!!.removeCallbacks(searchRunnable)
//                    handler!!.postDelayed(searchRunnable, 1000)
//                }
//
//                if (search.isNullOrEmpty()) {
//                    binding.recycleViewDis.adapter = adapterMandi
//                    handler!!.removeCallbacks(searchRunnable)
//                    handler!!.postDelayed(searchRunnable, 1000)
//                }

                binding.recycleViewDis.adapter = adapterMandi
                handler!!.removeCallbacks(searchRunnable)
                handler!!.postDelayed(searchRunnable, 1000)
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
                        if (item.isChecked) {
                            item.setChecked(false)
                        } else {
                            item.setChecked(true)
                        }
                        sortBy = "asc"
                        binding.filter.text = "Low to High"
                        if (search == null) {
                            submitAdapter()
                        } else {
                            submitAdapter()
                        }

                    }
                    R.id.action_ftbal -> {
                        if (item.isChecked) item.setChecked(false)
                        else item.setChecked(true)
                        item.isChecked = true
                        sortBy = "desc"
                        if (search == null) {
                            submitAdapter()
                        } else {
                            submitAdapter()
                        }
                        binding.filter.text = "High to Low"

                    }
                }
                true
            })
            popupMenu.show()
        }
    }

    private fun dateFormat() {
        adapterMandi.addLoadStateListener { loadState ->
            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapterMandi.itemCount < 1) {
                binding.llNotFound.visibility = View.VISIBLE
                binding.recycleViewDis.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
            } else if (loadState.source.refresh is LoadState.Loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.llNotFound.visibility = View.GONE
                binding.recycleViewDis.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
        val sdf = SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(Date())
        viewModel.viewModelScope.launch {
            val today = TranslationsManager().getString("str_today")
            binding.textView2.text = "$today $sdf"
        }

    }


    fun autoComplete() {
        viewModel.getMandiMaster().observe(viewLifecycleOwner) {
            val marketName = it.data?.data?.map { data ->
                data.mandiName
            } ?: emptyList()
            viewModel.getAllCrops().observe(viewLifecycleOwner) {
                val cropName = it?.data?.map { data ->
                    data.cropName
                } ?: emptyList()


                val list = cropName + marketName
                Log.d("autoList", "autoComplete: $marketName")
                val arrayAdapter =
                    ArrayAdapter<String>(requireContext(), R.layout.item_auto_complete, list)
                binding.searchBar.setAdapter(arrayAdapter)
            }
        }
    }

//    override fun onStart() {
//        super.onStart()
//        binding.searchBar.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
//            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                search = binding.searchBar.text.toString()
//                    //binding.filter.text = "Sort By"
//                        if (!search.isNullOrEmpty())
//                            binding.recycleViewDis.adapter = adapterMandi
//                submitAdapter()
//                        if (binding.searchBar.text.isNullOrEmpty())
//                            binding.recycleViewDis.adapter = adapterMandi
//                submitAdapter()
//            }
//
//            override fun afterTextChanged(editable: Editable) {
//                //after the change calling the method and passing the search input
//                // viewModel.getSearch(editable.toString())
//
//            }
//        })
//
////        callingData()
//    }

    private fun showKeypad(editText: EditText) {
        val imm: InputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText.rootView, InputMethodManager.SHOW_IMPLICIT)
        editText.requestFocus()
    }

    fun callingData() {
        search = binding.searchBar.text.toString()
        //binding.filter.text = "Sort By"
        if (!search.isNullOrEmpty())
            submitAdapter()
    }

    fun submitAdapter() {
        binding.recycleViewDis.adapter = adapterMandi
        if (lat != null && long != null)
            viewModel.getMandiDetails(

                lat!!,
                long!!, cropCategory,
                state,
                crop,
                sortBy,
                orderBy,
                search, accountId
            ).observe(viewLifecycleOwner) {
                // binding.viewModel = it
                adapterMandi.submitData(lifecycle, it)
                // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
            }
    }

    private fun translation() {
        var mandi = "Market Prices"
        viewModel.viewModelScope.launch {
            mandi = TranslationsManager().getString("mandi_price")
            binding.topAppBar.title = mandi
            var hint = TranslationsManager().getString("search_crop_mandi")
            binding.searchBar.hint = hint
        }
        TranslationsManager().loadString("sort_by", binding.filter, "Sort By")

    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("SearchFragment")
    }
}