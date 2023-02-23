package com.example.soiltesting.ui.request


import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soiltesting.FileProvider
import com.example.soiltesting.databinding.FragmentViewReportBinding
import com.waycool.data.Network.NetworkModels.Recommendation
import com.waycool.data.Network.NetworkModels.ReportDetails
import com.waycool.data.Network.NetworkModels.ReportResult
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.*


class ViewReportFragment : Fragment() {
    private var _binding: FragmentViewReportBinding? = null
    private val binding get() = _binding!!

    var language = ""
    var id: String? = ""
    var header: String? = ""
    var soilConditioner: String? = ""
    var recommendation: String? = ""
    var macronutrient: String? = ""
    var micronutrient: String? = ""
    var scale: String? = ""
    var scale_text1: String? = ""
    var scale_text2: String? = ""
    var scale_text3 = ""
    var scale_text4: String? = ""
    var scale_text5: String? = ""
    var scale_text6: String? = ""
    var sample_info: String? = ""
    var farmer: String? = ""
    var testCenter: String? = ""
    var selected_crop = ""
    var plot_size: String? = ""
    var location: String? = ""
    var survey_no: String? = ""
    var sampling_date: String? = ""
    var test_date: String? = ""
    var name: String? = ""
    var address: String? = ""
    var mobile: String? = ""
    var soilReportResultAdapter: SoilReportResultAdapter? = null
    var soilConditionerAdapter: SoilReportRecommendationAdapter? = null
    var macroAdapter: SoilReportRecommendationAdapter? = null
    var microAdapter: SoilReportRecommendationAdapter? = null
    var soilConditionerList: ArrayList<Recommendation> = ArrayList<Recommendation>()
    var macroList: ArrayList<Recommendation> = ArrayList<Recommendation>()
    var microList: ArrayList<Recommendation> = ArrayList<Recommendation>()
    var fullList: List<Recommendation> = ArrayList<Recommendation>()
    var fullData: ReportDetails? = null
    var resultList: List<ReportResult> = ArrayList<ReportResult>()
    var trackerID: Int? = null

