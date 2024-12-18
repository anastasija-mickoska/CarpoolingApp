// FormFragment.java
package com.example.carpoolingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.carpoolingapp.AddRideActivity;
import com.example.carpoolingapp.R;
import com.google.android.gms.maps.model.LatLng;

public class FormFragment extends Fragment {

    private EditText priceInput;
    private EditText dateInput;
    private EditText timeInput;
    private Button saveButton;
    double originLat,originLng,destLat,destLng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_form, container, false);

        Bundle args = getArguments();
        if (args != null) {
             originLat = args.getDouble("origin_latitude");
             originLng = args.getDouble("origin_longitude");
             destLat = args.getDouble("destination_latitude");
             destLng = args.getDouble("destination_longitude");
        }

        // Initialize form inputs
        priceInput = rootView.findViewById(R.id.insertPrice);
        dateInput = rootView.findViewById(R.id.insertDate);
        timeInput = rootView.findViewById(R.id.insertTime);
        saveButton = rootView.findViewById(R.id.saveRide);

        // Set up the save button click listener
        saveButton.setOnClickListener(v -> {
            // Collect data from form fields
            String price = priceInput.getText().toString().trim();
            String date = dateInput.getText().toString().trim();
            String time = timeInput.getText().toString().trim();

            if (price.isEmpty() || date.isEmpty() || time.isEmpty()) {
                // Show an error message if any of the fields are empty
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                ((AddRideActivity) getActivity()).saveRideData(price, date, time, originLat, originLng, destLat, destLng);
            }
//                CustomMapFragment mapFragment = (CustomMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//                if (mapFragment != null) {
//                    LatLng origin = mapFragment.getFirstLocation();
//                    LatLng destination = mapFragment.getSecondLocation();
//
//                    if (origin != null && destination != null) {
//                        // Call the method in the Activity to save the data with the locations
//                        ((AddRideActivity) getActivity()).saveRideData(price, date, time, origin, destination);
//                    } else {
//                        Toast.makeText(getActivity(), "Please pin both locations on the map", Toast.LENGTH_SHORT).show();
//                    }
//                }
        });

        return rootView;
    }

//    // Methods to retrieve form data
//    public String getPriceInput() {
//        return priceInput.getText().toString();
//    }
//
//    public String getDateInput() {
//        return dateInput.getText().toString();
//    }
//
//    public String getTimeInput() {
//        return timeInput.getText().toString();
//    }
}
