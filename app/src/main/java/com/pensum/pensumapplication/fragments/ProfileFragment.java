package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.adapters.profile.ProfileSkillAdapter;
import com.pensum.pensumapplication.fragments.profile.EditSkillFragment;
import com.pensum.pensumapplication.fragments.profile.ErrorSkillsFragment;
import com.pensum.pensumapplication.fragments.profile.SkillsFragment;
import com.pensum.pensumapplication.models.Skill;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by eddietseng on 8/19/16.
 */
public class ProfileFragment extends Fragment
        implements EditSkillFragment.EditSkillFragmentListener,
        ProfileSkillAdapter.SwipeDeleteListener {
    //    @BindView(R.id.ivProfBGImage)ImageView ivProfBGImage;
    @BindView(R.id.ivProfImage)ImageView ivProfImage;
    @BindView(R.id.tvProfName)TextView tvProfName;
    @BindView(R.id.btnProfAddSkill)ImageButton btnProfAddSkill;

    private Unbinder unbinder;
    private List<Skill> skills;
    private ParseUser user;

    public static ProfileFragment newInstance(String userId) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        profileFragment.setArguments(args);
        return profileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        skills = new ArrayList<>();

        return view;
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final String userId = getArguments().getString("userId");

        if (userId != null) { //any user
            ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
            query.whereEqualTo("objectId", userId);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        user = (ParseUser)objects.get(0);
                        populateView(userId);
                    } else {
                        Log.e("message", "Error fetch user with id" + e);
                    }
                    btnProfAddSkill.setVisibility(View.INVISIBLE);
                }
            });
        } else { //current user
            user = ParseUser.getCurrentUser();
            populateView(null);
        }
    }

    private void populateView(String userId) {
        if (user.get("skills") != null)
            skills = (List<Skill>) user.get("skills");

        //TODO blur the background
        if (user != null) {
            ivProfImage.setImageResource(0);

            String fbName = (String) user.get("fbName");
            tvProfName.setText(fbName);

            String profileUrl = (String) user.get("profilePicUrl");
            Picasso.with(getContext()).load(profileUrl).into(ivProfImage);
        }

        Fragment fragmentSkills = SkillsFragment.newInstance(userId);
//        Fragment fragmentStatus = new StatusFragment();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        if (skills.size() > 0)
            transaction.replace(R.id.flProfSkills, fragmentSkills);
        else
            transaction.replace(R.id.flProfSkills, new ErrorSkillsFragment());

        transaction.replace(R.id.flProfStatus, new ErrorSkillsFragment());
        transaction.commit();
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnProfAddSkill)
    public void addSkill(ImageButton button) {
        FragmentManager fm = getChildFragmentManager();
        EditSkillFragment editSkillDialogFragment =
                EditSkillFragment.newInstance("Add Skill", null, -1); // negative 1 for new skill
        editSkillDialogFragment.show(fm, "fragment_profile_edit_skill");
    }

    // Current user only
    @Override
    public void onFinishEditDialog(Skill skill, int position) {
        if (position == -1) { // Add new
            skills.add(0, skill);

            user.put("skills", skills);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                        Fragment fragmentSkills = SkillsFragment.newInstance(null);
                        transaction.replace(R.id.flProfSkills, fragmentSkills).commit();
                    } else {
                        Log.e("message", "Error saving skill to parse" + e);
                    }
                }
            });
        } else { // Update
            skills.set(position, skill); // Manual sync the list (might not needed)
        }
    }

    @Override
    public void onSwipeDelete(String id) {
        Iterator it = skills.iterator();
        while (it.hasNext()) {
            Skill skill = (Skill) it.next();
            if (skill.getObjectId().equals(id))
                it.remove();
        }

        user.put("skills", skills);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e("message", "Delete skill from parse user successfully");
                } else {
                    Log.e("message", "Error delete skill from parse user" + e);
                }
            }
        });

        if (skills.size() == 0) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.flProfSkills, new ErrorSkillsFragment()).commit();
        }
    }
}
