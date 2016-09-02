package com.pensum.pensumapplication.fragments.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
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
public class SkillsFragment extends Fragment
        implements RecyclerViewClickListener, EditSkillFragment.EditSkillFragmentListener,
        ProfileSkillAdapter.SwipeDeleteListener {
    private ArrayList<Skill> skills;
    protected ProfileSkillAdapter aSkills;
    private ItemTouchHelper mItemTouchHelper;

    public static SkillsFragment newInstance(String userId) {
        SkillsFragment frag = new SkillsFragment();
        Bundle args = new Bundle();
        boolean isUser = false;

        if(userId!=null)
            args.putString("userId", userId);
        else
            isUser = true;

        args.putBoolean("isUser", isUser);
        frag.setArguments(args);
        return frag;
    }

    @BindView(R.id.rvSkills) RecyclerView rvSkills;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        skills = new ArrayList<>();
        aSkills = new ProfileSkillAdapter(skills, this, this, getArguments().getBoolean("isUser"));
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

        if(getArguments().getBoolean("isUser")) {
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(aSkills);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(rvSkills);
        }
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
        query.getInBackground(userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    List<Skill> skillList = object.getList("skills");
                    for( Skill s : skillList) {
                        try {
                            s.fetchIfNeeded();
                        } catch (ParseException ex) {
                            Log.e("message", "Error fetching skill" + ex);
                        }
                    }
                    skills.addAll(skillList);
                    aSkills.notifyItemRangeInserted(0, skillList.size());
                } else {
                    Log.e("message", "Error getting user in profile Skill" + e);
                }
            }
        });
    }

    @Override
    public void onRowClicked(int position) {
        Skill skill = skills.get(position);
        FragmentManager fm = getChildFragmentManager();
        EditSkillFragment editSkillDialogFragment =
                EditSkillFragment.newInstance("Add Skill", skill.getObjectId(), position);
        editSkillDialogFragment.show(fm, "fragment_profile_edit_skill");
    }

    @Override
    public void onFinishEditDialog(Skill skill, int position) {
        if( position != -1 ) { // Update
            String oldSkillId = skill.getObjectId();
            ParseObject oldSkill = ParseObject.createWithoutData("Skill", oldSkillId);
            oldSkill.put("skillName",skill.getSkillName());
            oldSkill.put("skillExperience",skill.getSkillExperiences());

            skills.set(position, skill);
            aSkills.notifyItemChanged(position);

            oldSkill.saveInBackground();
        }

        //Forward to parent
        Fragment fragment = getParentFragment();
        if( fragment instanceof EditSkillFragment.EditSkillFragmentListener )
            ((EditSkillFragment.EditSkillFragmentListener)fragment).onFinishEditDialog(skill,position);
    }

    @Override
    public void onSwipeDelete(String id) {
        ParseQuery<Skill> query = ParseQuery.getQuery(Skill.class);
        query.getInBackground(id, new GetCallback<Skill>() {
            @Override
            public void done(Skill object, ParseException e) {
                if (e == null)
                    object.deleteInBackground();
                else
                    Toast.makeText(getContext(),"Delete failed",Toast.LENGTH_SHORT).show();
            }
        });

        //Forward to parent
        Fragment fragment = getParentFragment();
        if( fragment instanceof ProfileSkillAdapter.SwipeDeleteListener ) {
            ((ProfileSkillAdapter.SwipeDeleteListener)fragment).onSwipeDelete(id);
        }
    }
}
