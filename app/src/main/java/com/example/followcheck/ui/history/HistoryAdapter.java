package com.example.followcheck.ui.history;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followcheck.R;
import com.example.followcheck.data.model.FollowSnapshot;

import java.util.Date;

public class HistoryAdapter extends ListAdapter<FollowSnapshot, HistoryAdapter.ViewHolder> {

    private final OnSnapshotClickListener listener;

    public interface OnSnapshotClickListener {
        void onSnapshotClick(FollowSnapshot snapshot);
    }

    public HistoryAdapter(OnSnapshotClickListener listener) {
        super(new DiffUtil.ItemCallback<FollowSnapshot>() {
            @Override
            public boolean areItemsTheSame(@NonNull FollowSnapshot oldItem, @NonNull FollowSnapshot newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull FollowSnapshot oldItem, @NonNull FollowSnapshot newItem) {
                return oldItem.getTimestamp() == newItem.getTimestamp() &&
                        oldItem.getFollowersCount() == newItem.getFollowersCount() &&
                        oldItem.getFollowingCount() == newItem.getFollowingCount();
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_snapshot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FollowSnapshot snapshot = getItem(position);
        Date date = new Date(snapshot.getTimestamp());
        holder.textDate.setText(DateFormat.format("MMMM d, yyyy", date));
        holder.textTime.setText(DateFormat.format("h:mm a", date));
        holder.valFollowers.setText(String.valueOf(snapshot.getFollowersCount()));
        holder.valFollowing.setText(String.valueOf(snapshot.getFollowingCount()));

        holder.itemView.setOnClickListener(v -> listener.onSnapshotClick(snapshot));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textTime, valFollowers, valFollowing;

        ViewHolder(View view) {
            super(view);
            textDate = view.findViewById(R.id.text_date);
            textTime = view.findViewById(R.id.text_time);
            valFollowers = view.findViewById(R.id.val_followers);
            valFollowing = view.findViewById(R.id.val_following);
        }
    }
}
