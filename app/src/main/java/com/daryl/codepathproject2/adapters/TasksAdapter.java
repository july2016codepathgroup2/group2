package com.daryl.codepathproject2.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daryl.codepathproject2.R;
import com.daryl.codepathproject2.models.Task;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private List<Task> mTasks;
    private Context mContext;
    private TextView tvType;
    private TextView tvTaskTitle;
    private TextView tvPrice;
    private ImageView ivProfilePicture;

    public TasksAdapter(List<Task> mTasks, Context mContext) {
        this.mTasks = mTasks;
        this.mContext = mContext;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View taskView = inflater.inflate(R.layout.item_task, parent, false);

        ViewHolder viewHolder = new ViewHolder(taskView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = mTasks.get(position);

        ivProfilePicture = holder.ivProfilePicture;
        Picasso.with(getContext()).load(task.getAuthor().getProfilePictureUrl()).
                transform(new CropCircleTransformation()).into(ivProfilePicture);

        tvType = holder.tvType;
        tvType.setText("#" + task.getType());
//        setColorAndTypeForTvType(task);

        tvTaskTitle = holder.tvTaskTitle;
        tvTaskTitle.setText(task.getTitle());

        tvPrice = holder.tvPrice;
        tvPrice.setText("$" + (int) task.getBudget());
    }

    private void setColorAndTypeForTvType(Task task) {
        switch (task.getType()) {
            case "Cleaning":
                tvType.setText("#" + "Cleaning");
                tvType.setBackgroundColor(Color.MAGENTA);
                break;
            case "Pet Care":
                tvType.setText("#" + "Pet Care");
                tvType.setBackgroundColor(Color.CYAN);
                break;
            case "Electronics":
                tvType.setText("#" + "Electronics");
                tvType.setBackgroundColor(Color.YELLOW);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfilePicture;
        public TextView tvType;
        public TextView tvTaskTitle;
        public TextView tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);

            ivProfilePicture = (ImageView) itemView.findViewById(R.id.ivProfilePicture);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
            tvTaskTitle = (TextView) itemView.findViewById(R.id.tvTaskTitle);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
        }
    }
}
