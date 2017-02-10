package com.fbla.dulaney.fblayardsale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fbla.dulaney.fblayardsale.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private FragmentActivity mParent;
    FragmentHomeBinding mBinding;
    HomeFragment mThis;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.account:
                getActivity().startActivity(new Intent(getActivity(), Account.class));
                break;
            default:
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        public void onHomeInteraction(View v);
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
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        View view = mBinding.getRoot();

        // Inflate the layout for this fragment
        //View v = inflater.inflate(R.layout.fragment_home, container, false);
        //mParent = getActivity();
        return view;
    }

    //@Override
    //public void onViewCreated(View view, Bundle savedInstanceState) {
    //    super.onViewCreated(view, savedInstanceState);
    //    mBinding = DataBindingUtil.bind(view);
    //    mBinding.add.setOnClickListener(this);
    //}

    // Initializes layout items
    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        mParent = getActivity();
        mThis = this;
    }


}
