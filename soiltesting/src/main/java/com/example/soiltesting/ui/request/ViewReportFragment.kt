package com.example.soiltesting.ui.request


import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.waycool.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.*
import java.lang.RuntimeException


class ViewReportFragment : Fragment() {
    private var _binding: FragmentViewReportBinding? = null
    private val binding get() = _binding!!

    var language = ""
    var id: kotlin.String? = ""
    var header: kotlin.String? = ""
    var soilConditioner: kotlin.String? = ""
    var recommendation: kotlin.String? = ""
    var macronutrient: kotlin.String? = ""
    var micronutrient: kotlin.String? = ""
    var scale: kotlin.String? = ""
    var scale_text1: kotlin.String? = ""
    var scale_text2: kotlin.String? = ""
    var scale_text3 = ""
    var scale_text4: kotlin.String? = ""
    var scale_text5: kotlin.String? = ""
    var scale_text6: kotlin.String? = ""
    var sample_info: kotlin.String? = ""
    var farmer: kotlin.String? = ""
    var testCenter: kotlin.String? = ""
    var selected_crop = ""
    var plot_size: kotlin.String? = ""
    var location: kotlin.String? = ""
    var survey_no: kotlin.String? = ""
    var sampling_date: kotlin.String? = ""
    var test_date: kotlin.String? = ""
    var name: kotlin.String? = ""
    var address: kotlin.String? = ""
    var mobile: kotlin.String? = ""
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

        scale_text2 = "Low, Deficient"
        scale_text3 = "Medium (OC,OM,mbc)"
        scale_text4 = "Medium, Neutral,Sufficient, Alkaline," + "Slightly Acidic"
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
//        binding.soilReportHeaderLayout.backBtn.setOnClickListener { onBackPressed() }
//        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                findNavController().navigateUp()
//            }
//
//        })
        val bundle = Bundle()
        if (arguments != null) {
            trackerID = arguments?.getInt("id")

            val soitestNumber = arguments?.getString("soil_test_number")
            binding.soilReportHeaderLayout.title.text = "ID: $soitestNumber"
            Log.d("TAG", "onViewCreatedbdhsvbhdbvhb:$trackerID ")
            setData(trackerID!!)
            CoroutineScope(Dispatchers.IO).launch {

            }

        }
        binding.soilReportHeaderLayout.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
        binding.cardCheckHealth.setOnClickListener {
            initObserve(trackerID!!)


        }

    }


    fun setData(id: Int) {
        viewModel.viewReport(id).observe(requireActivity()) { soilTestReportMaster ->
            when (soilTestReportMaster) {
                is Resource.Success -> {
                    Log.d("TAG", "setDataBHBHxbdb:${soilTestReportMaster} ")
                    Log.d("TAG", "setDataBHBHxb:${soilTestReportMaster.data!!.data} ")


                    if (soilTestReportMaster.data!!.status) {
                        if (soilTestReportMaster.data!!.data.get(0).results != null) {

                            fullList =
                                soilTestReportMaster.data!!.data.get(0).results.reportData.ferilizerRecomendations
                            if (fullList.isNotEmpty()) {
                                dataSetToRecommendation(fullList)
                            }
//                            fullData = soilTestReportMaster.data!!.data.get(0)
//                            setOtherData(fullData!!)
                            resultList =
                                soilTestReportMaster.data!!.data.get(0).results.reportData.parameterInfos
                            soilReportResultAdapter!!.updateList(resultList)

                        }
                        if (soilTestReportMaster.data!!.data.get(0) != null) {
                            fullData = soilTestReportMaster.data!!.data.get(0);
                            setOtherData(fullData!!);
                        }
                    }
                }

                is Resource.Error -> {

                }
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT)
                        .show()

                }
            }
        }


//        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
//            Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_LONG).show()
//        } else {
//            DataUtils.setProgressVisible(
//                requireContext(),
//                binding.loadingScreen.loadingProcess,
//                binding.loadingScreen.loadingImage
//            )
//            soilViewModel.getSoilReport(this@SoilTestReportActivity, language, id).observe(this,
//                Observer<Any?> { soilTestReportMaster ->
//                    DataUtils.setProgressGone(
//                        getApplicationContext(),
//                        binding.loadingScreen.loadingProcess
//                    )
//                    if (soilTestReportMaster != null) {
//                        if (soilTestReportMaster.getStatus()) {
//                            fullList =
//                                soilTestReportMaster.getData().get(0).getResults().getReportData()
//                                    .getFerilizerRecomendations()
//                            if (fullList.size > 0) {
//                                dataSetToRecommendation(fullList)
//                            }
//                            fullData = soilTestReportMaster.getData().get(0)
//                            setOtherData(fullData!!)
//                            resultList =
//                                soilTestReportMaster.getData().get(0).getResults().getReportData()
//                                    .getParameterInfos()
//                            soilReportResultAdapter!!.updateList(resultList)
//                        }
//                    } else {
//                        Toast.makeText(
//                            requireContext(),
//                            "Something went wrong",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                })
//        }
    }

    fun initObserve(soil_test_id: Int) {
        viewModel.pdfDownload(soil_test_id).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    val uri: Uri =

                        writeResponseBodyToDisk(it.data!!)
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


                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()

                }
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
            }

        }
    }


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
                    outputStream?.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d("g56", "file download: $fileSizeDownloaded of $fileSize")
                }
                outputStream.flush()
                FileProvider.getUriForFile(requireContext(),"com.example.soiltesting",invoicePdfFile)
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
        if (data.FdName != null) binding.farmerLayout.soilReportTv1.text =
            name + data.FdName
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
    }


}