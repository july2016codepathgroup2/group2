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
        conversations = new ArrayList<>();
        String taskObjectId = getArguments().getString("task_object_id");
        Task task = new Task();
        task.setObjectId(taskObjectId);
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
        query.whereEqualTo("owner", ParseUser.getCurrentUser());
        query.whereEqualTo("task", task);
        query.include("candidate");
        query.include("task");
        query.findInBackground(new FindCallback<Conversation>() {
            public void done(List<Conversation> conversationsFromQuery, ParseException e) {
                if (e == null) {
                    List<String> spinnerArray =  new ArrayList<String>();
                    for(Conversation c: conversationsFromQuery) {
                        spinnerArray.add(FormatterHelper.formatName(c.getCandidate().getString("fbName")) + "  -  " + FormatterHelper.formatMoney(c.getOffer().toString()));
                    }
//                    CandidateSpinnerAdapter adapter = new CandidateSpinnerAdapter(
//                            getContext(), R.layout.candidate_spinner_item, conversationsFromQuery);
//
//                    adapter.setDropDownViewResource(R.layout.candidate_spinner_item);

                    ArrayAdapter<String> adapter =  new ArrayAdapter(
                            getContext(),android.R.layout.simple_list_item_1 ,spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

                    spCandidates.setAdapter(adapter);
                    conversations.addAll(conversationsFromQuery);
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
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Conversation conversation = conversations.get(spCandidates.getSelectedItemPosition());
                conversation.getTask().acceptCandidate(conversation);
                dismiss();
            }
        });

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
