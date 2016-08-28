package com.pensum.pensumapplication.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.models.Message;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private Context mContext;
    private List<Message> mMessages;

    public ChatListAdapter(Context context, List<Message> messages) {
        this.mContext = context;
        this.mMessages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View chatView = inflater.inflate(R.layout.item_chat, parent, false);

        ViewHolder viewHolder = new ViewHolder(chatView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = mMessages.get(position);
        boolean isMe = message.getFrom().getObjectId() != null &&
                message.getFrom().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());

        if (isMe) {
            viewHolder.ivProfileMe.setVisibility(View.VISIBLE);
            viewHolder.ivProfileOther.setVisibility(View.GONE);
            viewHolder.tvBody.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        } else {
            viewHolder.ivProfileOther.setVisibility(View.VISIBLE);
            viewHolder.ivProfileMe.setVisibility(View.GONE);

            viewHolder.tvBody.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        }

        ImageView imageProfilePicture = isMe ? viewHolder.ivProfileMe : viewHolder.ivProfileOther;
        String profilePictureUrl = null;
        try {
            profilePictureUrl = isMe ? String.valueOf(message.getFrom().fetchIfNeeded().get("profilePicUrl")) :
                    String.valueOf(message.getTo().fetchIfNeeded().get("profilePicUrl"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (profilePictureUrl != null) {
            Picasso.with(getContext()).load(profilePictureUrl).into(imageProfilePicture);
        }

        viewHolder.tvBody.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    private Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileOther;
        public ImageView ivProfileMe;
        public TextView tvBody;

        public ViewHolder(View itemView) {
            super(itemView);

            ivProfileOther = (ImageView) itemView.findViewById(R.id.ivProfileOther);
            ivProfileMe = (ImageView) itemView.findViewById(R.id.ivProfileMe);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
        }
    }
}
