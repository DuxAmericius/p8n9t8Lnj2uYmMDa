/* HelpAdapter.java
   =============================================================================
                         Josh Talley and Daniel O'Donnell
                                Dulaney High School
                      Mobile Application Development 2016-17
   =============================================================================
   Purpose: This is the recycler view adapter for the Help activity.
*/
package com.fbla.dulaney.fblayardsale;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpAdapter  extends RecyclerView.Adapter<HelpAdapter.ViewHolder> {

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public HelpAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType)
        {
            case 0:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.help01_register, parent, false);
                break;
            case 1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.help02_login, parent, false);
                break;
            case 2:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.help03_switch, parent, false);
                break;
            case 3:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.help04_editaccount, parent, false);
                break;
            case 4:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.help05_addsales, parent, false);
                break;
            case 5:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.help06_viewsales, parent, false);
                break;
            case 6:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.help07_deletesales, parent, false);
                break;
            case 7:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.help08_comment, parent, false);
                break;
            case 8:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.help09_deletecomment, parent, false);
                break;
            case 9:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.help10_logout, parent, false);
                break;
            default:
                v = null;
                break;
        }
        if (v == null) return null;
        return new HelpAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HelpAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
