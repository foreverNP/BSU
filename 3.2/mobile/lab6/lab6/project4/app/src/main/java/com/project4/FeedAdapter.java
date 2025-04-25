package com.project4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private List<Feed> feedList;
    private Context context;

    public FeedAdapter(List<Feed> feedList, Context context) {
        this.feedList = feedList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Feed feed = feedList.get(position);

        holder.tvFeedName.setText(feed.getName());
        holder.tvFeedPrice.setText(String.format("Цена: %.2f ₽", feed.getPrice()));
        holder.tvFeedManufacturer.setText("Производитель: " + feed.getManufacturerName());
        holder.tvFeedQuantity.setText("Количество: " + feed.getQuantity());
        holder.tvFeedType.setText("Тип: " + feed.getType());
        holder.tvFeedDescription.setText(feed.getDescription());
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public void updateData(List<Feed> newFeedList) {
        this.feedList = newFeedList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFeedName, tvFeedPrice, tvFeedManufacturer,
                tvFeedQuantity, tvFeedType, tvFeedDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFeedName = itemView.findViewById(R.id.tv_feed_name);
            tvFeedPrice = itemView.findViewById(R.id.tv_feed_price);
            tvFeedManufacturer = itemView.findViewById(R.id.tv_feed_manufacturer);
            tvFeedQuantity = itemView.findViewById(R.id.tv_feed_quantity);
            tvFeedType = itemView.findViewById(R.id.tv_feed_type);
            tvFeedDescription = itemView.findViewById(R.id.tv_feed_description);
        }
    }
}