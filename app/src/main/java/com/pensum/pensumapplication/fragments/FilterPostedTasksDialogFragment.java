package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.pensum.pensumapplication.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by violetaria on 9/3/16.
 */
public class FilterPostedTasksDialogFragment extends DialogFragment {
    @BindView(R.id.sPosted) Switch sPosted;
    @BindView(R.id.sAccepted) Switch sAccepted;
    @BindView(R.id.sCompleted) Switch sCompleted;
    @BindView(R.id.btnSaveFilters) Button btnSaveFilters;
    private Unbinder unbinder;

    public FilterPostedTasksDialogFragment(){

    }

    public static FilterPostedTasksDialogFragment newInstance(boolean posted, boolean accepted, boolean completed) {
        FilterPostedTasksDialogFragment fragment = new FilterPostedTasksDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("posted", posted);
        args.putBoolean("accepted", accepted);
        args.putBoolean("completed", completed);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fliter_posted_tasks, parent, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean posted = getArguments().getBoolean("posted");
        boolean accepted = getArguments().getBoolean("accepted");
        boolean completed = getArguments().getBoolean("completed");

        sPosted.setChecked(posted);
        sAccepted.setChecked(accepted);
        sCompleted.setChecked(completed);

        btnSaveFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBackFilters();
            }
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void sendBackFilters(){
        final FilterPostedTasksDialogListener listener = (FilterPostedTasksDialogListener) getTargetFragment();
        boolean posted = sPosted.isChecked();
        boolean accepted = sAccepted.isChecked();
        boolean completed = sCompleted.isChecked();

        listener.onFinishFilterPostedTasksDialog(posted, accepted, completed);
        dismiss();
    }

    public interface  FilterPostedTasksDialogListener{
        void onFinishFilterPostedTasksDialog(boolean posted, boolean accepted, boolean completed);
    }

}
