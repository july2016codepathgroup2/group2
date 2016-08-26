package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.helpers.FormatterHelper;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by violetaria on 8/26/16.
 */
public class AcceptTaskDialogFragment extends DialogFragment {
    @BindView(R.id.spCandidates) Spinner spCandidates;
    @BindView(R.id.btnAccept) Button btnAccept;

    private Unbinder unbinder;
    private Task task;
    private ArrayList<Conversation> conversations;

    public AcceptTaskDialogFragment(){}

    public static AcceptTaskDialogFragment newInstance(String taskObjectId){
        AcceptTaskDialogFragment fragment = new AcceptTaskDialogFragment();
        Bundle args = new Bundle();
        args.putString("task_object_id",taskObjectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String taskObjectId = getArguments().getString("task_object_id");
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
        query.whereEqualTo("owner", ParseUser.getCurrentUser());
        query.whereEqualTo("task.objectId", taskObjectId);
        query.include("candidate");
        query.findInBackground(new FindCallback<Conversation>() {
            public void done(List<Conversation> conversationsFromQuery, ParseException e) {
                if (e == null) {
                    List<String> spinnerArray =  new ArrayList<String>();
                    for(Conversation c: conversationsFromQuery) {
                        spinnerArray.add(FormatterHelper.formatName(c.getCandidate().getString("fbName")) + " - " + c.getOffer().toString());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getActivity(), android.R.layout.simple_spinner_item, spinnerArray);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCandidates.setAdapter(adapter);
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accept_task, parent, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }
}
