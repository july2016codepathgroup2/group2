package com.pensum.pensumapplication.adapters;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.activities.HomeActivity;
import com.pensum.pensumapplication.helpers.FormatterHelper;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Stat;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by violetaria on 8/24/16.
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.ivNewMessage) ImageView ivNewMessage;
        @BindView(R.id.rbRating) RatingBar rbRating;
        @BindView(R.id.tvOffer) TextView tvOffer;
        @BindView(R.id.btnMessage) Button btnMessage;

        public ViewHolder (View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private List<Conversation> conversations;
    private Context context;
//    private static OnItemClickListener listener;

    public ConversationAdapter(Context context, List<Conversation> conversations){
        this.context = context;
        this.conversations = conversations;
    }

    private Context getContext(){
        return context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View convoView = inflater.inflate(R.layout.item_conversation, parent, false);

        ViewHolder viewHolder = new ViewHolder(convoView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Conversation conversation = conversations.get(position);
        ParseUser candidate = conversation.getCandidate();

        TextView tvName = holder.tvName;
        try {
            tvName.setText(FormatterHelper.formatName(candidate.fetchIfNeeded().getString("fbName")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ImageView ivProfileImage = holder.ivProfileImage;
        String imageUrl = candidate.getString("profilePicUrl");
        if (imageUrl != null){
            Picasso.with(getContext()).load(imageUrl).
                    transform(new CropCircleTransformation()).into(ivProfileImage);
        } else {
            Picasso.with(getContext()).load(R.mipmap.ic_launcher).
                    transform(new CropCircleTransformation()).into(ivProfileImage);
        }

        RatingBar rbRating = holder.rbRating;
        Stat candidateStat = null;
        try {
            candidateStat = (Stat) conversation.getCandidate().getParseObject("stats").fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (candidateStat != null) {
            rbRating.setRating(Float.parseFloat(candidateStat.getRating().toString()));
        }

        TextView tvOffer = holder.tvOffer;
        if (conversation.getOffer() != null){
            tvOffer.setText(NumberFormat.getCurrencyInstance().format(conversation.getOffer()));
        }

        ImageView ivNewMessage = holder.ivNewMessage;
        if (conversation.getOwner() == ParseUser.getCurrentUser() && conversation.getUnreadOwnerMessageFlag()){
            ivNewMessage.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_mail_outline_black_24dp, null));
        } else if (conversation.getCandidate() == ParseUser.getCurrentUser() && conversation.getUnreadCandidateMessageFlag()){
            ivNewMessage.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_mail_outline_black_24dp, null));
        }

        Button btnMessage = holder.btnMessage;
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity homeActivity = (HomeActivity) getContext();
                homeActivity.launchChatFragment(conversation);
            }
        });
    }

    public void remove(int position) {
       Conversation c =  conversations.get(position);
        c.setStatus("declined");
        c.saveInBackground();
        conversations.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public int getItemCount() {
        return conversations.size();
    }
}