    //    var soilViewModel: SoilViewModel? = null
    private val viewModel by lazy { ViewModelProvider(this)[SoilTestRequestViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        soilViewModel = ViewModelProvider(this).get(SoilViewModel::class.java)
//        if (getIntent().getStringExtra("request_id") != null)
//            header = getIntent().getStringExtra("request_id")
//        if (getIntent().getStringExtra("id") != null) id = getIntent().getStringExtra("id")


        language = "1"
        soilConditioner = "Soil Conditioner / Amendment"
        recommendation = "Recommendation :"
        macronutrient = "Macronutrient Fertilizer (in Kg/Bag)"
        micronutrient = "Micronutrient Fertilizer"
        scale = "Scale"
        scale_text1 = "High, Acid, Saline,Highly Alkaline," + "Low (OC,OM)"
        scale_text2 = "Low," + " Deficient"
        scale_text3 = "Medium ,Normal"
        scale_text4 = "Neutral,Sufficient, Alkaline," + "Slightly Acidic"
        scale_text5 = "High (OC,OM,mbc)"
        scale_text6 = "Not selected"
        sample_info = "Sample Information"
        farmer = "Farmer Details"
        testCenter = "Test Center"
        name = "Name - "
        address = "Address - "
        mobile = "Mobile - "
        selected_crop = "Selected Crop - "
        survey_no = "SurveyNo -  "
        plot_size = "Plot Size - "
        location = "Location - "
        sampling_date = "Sampling Date - "
        test_date = "Testing Date - "
        translations()


        binding.soilConditionerText.text = soilConditioner
        binding.recommandationText.text = recommendation
        binding.macronutrientText.text = macronutrient
        binding.micronutrientFertilizerText.text = micronutrient
        binding.scaleLayout.scaleTittle.text = scale
        binding.scaleLayout.scaleTv1.text = scale_text1
        binding.scaleLayout.scaleTv2.text = scale_text2
        binding.scaleLayout.scaleTv3.text = scale_text3
        binding.scaleLayout.scaleTv4.text = scale_text4
        binding.scaleLayout.scaleTv5.text = scale_text5
        binding.scaleLayout.scaleTv6.text = scale_text6
        binding.sampleLayout.othersTittle.text = sample_info
        binding.sampleLayout.soilReportTv1.text = selected_crop
        binding.sampleLayout.soilReportTv2.text = plot_size
        binding.sampleLayout.soilReportTv3.text = location
        binding.sampleLayout.soilReportTv4.text = survey_no
        binding.sampleLayout.soilReportTv5.text = sampling_date
        binding.sampleLayout.soilReportTv6.text = test_date
        binding.farmerLayout.othersTittle.text = farmer
        binding.farmerLayout.soilReportTv1.text = name
        binding.farmerLayout.soilReportTv2.text = address
        binding.farmerLayout.soilReportTv3.text = mobile
        binding.farmerLayout.soilReportTv4.visibility = View.GONE
        binding.farmerLayout.soilReportTv5.visibility = View.GONE
        binding.farmerLayout.soilReportTv6.visibility = View.GONE
        binding.testCenterLayout.othersTittle.text = testCenter
        binding.testCenterLayout.soilReportTv1.text = name
        binding.testCenterLayout.soilReportTv2.text = address
        binding.testCenterLayout.soilReportTv3.text = ""
        binding.testCenterLayout.soilReportTv4.visibility = View.GONE
        binding.testCenterLayout.soilReportTv5.visibility = View.GONE
        binding.testCenterLayout.soilReportTv6.visibility = View.GONE


        soilReportResultAdapter = SoilReportResultAdapter(requireContext(), resultList)
        binding.soilResultRecyclerView.setHasFixedSize(true)
        binding.soilResultRecyclerView.adapter = soilReportResultAdapter
        val gridLayoutManager =
            GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
        binding.soilResultRecyclerView.layoutManager = gridLayoutManager
        Bundle()
        if (arguments != null) {
            trackerID = arguments?.getInt("id")
            val soitestNumber = arguments?.getString("soil_test_number")
            binding.tvToolBar.text = "ID: ${soitestNumber ?: "N/A"}"
            Log.d("Praaasd", "onViewCreatedbdhsvbhdbvhb:$soitestNumber ")
            setData(trackerID!!)

        }
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
        binding.cardCheckHealth.setOnClickListener {
            initObserve(trackerID!!)


        }

    }

    private fun translations() {
//        TranslationsManager().loadString("str_recommendation",binding.tvToolBar,"ID: ")
        TranslationsManager().loadString(
            "str_recommendation",
            binding.recommandationText,
            "Recommendation: "
        )
        TranslationsManager().loadString(
            "str_recommendation",
            binding.soilConditionerText,
            "Recommendation: "
        )
        TranslationsManager().loadString(
            "str_recommendation",
            binding.macronutrientText,
            "Recommendation: "
        )
        TranslationsManager().loadString(
            "str_recommendation",
            binding.micronutrientFertilizerText,
            "Recommendation: "
        )
        TranslationsManager().loadString(
            "str_recommendation",
            binding.soilConditionerText,
            "Recommendation: "
        )
        TranslationsManager().loadString(
            "str_high_acide",
            binding.scaleLayout.scaleTv1,
            scale_text1
        )
//        TranslationsManager().loadString("str_low",binding.scaleLayout.scaleTv2,scale_text2)
        TranslationsManager().loadString("str_medium_oc", binding.scaleLayout.scaleTv3, scale_text3)
        TranslationsManager().loadString(
            "str_medium_neutral",
            binding.scaleLayout.scaleTv4,
            scale_text4
        )
        TranslationsManager().loadString(
            "str_high_oc_om_mbc",
            binding.scaleLayout.scaleTv5,
            scale_text5
        )
        TranslationsManager().loadString("not_selected", binding.scaleLayout.scaleTv6, scale_text6)
        TranslationsManager().loadString("scale", binding.scaleLayout.scaleTittle, scale)


    }


    fun setData(id: Int) {
        viewModel.viewReport(id).observe(requireActivity()) { soilTestReportMaster ->
            when (soilTestReportMaster) {
                is Resource.Success -> {
                    if (soilTestReportMaster.data!!.status) {
                        if (soilTestReportMaster.data!!.data.get(0).results != null) {

                            fullList =
                                soilTestReportMaster.data!!.data.get(0).results.reportData.ferilizerRecomendations
                            if (fullList.isNotEmpty()) {
                                dataSetToRecommendation(fullList)
                            }
                            resultList =
                                soilTestReportMaster.data!!.data.get(0).results.reportData.parameterInfos
                            soilReportResultAdapter!!.updateList(resultList)

                        }
                        if (soilTestReportMaster.data!!.data.get(0) != null) {
                            fullData = soilTestReportMaster.data!!.data.get(0)
                            setOtherData(fullData!!)
                        }
                    }
                }

                is Resource.Error -> {
                    AppUtils.translatedToastServerErrorOccurred(context)
                }
                is Resource.Loading -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        val toastLoading = TranslationsManager().getString("loading")
                        if (!toastLoading.isNullOrEmpty()) {
                            context?.let { it1 ->
                                ToastStateHandling.toastError(
                                    it1, toastLoading,
                                    Toast.LENGTH_SHORT
                                )
                            }
                        } else {
                            context?.let { it1 ->
                                ToastStateHandling.toastError(
                                    it1, "Loading",
                                    Toast.LENGTH_SHORT
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    fun initObserve(soil_test_id: Int) {
        viewModel.pdfDownload(soil_test_id).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    val uri: Uri = writeResponseBodyToDisk(it.data!!)
//                         val intent = Intent()
//                         intent.setAction(Intent.ACTION_VIEW)
//                         intent.setDataAndType(uri, "application/pdf")
//                         startActivity(intent)
                    val i = Intent()
                    i.action = Intent.ACTION_SEND
//i.putExtra(Intent.EXTRA_TEXT,"Title")
                    i.putExtra(Intent.EXTRA_STREAM, uri)
                    i.type = "application/pdf"
                    startActivity(i)

                    Log.d("TAG", "initObserveDataggvsx:$uri")
//
//                    val filename = "gwx.pdf"
////                    val fileUri = writeResponseBodyToDisk(it.data!!
//                    val downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//                    val request = DownloadManager.Request(uri)
//                        .setTitle(filename)
//                        .setDescription("Downloading PDF...")
//                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
//                    downloadManager.enqueue(request)


                }
                is Resource.Error -> {
                    AppUtils.translatedToastServerErrorOccurred(context)
                }
                is Resource.Loading -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        val toastLoading = TranslationsManager().getString("loading")
                        if (!toastLoading.isNullOrEmpty()) {
                            context?.let { it1 ->
                                ToastStateHandling.toastError(
                                    it1, toastLoading,
                                    Toast.LENGTH_SHORT
                                )
                            }
                        } else {
                            context?.let { it1 ->
                                ToastStateHandling.toastError(
                                    it1, "Loading",
                                    Toast.LENGTH_SHORT
                                )
                            }
                        }
                    }
                }
            }

        }
    }
//    private fun writeResponseBodyToDisk(body: ResponseBody, filename: String): String {
//        val file = File(requireContext().externalCacheDir, filename)
//
//        try {
//            val inputStream = body.byteStream()
//            val outputStream = FileOutputStream(file)
//
//            val buffer = ByteArray(4096)
//            var bytesRead: Int
//            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                outputStream.write(buffer, 0, bytesRead)
//            }
//
//            outputStream.flush()
//            outputStream.close()
//            inputStream.close()
//
//            return file.absolutePath
//        } catch (e: IOException) {
//            throw RuntimeException("Error writing file", e)
//        }
//    }


    private fun writeResponseBodyToDisk(body: ResponseBody): Uri {
        return try {

            // todo change the file location/name according to your needs

            //            android.os.Environment.getExternalStorageState();

            //            File test = new File(getExternalCacheDir(), "GWX Invoices");

            //            if (!test.exists()) {

            //                test.mkdirs();

            //            }
            var invoicePdfFile = File(requireContext().externalCacheDir, "gwx.pdf")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize: Long = body.contentLength()
                var fileSizeDownloaded: Long = 0
                Log.d("g56", "Filesize: $fileSize")
                inputStream = body.byteStream()
                outputStream = FileOutputStream(invoicePdfFile)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d("g56", "file download: $fileSizeDownloaded of $fileSize")
                }
                outputStream.flush()
                FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.soiltesting",
                    invoicePdfFile
                )
            } catch (e: IOException) {
//                Log.d("g56", e.getMessage())
//                Uri.fromFile(invoicePdfFile)
                throw RuntimeException(e.message)
//                FileProvider.getUriForFile(requireContext(),"com.example.soiltesting",invoicePdfFile)
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }
                if (outputStream != null) {
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
//            Log.d("g56", e.getMessage())
//            Uri.fromFile(File(""))
            throw RuntimeException(e.message)
//            FileProvider.getUriForFile(requireContext(),"com.example.soiltesting",File(""))
        }
    }

