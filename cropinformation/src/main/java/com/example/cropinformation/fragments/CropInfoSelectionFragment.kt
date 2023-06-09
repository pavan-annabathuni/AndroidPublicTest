package com.example.cropinformation.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.cropinformation.R
import com.example.cropinformation.adapter.CropListAdapter
import com.example.cropinformation.adapter.MyCropsAdapter
import com.example.cropinformation.databinding.FragmentCropSelectionInfoBinding
import com.example.cropinformation.viewModle.CropInfoViewModel
import com.google.android.material.chip.Chip
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.CropCategoryMasterDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.Resource
import com.waycool.data.utils.SpeechToText
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CropInfoSelectionFragment : Fragment() {
    private var selectedCategory: CropCategoryMasterDomain? = null
    private lateinit var binding: FragmentCropSelectionInfoBinding
    private val viewModel: CropInfoViewModel by lazy {
        ViewModelProvider(requireActivity())[CropInfoViewModel::class.java]
    }
    private lateinit var myCropAdapter: MyCropsAdapter
    private val adapter: CropListAdapter by lazy { CropListAdapter() }
    // private val myCropAdapter: MyCropsAdapter by lazy { MyCropsAdapter() }

    private var handler: Handler? = null

    private var searchCharSequence: CharSequence? = ""
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
        binding = FragmentCropSelectionInfoBinding.inflate(inflater)
        translation()
        EventClickHandling.calculateClickEvent("cropinformation_landing")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.toolbar.setNavigationOnClickListener {
            activity?.finish()
        }
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                  activity?.let { it.finish()}
                }
            }
        activity?.let {
            it.onBackPressedDispatcher.addCallback(
                it,
                callback
            )
        }
        translation()

        binding.cropsRv.adapter = adapter
        myCropAdapter = MyCropsAdapter(MyCropsAdapter.DiffCallback.OnClickListener {
            val bundle = Bundle()
            bundle.putString("", it.cropNameTag)
            bundle.putString("","CropInfoSelectionFragment")
            EventItemClickHandling.calculateItemClickEvent("cropinformation_landing",bundle)

            var id = it.idd
            var id2:Int? = null
            val args = Bundle()
            it.idd?.let { it1 -> args.putInt("cropid", it1) }
            it.cropName?.let { it1 -> args.putString("cropname", it1) }
            it.cropLogo?.let { it1 -> args.putString("cropLogo", it1) }
            viewModel.getCropMaster().observe(viewLifecycleOwner){
                for (i in 0 until it.data?.size!!){
                    if(it.data?.get(i)?.cropId==id) {
                        id2 = it.data?.get(i)?.cropId!!
                        Log.d("CropId", "onViewCreated: ${id} ${it.data?.get(i)?.cropId}")
                        break
                    }
                }
            if(id==id2){
            findNavController().navigate(
                R.id.action_cropSelectionFragment_to_cropInfoFragment,
                args
            )}else{
                dialog()
            }}
        })
        binding.rvMyCrops.adapter = myCropAdapter

        fabButton()
        myCrops()

        handler = Handler(Looper.myLooper()!!)
        val searchRunnable =
            Runnable {
                getSelectedCategoryCrops(
                  //  categoryId = selectedCategory?.id,
                    searchQuery = searchCharSequence.toString()
                )
            }

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if(charSequence.isEmpty()){
                    setUpCropCategories()
                }
                searchCharSequence = charSequence
                handler!!.removeCallbacks(searchRunnable)
                handler!!.postDelayed(searchRunnable, 150)
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putString("","${it?.cropNameTag}")
            bundle.putString("","Crop_category_${selectedCategory?.categoryTagName}")
            EventItemClickHandling.calculateItemClickEvent("cropInfo_landing",bundle)
            val args = Bundle()
            it?.cropId?.let { it1 -> args.putInt("cropid", it1) }
            it?.cropName?.let { it1 -> args.putString("cropname", it1) }
            it?.cropLogo?.let { it1 -> args.putString("cropLogo", it1) }
            findNavController().navigate(
                R.id.action_cropSelectionFragment_to_cropInfoFragment,
                args
            )

        }

        binding.micBtn.setOnClickListener {
            speechToText()
        }
        setUpCropCategories()
    }

    private fun setUpCropCategories() {
        viewModel.getCropCategory().observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
//                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                    binding.cropCategoryChipGroup.removeAllViews()
                    selectedCategory = null
                    val categoryList = it.data
                    val allCategory = CropCategoryMasterDomain(categoryName = "All")
                    createChip(allCategory)
                    if (categoryList != null) {
                        for (category in categoryList) {
                            createChip(category)
                        }
                    }
                }
                is Resource.Error -> {
                    AppUtils.translatedToastServerErrorOccurred(context)

                }
                is Resource.Loading -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        val toastLoading = TranslationsManager().getString("loading")
                        if(!toastLoading.isNullOrEmpty()){
                            context?.let { it1 -> ToastStateHandling.toastError(it1,toastLoading,
                                Toast.LENGTH_SHORT
                            ) }}
                        else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Loading",
                            Toast.LENGTH_SHORT
                        ) }}}
                }
                else -> {}
            }
        }
    }

    private fun createChip(category: CropCategoryMasterDomain) {
        if(activity!=null) {
            val chip = Chip(activity)
            chip.text = category.categoryName
            chip.isCheckable = true
            chip.isClickable = true
            chip.isCheckedIconVisible = false
            chip.setTextColor(
                AppCompatResources.getColorStateList(
                    requireContext(),
                    com.waycool.uicomponents.R.color.bg_chip_text
                )
            )
            chip.setChipBackgroundColorResource(com.waycool.uicomponents.R.color.chip_bg_selector)
            chip.chipStrokeWidth = 1f
            chip.chipStrokeColor = AppCompatResources.getColorStateList(
                requireContext(),
                com.waycool.uicomponents.R.color.bg_chip_text
            )

            if (selectedCategory == null) {
                chip.isChecked = true
                selectedCategory = category
                getSelectedCategoryCrops(
                    categoryId = category.id,
                    searchQuery = searchCharSequence.toString()
                )
            }


            chip.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
                if (b) {
                    selectedCategory = category
                    getSelectedCategoryCrops(
                        categoryId = category.id,
                        searchQuery = searchCharSequence.toString()
                    )
                }
                //  EventClickHandling.calculateClickEvent(chip.text.toString())
            }
            binding.cropCategoryChipGroup.addView(chip)
        }
    }

    private fun getSelectedCategoryCrops(categoryId: Int? = null, searchQuery: String? = "") {
        viewModel.getCropMaster(searchQuery).observe(requireActivity()) { res ->
            when (res) {
                is Resource.Success -> {
                    if(!res.data.isNullOrEmpty()){
                        binding.progressBar.visibility=View.GONE
                    }
                    if (categoryId == null) {
                        adapter.submitList(res.data)
                    } else
                        adapter.submitList(res.data?.filter { it.cropCategory_id == categoryId })
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility=View.VISIBLE
                }
                is Resource.Error -> {
                    AppUtils.translatedToastServerErrorOccurred(context)
                }
                else -> {}
            }
        }
    }


    private fun speechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        viewModel.viewModelScope.launch {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, SpeechToText.getLangCode())
        }
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast
                .makeText(requireActivity(), " " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                searchCharSequence = result?.get(0).toString()
                binding.search.setText(searchCharSequence)

            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 1
    }

    private fun fabButton() {
        var isVisible = false
        binding.addFab.setOnClickListener {
            if (!isVisible) {
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                    com.waycool.uicomponents.R.drawable.ic_cross
                ))
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            } else {
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),com.waycool.uicomponents.R.drawable.ic_chat_call))
                binding.addFab.isExpanded = false
                isVisible = false
            }
        }
        binding.addCall.setOnClickListener {
            EventClickHandling.calculateClickEvent("call_icon")
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.addChat.setOnClickListener {
            EventClickHandling.calculateClickEvent("chat_icon")

            FeatureChat.zenDeskInit(requireContext())
        }
    }

    private fun myCrops() {
        viewModel.getMyCrop2().observe(viewLifecycleOwner) {
            myCropAdapter.submitList(it.data)
            if ((it.data != null)) {
                binding.tvCount.text = it.data!!.size.toString()
            } else {
                binding.tvCount.text = "0"
            }
            // Log.d("MYCROPS", it.data?.get(0)?.cropLogo.toString())

        }

    }

    private fun translation(){
        var title:String
            TranslationsManager().loadString("str_description",binding.textView )
            TranslationsManager().loadString("str_mycrops",binding.title3SemiBold )
        TranslationsManager().loadString("str_title",binding.toolbarTitle )
        viewModel.viewModelScope.launch{
            binding.search.hint = TranslationsManager().getString("search")
        }
    }
    private fun dialog(){

        val dialog = Dialog(requireContext())
        //dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_crop_info)
        // val body = dialog.findViewById(R.id.body) as TextView
        val yesBtn = dialog.findViewById(R.id.ok) as Button
        val tvInformation = dialog.findViewById(R.id.textView14)as TextView
        val tvMessage = dialog.findViewById(R.id.textView15)as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
            Log.d("Dialog", "dialog: Clicked")
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        TranslationsManager().loadString("str_information",tvInformation,"Information")
        TranslationsManager().loadString("crop_info_message",tvMessage,"Thanks for showing your interest. Currently, we’re working on a Crop Information for this crop. We look forward to serving you shortly")
        viewModel.viewModelScope.launch {
            var ok = TranslationsManager().getString("str_ok")
            if(ok.isNullOrEmpty())
                yesBtn.text = "Ok"
        }
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("CropInfoSelectionFragment")
    }
}