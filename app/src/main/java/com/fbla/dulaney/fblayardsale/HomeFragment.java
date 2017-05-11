/* HomeFragment.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This is the first fragment loaded on YardSaleMain. It shows the
   application icon and is used like a menu. Buttons take you other activities.
   You can also swipe left to get to the Local Sales fragment.
*/
package com.fbla.dulaney.fblayardsale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fbla.dulaney.fblayardsale.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    //private YardSaleMain mParent;
    FragmentHomeBinding mBinding;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.account:
                if (FblaLogon.getLoggedOn()) {
                    getActivity().startActivityForResult(new Intent(getActivity(), AccountEdit.class), 0);
                }
                break;
            case R.id.add:
                if (FblaLogon.getLoggedOn()) {
                    getActivity().startActivity(new Intent(getActivity(), AddSales.class));
                }
                break;
            case R.id.my:
                if (FblaLogon.getLoggedOn()) {
                    getActivity().startActivity(new Intent(getActivity(), MySales.class));
                }
                break;
            case R.id.help:
                getActivity().startActivity(new Intent(getActivity(), Help.class));
                break;
            case R.id.logout:
                YardSaleMain parent = (YardSaleMain)getActivity();
                parent.Logoff();
                break;
            default:
                break;
        }
    }

    public void setEnabled(boolean enable) {
        if (mBinding != null)
            mBinding.fragmentHome.setEnabled(enable);
    }

    public interface OnFragmentInteractionListener {
        public void onHomeAttach(HomeFragment f);
        public void onHomeDetach(HomeFragment f);
    }

    // Implementation of Fragment
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
            mListener.onHomeAttach(this);
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onHomeDetach(this);
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
                inflater, R.layout.fragment_home, container, false);
        mBinding.account.setOnClickListener(this);
        mBinding.add.setOnClickListener(this);
        mBinding.my.setOnClickListener(this);
        mBinding.help.setOnClickListener(this);
        mBinding.logout.setOnClickListener(this);
        View view = mBinding.getRoot();

        return view;
    }

    // Initializes layout items
    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        //mParent = (YardSaleMain)getActivity();
    }

}
