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
import com.pensum.pensumapplication.helpers.view.MessageBubbleDrawable;
import com.pensum.pensumapplication.models.Message;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

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

        MessageBubbleDrawable messageBubbleDrawable;
        if (isMe) {
            viewHolder.ivProfileMe.setVisibility(View.VISIBLE);
            viewHolder.ivProfileOther.setVisibility(View.GONE);
            messageBubbleDrawable = new MessageBubbleDrawable(getContext(),
                    R.color.colorPrimaryLight, MessageBubbleDrawable.Gravity.END);
            viewHolder.tvBody.setBackground(messageBubbleDrawable);
            viewHolder.tvBody.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        } else {
            viewHolder.ivProfileOther.setVisibility(View.VISIBLE);
            viewHolder.ivProfileMe.setVisibility(View.GONE);

            messageBubbleDrawable = new MessageBubbleDrawable(getContext(),
                    R.color.colorPrimaryLight, MessageBubbleDrawable.Gravity.START);
            viewHolder.tvBody.setBackground(messageBubbleDrawable);
            viewHolder.tvBody.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        }

        ImageView imageProfilePicture = isMe ? viewHolder.ivProfileMe : viewHolder.ivProfileOther;
        String profilePictureUrl = null;
        try {
            profilePictureUrl = String.valueOf(message.getFrom().fetchIfNeeded().get("profilePicUrl"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (profilePictureUrl != null) {
            Picasso.with(getContext()).load(profilePictureUrl).
                    transform(new CropCircleTransformation()).into(imageProfilePicture);
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
        @BindView(R.id.ivProfileOther) ImageView ivProfileOther;
        @BindView(R.id.ivProfileMe) ImageView ivProfileMe;
        @BindView(R.id.tvMessageBodyOther)
        TextView tvBody;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
