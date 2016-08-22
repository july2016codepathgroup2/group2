package com.pensum.pensumapplication.fragments.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.adapters.profile.ProfileSkillAdapter;
import com.pensum.pensumapplication.models.Skill;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by eddietseng on 8/21/16.
 */
public class SkillsFragment extends Fragment {
    private ArrayList<Skill> skills;
    protected ProfileSkillAdapter aSkills;

    public static SkillsFragment newInstance(String userId) {
        SkillsFragment frag = new SkillsFragment();
        Bundle args = new Bundle();
        if(userId != null)
            args.putString("userId", userId);
        frag.setArguments(args);
        return frag;
    }

    @BindView(R.id.rvSkills) RecyclerView rvSkills;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        skills = new ArrayList<>();
        aSkills = new ProfileSkillAdapter(skills);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_profile_skill, container, false);
        unbinder = ButterKnife.bind(this, v);

        rvSkills.setAdapter(aSkills);
        rvSkills.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        populateSkills();
        return v;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void populateSkills() {
        String userId = (String)getArguments().get("userId");

        if(userId == null) {
            List<Skill> skillList = ParseUser.getCurrentUser().getList("skills");
            for( Skill s : skillList) {
                try {
                    s.fetchIfNeeded();
                } catch (ParseException e) {
                    Log.e("message", "Error fetching skill" + e);
                }
            }
            skills.addAll(skillList);
            aSkills.notifyItemRangeInserted(0, skillList.size());
            return;
        }

        // Construct query to execute
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId",userId);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if(objects.size() == 1) {
                        List<Skill> skillList = objects.get(0).getList("skills");
                        for( Skill s : skillList) {
                            try {
                                s.fetchIfNeeded();
                            } catch (ParseException ex) {
                                Log.e("message", "Error fetching skill" + ex);
                            }
                        }
                        skills.addAll(skillList);
                        aSkills.notifyItemRangeInserted(0, skillList.size());
                    }
                } else {
                    Log.e("message", "Error getting user" + e);
                }
            }
        });
    }
}
