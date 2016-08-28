package com.pensum.pensumapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.helpers.FormatterHelper;
import com.pensum.pensumapplication.models.Conversation;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by violetaria on 8/28/16.
 */
public class CandidateSpinnerAdapter extends ArrayAdapter {
    private List<Conversation> conversations;
    Conversation conversation;
    private Context context;

    public CandidateSpinnerAdapter(Context context, int resource, List<Conversation> conversations) {
        super(context, resource);

        this.context = context;
        this.conversations = conversations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.candidate_spinner_item_top, parent, false);
        }

        TextView tvName = (TextView) row.findViewById(R.id.tvName);
        try {
            ParseUser candidate = conversation.getCandidate().fetchIfNeeded();
            tvName.setText(FormatterHelper.formatName(candidate.getString("fbName")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return row;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.candidate_spinner_item, parent, false);
        }

        conversation = null;
        conversation = conversations.get(position);

        TextView tvName = (TextView) row.findViewById(R.id.tvName);
        TextView tvOffer = (TextView) row.findViewById(R.id.tvBudget);
        ImageView ivProfileImage = (ImageView) row.findViewById(R.id.ivProfileImage);

        try {
            ParseUser candidate = conversation.getCandidate().fetchIfNeeded();
            tvName.setText(FormatterHelper.formatName(candidate.getString("fbName")));
            tvOffer.setText(FormatterHelper.formatMoney(conversation.getOffer().toString()));
            String imageUrl = candidate.getString("profilePicUrl");
            if (imageUrl != null) {
                Picasso.with(getContext()).load(imageUrl).
                        transform(new CropCircleTransformation()).into(ivProfileImage);
            } else {
                Picasso.with(getContext()).load(R.mipmap.ic_launcher).
                        transform(new CropCircleTransformation()).into(ivProfileImage);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return row;
    }

}
