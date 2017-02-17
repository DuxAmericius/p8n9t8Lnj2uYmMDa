package com.fbla.dulaney.fblayardsale;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fbla.dulaney.fblayardsale.controller.LocalController;
import com.fbla.dulaney.fblayardsale.databinding.FragmentLocalBinding;

public class LocalFragment extends Fragment implements View.OnClickListener {

    private LocalFragment.OnFragmentInteractionListener mListener;
    private FragmentActivity mParent;
    FragmentLocalBinding mBinding;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comments:
                getActivity().startActivity(new Intent(getActivity(), Comments.class));
                break;
            default:
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        public void onLocalInteraction(View v);
    }

    // Implementation of Fragment
    public static LocalFragment newInstance(String param1, String param2) {
        LocalFragment fragment = new LocalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public LocalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (LocalFragment.OnFragmentInteractionListener) context;
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
                inflater, R.layout.fragment_local, container, false);
        //mBinding.comments.setOnClickListener(this);
        View view = mBinding.getRoot();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        // Setup the RecyclerView here because the data changes.
        mBinding.list.setLayoutManager(new LinearLayoutManager(mParent));
        LocalAdapter adapter = new LocalAdapter(this);
        LocalController.AttachAdapter(adapter);
        LocalController.Refresh();
        mBinding.list.setAdapter(adapter);
    }

}
