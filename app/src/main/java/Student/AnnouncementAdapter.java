package Student;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lms.R;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Models.Announcement;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {
    private List<Announcement> announcements;
    private Context context;
    private OnAnnouncementClickListener listener;

    public interface OnAnnouncementClickListener {
        void onAnnouncementClick(Announcement announcement);
    }

    public AnnouncementAdapter(Context context, List<Announcement> announcements) {
        this.context = context;
        this.announcements = announcements;
    }

    public void setOnAnnouncementClickListener(OnAnnouncementClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.annc_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);


        holder.titleView.setText(announcement.getTitle());


        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.dateView.setText(dateFormat.format(announcement.getDate()));


        holder.descriptionView.setText(announcement.getDescription());


        String authorText = announcement.getAuthorName();
        if (!TextUtils.isEmpty(announcement.getAuthorRole())) {
            authorText += " • " + announcement.getAuthorRole();
        }
        holder.authorView.setText(authorText);


        if (announcement.isRead()) {
            holder.unreadIndicator.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.background_secondary));
        } else {
            holder.unreadIndicator.setVisibility(View.VISIBLE);
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.background_tertiary));
        }


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAnnouncementClick(announcement);
            }

            // mark as read
            if (!announcement.isRead()) {
                announcement.setRead(true);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    public void updateAnnouncements(List<Announcement> newAnnouncements) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new AnnouncementDiffCallback(this.announcements, newAnnouncements));
        this.announcements = new ArrayList<>(newAnnouncements);
        diffResult.dispatchUpdatesTo(this);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView titleView;
        TextView dateView;
        TextView descriptionView;
        TextView authorView;
        View unreadIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            titleView = itemView.findViewById(R.id.announcement_title);
            dateView = itemView.findViewById(R.id.announcement_date);
            descriptionView = itemView.findViewById(R.id.announcement_description);
            authorView = itemView.findViewById(R.id.announcement_author);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
        }
    }

    private static class AnnouncementDiffCallback extends DiffUtil.Callback {
        private final List<Announcement> oldList;
        private final List<Announcement> newList;

        AnnouncementDiffCallback(List<Announcement> oldList, List<Announcement> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            // Assuming Announcement has a unique ID
            return oldList.get(oldItemPosition).getTitle().equals(
                    newList.get(newItemPosition).getTitle());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Announcement oldItem = oldList.get(oldItemPosition);
            Announcement newItem = newList.get(newItemPosition);
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getDate().equals(newItem.getDate()) &&
                    oldItem.isRead() == newItem.isRead();
        }
    }
}
