/* MapFragment.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This fragment uses the Google Map API to display and interactive
   map. The data in LocalController is used to place pins on the map of your
   school and all schools within a 10 or 5 mile radius that have at least
   one item for sale. The pins are color coded so that schools with only
   1-2 items are yellow, 3-4 items are orange, and 5+ items are red. Your school
   pin is always azure.
*/
package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fbla.dulaney.fblayardsale.controller.LocalController;
import com.fbla.dulaney.fblayardsale.model.SaleItem;
import com.fbla.dulaney.fblayardsale.model.Schools;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.support.v4.app.Fragment;

import com.fbla.dulaney.fblayardsale.databinding.FragmentMapBinding;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapFragment extends Fragment {

    private MapFragment.OnFragmentInteractionListener mListener;
    FragmentMapBinding mBinding;
    private GoogleMap mMap;

    public interface OnFragmentInteractionListener {
        public void onMapAttach(MapFragment f);
        public void onMapDetach(MapFragment f);
    }

    // Implementation of Fragment
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    public void setEnabled(boolean enable) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (MapFragment.OnFragmentInteractionListener) context;
            mListener.onMapAttach(this);
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onMapDetach(this);
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_map, container, false);
        View view = mBinding.getRoot();

        Log.d("Map:onCreateView", "Start");
        mBinding.map.onCreate(savedInstanceState);
        mBinding.map.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            Log.d("Map:onCreateView", e.getMessage());
        }

        mBinding.map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setAllGesturesEnabled(true);

                if (FblaLogon.getAccount() == null) return;
                Schools mySchool = FblaLogon.getAccount().getSchool();
                if (mySchool == null) return;
                // Get all of the distinct schools from the LocalController
                ArrayList<Schools> schools = new ArrayList<>();
                HashMap<String, Integer> counts = new HashMap<String, Integer>();
                for (int i = 0; i < LocalController.getItemCount(); i++) {
                    SaleItem item = LocalController.getItem(i);
                    Schools school = item.getAccount().getSchool();
                    if (!schools.contains(school)) {
                        schools.add(school);
                        counts.put(school.getId(), new Integer(1));
                    } else {
                        counts.put(school.getId(), counts.get(school.getId()) + 1);
                    }
                }
                LatLng myLL = null;
                // Mark all nearby schools on the map.
                for (Schools s : schools) {
                    String title = s.getSchool();
                    Integer sales = counts.get(s.getId());
                    String desc = "Items for sale: " + sales.toString();
                    LatLng ll = new LatLng(s.getLat(), s.getLong());
                    BitmapDescriptor bm;
                    if (s.getId().equals(mySchool.getId())) {
                        myLL = ll;
                        bm = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                    } else {
                        // Change the hue of the marker depending on how many items are for sale at the school.
                        switch (sales) {
                            case 1:
                            case 2:
                                bm = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                                break;
                            case 3:
                            case 4:
                                bm = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                                break;
                            default:
                                bm = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                                break;
                        }
                    }
                    mMap.addMarker(new MarkerOptions().position(ll)
                            .title(title).snippet(desc).icon(bm));
                }
                // See if your school has already been marked. If not, mark it.
                if (myLL == null) {
                    myLL = new LatLng(mySchool.getLat(), mySchool.getLong());
                    String title = mySchool.getSchool();
                    String desc = "Items for sale: 0";
                    mMap.addMarker(new MarkerOptions().position(myLL)
                            .title(title).snippet(desc)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
                // Set the camera on your school
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLL));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBinding.map.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding.map.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mBinding.map.onLowMemory();
    }

    // Initializes layout items
    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

}
