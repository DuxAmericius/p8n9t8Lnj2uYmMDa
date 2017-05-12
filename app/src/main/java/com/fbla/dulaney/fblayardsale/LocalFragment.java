/* LocalFragment.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This is the second fragment loaded on YardSaleMain. It will display
   all sale items that are within either 5 or 10 miles of your school,
   excluding any of your own sale items.

   Those sale items are loaded from the LocalController.

   You can swipe right to get to the Home fragment.
   You can swipe left to get to the Map fragment.
*/
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
    private FragmentLocalBinding mBinding;

    @Override
    public void onClick(View v) {
        YardSaleMain main = (YardSaleMain)getActivity();
        FblaAzure azure = main.getAzure();
        switch (v.getId()) {
            case R.id.comments:
                Intent i = new Intent(main, Comments.class);
                Bundle b = new Bundle();
                b.putString("userId", azure.getUserId());
                b.putString("token", azure.getToken());
                i.putExtras(b);
                getActivity().startActivity(i);
                break;
            default:
                break;
        }
    }

    public void setEnabled(boolean enable) {
        if (mBinding != null)
            mBinding.fragmentLocal.setEnabled(enable);
    }

    public interface OnFragmentInteractionListener {
        public void onLocalAttach(LocalFragment f);
        public void onLocalDetach(LocalFragment f);
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
            mListener.onLocalAttach(this);
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onLocalDetach(this);
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
        View view = mBinding.getRoot();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        // Setup the RecyclerView here because the data changes.
        YardSaleMain mParent = (YardSaleMain)getActivity();
        mBinding.list.setLayoutManager(new LinearLayoutManager(mParent));
        LocalAdapter adapter = new LocalAdapter(this, mParent.getAzure());
        LocalController.AttachAdapter(adapter);
        LocalController.Refresh(mParent, mParent.getAzure());
        mBinding.list.setAdapter(adapter);
    }

}
