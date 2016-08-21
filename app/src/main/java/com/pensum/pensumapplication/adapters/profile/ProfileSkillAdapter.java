package com.pensum.pensumapplication.adapters.profile;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.models.Skill;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by eddietseng on 8/21/16.
 */
public class ProfileSkillAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Skill> skills;

    public ProfileSkillAdapter(List<Skill> skills) {
        this.skills = skills;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_skill, parent, false);
        SkillViewHolder viewHolder = new SkillViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SkillViewHolder svh = (SkillViewHolder)holder;
        configureSkillViewHolder(svh,position);
    }

    @Override
    public int getItemCount() {
        return skills.size();
    }

    private void configureSkillViewHolder(SkillViewHolder svh, int position) {
        Skill skill = skills.get(position);

        svh.tvSkillName.setText(skill.getSkillName());
        svh.pbSkillExperience.setProgress(skill.getSkillExperiences());
    }

    public class SkillViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvSkillName) TextView tvSkillName;
        @BindView(R.id.pbSkillExperience) ProgressBar pbSkillExperience;

        public SkillViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
