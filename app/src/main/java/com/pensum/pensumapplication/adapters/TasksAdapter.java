package com.pensum.pensumapplication.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.fragments.profile.ItemTouchHelperAdapter;
import com.pensum.pensumapplication.fragments.profile.ItemTouchHelperViewHolder;
import com.pensum.pensumapplication.models.Task;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    public interface SwipeDeleteListener {
        void onSwipeDelete(String id);
    }

    private OnItemClickListener listener;
    private SwipeDeleteListener swipeListener;

    private List<Task> mTasks;
    private Context mContext;
    private TextView tvType;
    private TextView tvTaskTitle;
    private TextView tvPrice;
    private ImageView ivProfilePicture;
    private ImageView ivBidderTag;

    public TasksAdapter(List<Task> mTasks, Context mContext, SwipeDeleteListener swipeListener) {
        this.mTasks = mTasks;
        this.mContext = mContext;
        this.swipeListener = swipeListener;
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

        if(task.getHasBidder()){
            if(TextUtils.equals(task.getStatus(),"accepted")) {
                holder.ivBidderTag.setImageResource(R.drawable.ic_check_circle_black_24dp);
            } else {
                holder.ivBidderTag.setImageResource(R.drawable.tag);
            }
        } else {
            holder.ivBidderTag.setImageResource(0);
        }

        try {
            ParseUser postedBy = task.getPostedBy().fetchIfNeeded();
            String imageUrl = postedBy.getString("profilePicUrl");
            if (imageUrl != null){
                Picasso.with(getContext()).load(imageUrl).
                        transform(new CropCircleTransformation()).into(ivProfilePicture);
            } else {
                Picasso.with(getContext()).load(R.mipmap.ic_launcher).
                        transform(new CropCircleTransformation()).into(ivProfilePicture);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tvType = holder.tvType;
        tvType.setText("#" + task.getType());

        tvTaskTitle = holder.tvTaskTitle;
        tvTaskTitle.setText(task.getTitle());

        tvPrice = holder.tvPrice;
        tvPrice.setText(NumberFormat.getCurrencyInstance().format(task.getBudget()));
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemDismiss(int position) {
        String taskId = mTasks.get(position).getObjectId();

        mTasks.remove(position);
        notifyItemRemoved(position);

        swipeListener.onSwipeDelete(taskId);
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder {
        public ImageView ivProfilePicture;
        public TextView tvType;
        public TextView tvTaskTitle;
        public TextView tvPrice;
        public ImageView ivBidderTag;

        public ViewHolder(final View itemView) {
            super(itemView);

            ivProfilePicture = (ImageView) itemView.findViewById(R.id.ivProfilePicture);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
            tvTaskTitle = (TextView) itemView.findViewById(R.id.tvTaskTitle);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            ivBidderTag = (ImageView) itemView.findViewById(R.id.ivBidderTag);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onItemClick(itemView, getLayoutPosition());
                    }
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
