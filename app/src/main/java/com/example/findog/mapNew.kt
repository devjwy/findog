package com.example.findog

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.findog.databinding.PickMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class mapNew : AppCompatActivity(), OnMapReadyCallback  {

    var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION)

    var fullAddress = ""
    var shortAddres = ""
    var latitude = 0.0
    var longitude = 0.0

    val PERM_FLAG = 99
    lateinit var mMap: GoogleMap
    lateinit var binding: PickMapsBinding

    //콜백 인스턴스 생성
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val intent = Intent(this@mapNew, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PickMapsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (isPermitted()){
            startProcess()
        }else{
            ActivityCompat.requestPermissions(this,permissions,PERM_FLAG)
        }

        binding.SendBtn.setOnClickListener{
            val intent = Intent()
            intent.putExtra("address", shortAddres)
            intent.putExtra("axis", "${latitude},${longitude}")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        //뒤로가기
        this.onBackPressedDispatcher.addCallback(this, callback)

    }

    fun isPermitted(): Boolean{
        for(perm in permissions){
            if(ContextCompat.checkSelfPermission(this,perm) != PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }

    fun startProcess(){
        val mapFragment = supportFragmentManager
            .findFragmentById(com.example.findog.R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setUpdateLoctionListener()

        // 맵 터치 이벤트 구현 //
        mMap!!.setOnMapClickListener { point ->
            val mOptions = MarkerOptions()
            // 마커 타이틀
            mOptions.title("마커 좌표")
            latitude = point.latitude // 위도
            longitude = point.longitude // 경도

            val geocoder: Geocoder = Geocoder(this)
            var list: List<Address>? = null

            try {
                list = geocoder.getFromLocation(latitude, longitude, 10) //10개의 데이터를 얻어오겠다.

            } catch (e: IOException) {
                Log.d("위도/경도", "입출력 오류")
            }

            if (list != null) {
                if (list.isEmpty()) {
                    finish()
                } else { //정상적으로 산출된 주소
                    fullAddress = list.get(0).getAddressLine(0).toString()
                    shortAddres = fullAddress.split(",").get(0)
                    binding.textView.text = shortAddres
                }
            }

            mMap.clear()

            // 마커의 스니펫(간단한 텍스트) 설정
            mOptions.snippet("$latitude, $longitude")
            // LatLng: 위도 경도 쌍을 나타냄
            mOptions.position(LatLng(latitude, longitude))
            // 마커(핀) 추가S
            mMap.addMarker(mOptions)

        }

    }

    // --내 위치를 가져오는 코드
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback:LocationCallback

    @SuppressLint("MissingPermission")
    fun setUpdateLoctionListener(){

       val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,60000)
            .setWaitForAccurateLocation(false)
            .build()

        locationCallback = object :LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                p0?.let {
                    for ((i,location) in it.locations.withIndex()){
                        Log.d("로케이션","$i ${location.latitude}, ${location.longitude}")
                        setLastLocation(location)
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper())
    }

    fun setLastLocation(location : Location){
        val myLocation = LatLng(location.latitude, location.longitude)
        val marker = MarkerOptions()
            .position(myLocation)
            .title("현재 위치")
        val cameraOption = CameraPosition.Builder()
            .target(myLocation)
            .zoom(15.0f)
            .build()
        val camera = CameraUpdateFactory.newCameraPosition(cameraOption)

        mMap.clear()

        mMap.addMarker(marker)
        mMap.moveCamera(camera)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERM_FLAG -> {
                var check = true
                for (grant in grantResults){
                    if(grant != PERMISSION_GRANTED){
                        check = false
                        break
                    }
                }
                if(check){
                    startProcess()
                }else{
                    Toast.makeText(this, "권한 승인 필수",Toast.LENGTH_LONG)
                    finish()
                }
            }
        }
    }
}