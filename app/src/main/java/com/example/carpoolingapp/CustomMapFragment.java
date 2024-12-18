package com.example.carpoolingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CustomMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng firstLocation;
    private LatLng secondLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CustomMapFragment", "onCreate called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);  // Make sure to use your layout XML here
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("CustomMapFragment", "onViewCreated called");

        // Dynamically load the SupportMapFragment into the container
        if (getActivity() != null) {
            SupportMapFragment mapFragment = SupportMapFragment.newInstance();

            // Dynamically add the map fragment into the FrameLayout
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.map, mapFragment);  // Make sure 'map' is the correct ID of the FrameLayout
            transaction.commit();

            // Set the callback when the map is ready
            mapFragment.getMapAsync(this);
            Log.d("CustomMapFragment", "getMapAsync called");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Map is ready to use
        Log.d("MapFragment", "onMapReady called");

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Set a default camera position
        LatLng defaultLocation = new LatLng(41.59, 21.25);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        // Enable map click listener for pinning locations
        mMap.setOnMapClickListener(latLng -> {
            Log.d("MapFragment", "Map clicked at: " + latLng);
            if (firstLocation == null) {
                firstLocation = latLng;
                mMap.addMarker(new MarkerOptions().position(latLng).title("Origin"));
            } else if (secondLocation == null) {
                secondLocation = latLng;
                mMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));

                // Navigate to the FormFragment after selecting the destination
                navigateToFormFragment(firstLocation, secondLocation);
            }
        });
    }

    // Method to navigate to FormFragment with selected locations
    private void navigateToFormFragment(LatLng firstLocation, LatLng secondLocation) {
        Bundle bundle = new Bundle();
        bundle.putDouble("origin_latitude", firstLocation.latitude);
        bundle.putDouble("origin_longitude", firstLocation.longitude);
        bundle.putDouble("destination_latitude", secondLocation.latitude);
        bundle.putDouble("destination_longitude", secondLocation.longitude);

        FormFragment formFragment = new FormFragment();
        formFragment.setArguments(bundle);

        // Navigate to the form fragment
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, formFragment)
                .addToBackStack(null)
                .commit();
    }

    // Methods to access the pinned locations
//    public LatLng getFirstLocation() {
//        return firstLocation;
//    }
//
//    public LatLng getSecondLocation() {
//        return secondLocation;
//    }
}
