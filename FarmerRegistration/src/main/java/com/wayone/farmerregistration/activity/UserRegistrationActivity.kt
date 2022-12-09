package com.wayone.farmerregistration.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.waycool.data.Network.NetworkModels.ModuleData
import com.waycool.data.utils.NetworkUtil
import com.waycool.uicomponents.utils.AppUtil
import com.wayone.farmerregistration.R
import com.wayone.farmerregistration.adapter.UserProfileKnowServiceAdapter
import com.wayone.farmerregistration.adapter.UserProfilePremiumAdapter
import com.wayone.farmerregistration.databinding.ActivityUserProfileBinding
import com.wayone.farmerregistration.registrationViewModel.RegistrationViewModel
import java.util.*
import kotlin.collections.HashMap

class UserRegistrationActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserProfileBinding
    var k : Int= 4
    var dummylist : MutableList<ModuleData>  = ArrayList()
    var name: Array<String> = arrayOf("Mandi","Crop Health","Crop Information","News")
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    lateinit var latitude:String
    lateinit var  longitutde:String
    lateinit var registrationViewModel: RegistrationViewModel
    var village = ""
    var state = ""
    var district = ""
    var subDistrict = ""
    var pincode = ""
    lateinit var knowAdapter : UserProfileKnowServiceAdapter
    lateinit var premiumAdapter : UserProfilePremiumAdapter
    var mobileNumber : String? =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater);
        setContentView(binding.root)
        binding.registerDoneBtn.isEnabled = false
        if (intent.getStringExtra(getString(R.string.mobile_stringextra)) != null)
            mobileNumber = intent.getStringExtra(getString(R.string.mobile_stringextra))
        registrationViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.farmerDetailsTitle.setTextColor(resources.getColor(R.color.black))
        binding.titleTv.setTextColor(resources.getColor(R.color.black))
        binding.farmerDetMsgTv.setTextColor(resources.getColor(R.color.black))
        binding.farmerNameTv.setTextColor(resources.getColor(R.color.black))
        binding.farmerLocationTv.setTextColor(resources.getColor(R.color.black))
        binding.knowServicesTv.setTextColor(resources.getColor(R.color.black))
        binding.premiumFeaturesTv.setTextColor(resources.getColor(R.color.black))
        premiumAdapter = UserProfilePremiumAdapter(
            dummylist,
            applicationContext, this
        )
        binding.premiumFeaturesRecyclerView.setHasFixedSize(true)
        binding.premiumFeaturesRecyclerView.adapter = premiumAdapter
        binding.premiumFeaturesRecyclerView.layoutManager =
            GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        binding.premiumFeaturesRecyclerView.setItemViewCacheSize(20)
        binding.premiumFeaturesRecyclerView.isDrawingCacheEnabled = true
        binding.premiumFeaturesRecyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        binding.premiumFeaturesRecyclerView.invalidate()

        knowAdapter = UserProfileKnowServiceAdapter(
            dummylist,
            applicationContext, this
        )
        binding.knowServicesRecyclerView.setHasFixedSize(true)
        binding.knowServicesRecyclerView.adapter = knowAdapter
        binding.knowServicesRecyclerView.layoutManager =
            GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        binding.knowServicesRecyclerView.setItemViewCacheSize(20)
        binding.knowServicesRecyclerView.isDrawingCacheEnabled = true
        binding.knowServicesRecyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        binding.knowServicesRecyclerView.invalidate()
        binding.userNameEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, count: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                if(binding.userNameEdittext.text.toString().trim().length != 0) {
                    if (binding.userLocationEdittext.text.toString().trim().length != 0) {
                        binding.registerDoneBtn.isEnabled = true
                    } else {
                        binding.registerDoneBtn.isEnabled = false
                    }
                }else{
                    binding.registerDoneBtn.isEnabled = false
                }
            }
        })
        binding.userLocationEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(binding.userLocationEdittext.text.toString().trim().length != 0) {
                    if (binding.userNameEdittext.text.toString().trim().length != 0) {
                        binding.registerDoneBtn.isEnabled = true
                    } else {
                        binding.registerDoneBtn.isEnabled = false
                    }
                }else{
                    binding.registerDoneBtn.isEnabled = false
                }
            }
        })
        binding.registerDoneBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                AppUtil.Toast(applicationContext,"cliked")
                userCreater()
            }
        })
        userModule()
        getLocation()
    }
    fun replaceFragmentwithoutbackstack( tittle : String,desc :String,url:String?,type:String) {
        val bottomSheetDialog = BottomSheetDialog(this@UserRegistrationActivity)
        bottomSheetDialog.setContentView(R.layout.fragmrnt_service_desc_layoutr)
        val headerTv = bottomSheetDialog.findViewById<TextView>(R.id.desc_item_name)
        val UserTYpeTV = bottomSheetDialog.findViewById<TextView>(R.id.desc_service_name)
        val descTV = bottomSheetDialog.findViewById<TextView>(R.id.desc_tv)
        val close = bottomSheetDialog.findViewById<ImageView>(R.id.privacy_close_btn)
        val icon = bottomSheetDialog.findViewById<ImageView>(R.id.image)
        headerTv!!.setText(tittle)
        UserTYpeTV!!.setText("free user")
        descTV!!.setText(desc)
        if(type.equals("0")){
            UserTYpeTV!!.setText("Free User")
            //icon!!.setVisibility(View.VISIBLE)
            icon!!.visibility=View.VISIBLE
        }else{
            UserTYpeTV!!.setText("Premium User")
           // icon!!.setVisibility(View.GONE)
            icon!!.visibility=View.GONE
        }
        descTV.setMovementMethod(ScrollingMovementMethod())
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.show()
        close!!.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                bottomSheetDialog.dismiss()
            }
        })
        /*Log.d("tested","test"+url)
        val addPhotoBottomDialogFragment = ServiceDialogFragment.newInstance()
        val bundle = Bundle()
        bundle.putString("tittle", tittle)
        bundle.putString("desc", desc)
        bundle.putString("url", url)
        bundle.putString("type", type)
        addPhotoBottomDialogFragment.arguments = bundle
        addPhotoBottomDialogFragment.show(supportFragmentManager, "dialog")
        addPhotoBottomDialogFragment.isCancelable = false
        addPhotoBottomDialogFragment.setStyle(
            DialogFragment.STYLE_NORMAL, R.style.someStyle
        )*/
    }

    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        latitude= location.latitude.toString()
                        longitutde = location.longitude.toString()
                    }
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>

                        if (list.size > 0) {
                            subDistrict = list.get(0).getLocality()
                            village = list.get(0).getSubLocality()
                            district = list.get(0).subAdminArea
                            state = list.get(0).getAdminArea()
                            pincode = list.get(0).postalCode
                        }
                        if(village.isEmpty()){
                            if(subDistrict.isEmpty()){
                                if(district.isEmpty()){

                                }else{
                                    village = district
                                }
                            } else{
                                village = subDistrict
                            }
                        }
                    }
                    binding.userLocationEdittext.setText(village)
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }
    fun userCreater(){
        if (NetworkUtil.getConnectivityStatusString(applicationContext) == 0) {
            Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show()
        }else {
            var query : HashMap<String,String>
            query = HashMap()
            query.put("name",binding.userNameEdittext.text.toString().trim())
            query.put("contact", mobileNumber.toString())
            query.put("lat",latitude)
            query.put("long",longitutde)
            query.put("lang_id","1")
            query.put("email","")
            query.put("pincode",pincode)
            query.put("village",village)
            query.put("address","")
            query.put("state_id","")
            query.put("district_id","")
            query.put("sub_district_id","")
            registrationViewModel.getUserData(query).observe(this) { registerMaster ->
               // registerMaster.data.approved
               // Log.d("registerresponse", "test" + registerMaster.data.approved)
            }
        }
    }
    fun userModule(){
        registrationViewModel.moduleMaster.observe(this){
                moduleMaster->
            if(moduleMaster.status==true) {
                dummylist.addAll(moduleMaster.data)
                knowAdapter.notifyDataSetChanged()
                premiumAdapter.notifyDataSetChanged()

                moduleMaster.message
                Log.d("registerresponse", "test" + moduleMaster.message)
            }


        }
    }
}