package com.fbla.dulaney.fblayardsale;

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

import com.fbla.dulaney.fblayardsale.databinding.ListItemsBinding;

public class TextFragment extends Fragment implements View.OnClickListener {

    private TextFragment.OnFragmentInteractionListener mListener;
    private FragmentActivity mParent;
    ListItemsBinding mBinding;

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
        public void onTextInteraction(View v);
    }

    // Implementation of Fragment
    public static TextFragment newInstance(String param1, String param2) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TextFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (TextFragment.OnFragmentInteractionListener) context;
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
                inflater, R.layout.list_items, container, false);
        mBinding.comments.setOnClickListener(this);
        View view = mBinding.getRoot();
        return view;
    }
}
