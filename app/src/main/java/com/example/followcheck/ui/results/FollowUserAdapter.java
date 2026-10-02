package com.example.followcheck.ui.results;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followcheck.R;
import com.example.followcheck.data.model.InstagramUser;

public class FollowUserAdapter extends ListAdapter<InstagramUser, FollowUserAdapter.ViewHolder> {

    private final String statusText;

    public FollowUserAdapter(String statusText) {
        super(new DiffUtil.ItemCallback<InstagramUser>() {
            @Override
            public boolean areItemsTheSame(@NonNull InstagramUser oldItem, @NonNull InstagramUser newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull InstagramUser oldItem, @NonNull InstagramUser newItem) {
                return oldItem.getUsername().equals(newItem.getUsername()) &&
                       oldItem.getFullName().equals(newItem.getFullName()) &&
                       oldItem.isVerified() == newItem.isVerified();
            }
        });
        this.statusText = statusText;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follow_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InstagramUser user = getItem(position);
        holder.textUsername.setText("@" + user.getUsername());
        holder.textFullName.setText(user.getFullName());
        holder.textStatus.setText(statusText);
        holder.imgVerified.setVisibility(user.isVerified() ? View.VISIBLE : View.GONE);
        // Avatar placeholder is handled by ShapeableImageView background/tint in XML
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textUsername, textFullName, textStatus;
        ImageView imgVerified;

        ViewHolder(View view) {
            super(view);
            textUsername = view.findViewById(R.id.text_username);
            textFullName = view.findViewById(R.id.text_full_name);
            textStatus = view.findViewById(R.id.text_status);
            imgVerified = view.findViewById(R.id.img_verified);
        }
    }
}