    //
    fun dataSetToRecommendation(list: List<Recommendation>) {
        soilConditionerList.clear()
        macroList.clear()
        microList.clear()
        for (i in list.indices) {
            val name: String = list[i].category
            if (name == "Manure / Amendments") {
                soilConditionerList.add(list[i])
            } else if (name == "Macro Nutrient") {
                macroList.add(list[i])
            } else if (name == "Micro Nutrient") {
                microList.add(list[i])
            }
        }
        if (soilConditionerList.size > 0) {
            soilConditionerAdapter =
                SoilReportRecommendationAdapter(requireContext(), soilConditionerList)
            binding.soilConditionerRecyclerView.setHasFixedSize(true)
            binding.soilConditionerRecyclerView.adapter = soilConditionerAdapter
            binding.soilConditionerRecyclerView.layoutManager =
                LinearLayoutManager(requireContext())
        } else {
            binding.soilConditionerLayout.visibility = View.GONE
        }
        if (macroList.size > 0) {
            macroAdapter = SoilReportRecommendationAdapter(requireContext(), macroList)
            binding.macronutrientRecyclerView.setHasFixedSize(true)
            binding.macronutrientRecyclerView.adapter = macroAdapter
            binding.macronutrientRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        } else {
            binding.macronutrientLayout.visibility = View.GONE
        }
        if (microList.size > 0) {
            microAdapter = SoilReportRecommendationAdapter(requireContext(), microList)
            binding.micronutrientFertilizerRecyclerView.setHasFixedSize(true)
            binding.micronutrientFertilizerRecyclerView.adapter = microAdapter
            binding.micronutrientFertilizerRecyclerView.layoutManager =
                LinearLayoutManager(requireContext())
        } else {
            binding.micronutrientFertilizerLayout.visibility = View.GONE
        }
    }

