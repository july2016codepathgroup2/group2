package com.pensum.pensumapplication.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.models.Task;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private static OnItemClickListener listener;

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
//        String profilePicture = task.getPostedBy().getProfilePictureUrl();
        Picasso.with(getContext()).load(R.mipmap.ic_launcher).
                transform(new CropCircleTransformation()).into(ivProfilePicture);

        tvType = holder.tvType;
        tvType.setText("#" + task.getType());

        tvTaskTitle = holder.tvTaskTitle;
        tvTaskTitle.setText(task.getTitle());

        tvPrice = holder.tvPrice;
        tvPrice.setText("$" + task.getBudget());
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfilePicture;
        public TextView tvType;
        public TextView tvTaskTitle;
        public TextView tvPrice;

        public ViewHolder(final View itemView) {
            super(itemView);

            ivProfilePicture = (ImageView) itemView.findViewById(R.id.ivProfilePicture);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
            tvTaskTitle = (TextView) itemView.findViewById(R.id.tvTaskTitle);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onItemClick(itemView, getLayoutPosition());
                    }
                }
            });
        }
    }
}
