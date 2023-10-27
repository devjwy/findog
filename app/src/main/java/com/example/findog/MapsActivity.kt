package com.example.findog

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import com.example.findog.databinding.PickMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    lateinit var binding: PickMapsBinding
    var fullAddress = ""
    var shortAddres = ""
    var latitude = 0.0
    var longitude = 0.0

    //콜백 인스턴스 생성
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val intent = Intent(this@MapsActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PickMapsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

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

            // 마커의 스니펫(간단한 텍스트) 설정
            mOptions.snippet("$latitude, $longitude")
            // LatLng: 위도 경도 쌍을 나타냄
            mOptions.position(LatLng(latitude, longitude))
            // 마커(핀) 추가S
            googleMap.addMarker(mOptions)
        }
        ////////////////////

        // Add a marker in Seoul and move the camera
        val seoul = LatLng(37.5562, 126.9724)
        mMap!!.addMarker(MarkerOptions().position(seoul).title("Marker in seoul"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,15.0f))
    }

}
