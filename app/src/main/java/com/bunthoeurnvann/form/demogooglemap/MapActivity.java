package com.bunthoeurnvann.form.demogooglemap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng CURRENT_LATLNG;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private EditText editTextSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        editTextSearch = findViewById(R.id.search_place);
        init();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng simple1 = new LatLng(11.7447549,104.9765563);
        CURRENT_LATLNG = simple1;
        mMap.addMarker(new MarkerOptions().position(simple1).title("ANGDA Nagagy"));
        moveCamera(simple1,15,"Current location");
    }
    private void moveCamera(LatLng latLng, float zoom, String title)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);
    }
    private void hideSoftKeyboard()
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    private void enableMylocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},LOCATION_PERMISSION_REQUEST_CODE);
        }
        else if(mMap != null){
            mMap.setMyLocationEnabled(true);
        }
        //mMap.setMyLocationEnabled(true);
    }
    private void init()
    {
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE || actionId == KeyEvent.ACTION_DOWN || actionId == KeyEvent.KEYCODE_ENTER
                )
                    geoLocate();
                return false;
            }
        });
        hideSoftKeyboard();
    }
    private void geoLocate()
    {
        String searchString = editTextSearch.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
            if(list.size()>0){
                Address address = list.get(0);
                Log.e("GEOLocate","Found result"+address.toString());
                LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                moveCamera(latLng, 15, address.getAddressLine(0));
            }
        }catch (Exception ex){
            Log.e("GeoLocate error::",ex.toString());
        }
    }
}
