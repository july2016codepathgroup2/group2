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
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.fragments.profile.EditSkillFragment;
import com.pensum.pensumapplication.fragments.profile.ErrorSkillsFragment;
import com.pensum.pensumapplication.fragments.profile.SkillsFragment;
import com.pensum.pensumapplication.fragments.profile.StatusFragment;
import com.pensum.pensumapplication.models.Skill;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by eddietseng on 8/19/16.
 */
public class ProfileFragment extends Fragment
        implements EditSkillFragment.EditSkillFragmentListener {
    //    @BindView(R.id.ivProfBGImage)ImageView ivProfBGImage;
    @BindView(R.id.ivProfImage)
    ImageView ivProfImage;
    @BindView(R.id.tvProfName)
    TextView tvProfName;

    private Unbinder unbinder;
    private List<Skill> skills;
    private ParseUser user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        skills = new ArrayList<>();

        //TODO blur the background

        user = ParseUser.getCurrentUser(); //TODO change to any user
        if (user != null) {
            ivProfImage.setImageResource(0);

            String fbName = (String) user.get("fbName");
            tvProfName.setText(fbName);

            String profileUrl = (String) user.get("profilePicUrl");
            Picasso.with(getContext()).load(profileUrl).into(ivProfImage);

            if (user.get("skills") != null) {
                skills = (List<Skill>) user.get("skills");
            }
        }

        return view;
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Fragment fragmentSkills = SkillsFragment.newInstance(null);
        Fragment fragmentStatus = new StatusFragment();

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
                EditSkillFragment.newInstance("Add Skill", null);
        editSkillDialogFragment.show(fm, "fragment_profile_edit_skill");
    }

    // Current user only
    @Override
    public void onFinishEditDialog(Skill skill) {
        Toast.makeText(getContext(), "Save skill" + skill.toString(), Toast.LENGTH_SHORT).show();
        skills.add(0, skill);
        user.put("skills",skills);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    Fragment fragmentSkills = SkillsFragment.newInstance(null);
                    transaction.replace(R.id.flProfSkills, fragmentSkills).commit();
                }
                else {
                    Log.e("message", "Error saving skill to parse" + e);
                }
            }
        });
    }
}
