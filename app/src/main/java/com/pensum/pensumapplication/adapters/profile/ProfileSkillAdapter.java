package com.pensum.pensumapplication.adapters.profile;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.fragments.profile.ItemTouchHelperAdapter;
import com.pensum.pensumapplication.fragments.profile.ItemTouchHelperViewHolder;
import com.pensum.pensumapplication.fragments.profile.RecyclerViewClickListener;
import com.pensum.pensumapplication.models.Skill;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by eddietseng on 8/21/16.
 */
public class ProfileSkillAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements ItemTouchHelperAdapter{
    private List<Skill> skills;
    private RecyclerViewClickListener listener;
    private SwipeDeleteListener swipeListener;
    private boolean isUser;

    public interface SwipeDeleteListener {
        void onSwipeDelete(String id);
    }

    public ProfileSkillAdapter(List<Skill> skills, RecyclerViewClickListener listener,
                               SwipeDeleteListener swipeListener, boolean isUser) {
        this.skills = skills;
        this.listener = listener;
        this.isUser = isUser;
        this.swipeListener = swipeListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_skill, parent, false);
        SkillViewHolder viewHolder = new SkillViewHolder(view,listener, isUser);

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

    @Override
    public void onItemDismiss(int position) {
        String skillId = skills.get(position).getObjectId();

        skills.remove(position);
        notifyItemRemoved(position);

        swipeListener.onSwipeDelete(skillId);
    }

    public class SkillViewHolder extends RecyclerView.ViewHolder
    implements ItemTouchHelperViewHolder{
        @BindView(R.id.tvSkillName) TextView tvSkillName;
        @BindView(R.id.pbSkillExperience) ProgressBar pbSkillExperience;
        @BindView(R.id.ivSkillDelete) ImageView ivSkillDelete;

        public SkillViewHolder(View itemView, final RecyclerViewClickListener listener, boolean isUser) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if(!isUser)
                ivSkillDelete.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                        listener.onRowClicked(getAdapterPosition());
                }
            });
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
