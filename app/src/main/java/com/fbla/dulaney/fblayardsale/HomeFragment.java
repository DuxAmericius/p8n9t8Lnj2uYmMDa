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
    private FragmentHomeBinding mBinding;

    @Override
    public void onClick(View v) {
        YardSaleMain main = (YardSaleMain)getActivity();
        FblaAzure azure = main.getAzure();
        boolean loggedOn = (azure != null && azure.getLoggedOn());
        switch (v.getId()) {

            case R.id.account:
                if (loggedOn) {
                    Intent i = new Intent(main, AccountEdit.class);
                    Bundle b = new Bundle();
                    b.putString("userId", azure.getUserId());
                    b.putString("token", azure.getToken());
                    i.putExtras(b);
                    getActivity().startActivityForResult(i, 0);
                }
                break;
            case R.id.add:
                if (loggedOn) {
                    Intent i = new Intent(main, AddSales.class);
                    Bundle b = new Bundle();
                    b.putString("userId", azure.getUserId());
                    b.putString("token", azure.getToken());
                    i.putExtras(b);
                    getActivity().startActivity(i);
                }
                break;
            case R.id.my:
                if (loggedOn) {
                    Intent i = new Intent(main, MySales.class);
                    Bundle b = new Bundle();
                    b.putString("userId", azure.getUserId());
                    b.putString("token", azure.getToken());
                    i.putExtras(b);
                    getActivity().startActivity(i);
                }
                break;
            case R.id.help:
                getActivity().startActivity(new Intent(getActivity(), Help.class));
                break;
            case R.id.logout:
                // Logout is a problem. Azure doesn't seem to be able to handle it
                // when I clear the cookies in order to force a new logon prompt.
                // If you try running the app too soon after logging off,
                // you get this strange net::ERR_EMPTY_RESPONSE error, which is coming
                // from the Azure library itself. Because of that problem, we are removing
                // the logoff feature and relabeling this button "Close App"
                //main.Logoff();
                getActivity().finish();
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
    }

}