    //
    fun setOtherData(data: ReportDetails) {
        if (data.StSamplingdDate != null) binding.sampleLayout.soilReportTv5.text =
            "Sampling Date - " + data.StSamplingdDate
        if (data.FdName != null) binding.farmerLayout.soilReportTv1.text = name + data.FdName
        if (data.FdAddress != null) binding.farmerLayout.soilReportTv2.text =
            address + data.FdAddress
        if (data.FdNumber != null) binding.farmerLayout.soilReportTv3.text =
            mobile + data.FdAddress
        if (data.TcName != null) binding.testCenterLayout.soilReportTv1.text =
            name + data.TcName
        if (data.TcAddress != null) binding.testCenterLayout.soilReportTv2.text =
            address + data.TcAddress
        if (data.TcLatitude != null) binding.sampleLayout.soilReportTv3.text =
            location + data.StLatitude.toString() + "," + data.StLongitude
        if (data.crop_name != null) binding.sampleLayout.soilReportTv1.text =
            selected_crop + data.crop_name
        if (data.soil_test_testing_date != null) binding.sampleLayout.soilReportTv6.text =
            test_date + data.soil_test_testing_date
        binding.sampleLayout.soilReportTv2.visibility = View.GONE
        binding.sampleLayout.soilReportTv4.visibility = View.GONE


    }

    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("ViewReportFragment")
    }

}