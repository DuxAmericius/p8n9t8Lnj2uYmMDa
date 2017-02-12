package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fbla.dulaney.fblayardsale.databinding.FragmentSearchBinding;


public class SearchFragment extends Fragment implements View.OnClickListener {

    private SearchFragment.OnFragmentInteractionListener mListener;
    private FragmentActivity mParent;
    FragmentSearchBinding mBinding;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchzip:
                //getActivity().startActivity(new Intent(getActivity(), Account.class));
                break;
            case R.id.searchkey:
                //getActivity().startActivity(new Intent(getActivity(), AddSales.class));
                break;
            default:
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        public void onSearchInteraction(View v);
    }

    // Implementation of Fragment
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SearchFragment.OnFragmentInteractionListener) context;
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
        // Inflate the layout for this fragment


        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_search, container, false);
        mBinding.searchzip.setOnClickListener(this);
        mBinding.searchkey.setOnClickListener(this);
        View view = mBinding.getRoot();

        return view;
    }


}
