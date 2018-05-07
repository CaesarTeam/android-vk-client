package com.caezar.vklite.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caezar.vklite.R;
import com.caezar.vklite.fragments.DialogsFragment;
import com.caezar.vklite.models.network.DialogItem;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import static com.caezar.vklite.Config.minItemsToRequestDialogs;
import static com.caezar.vklite.helpers.DialogsHelper.getBody;
import static com.caezar.vklite.helpers.DialogsHelper.getPeerId;
import static com.caezar.vklite.libs.ImageLoader.asyncImageLoad;
import static com.caezar.vklite.libs.ImageLoader.getUrlForResource;
import static com.caezar.vklite.libs.Time.getDateTimeForDialog;

/**
 * Created by seva on 01.04.18 in 18:12.
 */

public class DialogsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DIALOG = 1;
    private final int ANIMATE_DURATION_CLOSE_CHAT_TIME = 1500;

    @NonNull private final List<DialogItem> items = new ArrayList<>();
    private final Context context;
    private final DialogsFragment.DialogsCallbacks dialogsCallbacks;

    public DialogsAdapter(DialogsFragment.DialogsCallbacks dialogsCallbacks, Context context) {
        this.dialogsCallbacks = dialogsCallbacks;
        this.context = context;
    }

    public void addItemsToEnd(List<DialogItem> dialogItems) {
        if (dialogItems != null) {
            items.addAll(dialogItems);
            notifyDataSetChanged();
        }
    }

    public void setItems(List<DialogItem> dialogItems) {
        if (dialogItems != null) {
            items.clear();
            items.addAll(dialogItems);
            notifyDataSetChanged();
        }
    }

    public void animateClosedChat(RecyclerView.ViewHolder viewHolder) {
        DialogViewHolder dialogViewHolder = ((DialogViewHolder) viewHolder);
        final LinearLayout dialogContainer = dialogViewHolder.dialogContainer;

        int colorFrom = context.getResources().getColor(R.color.colorDialog);
        int colorTo = context.getResources().getColor(R.color.colorDialogAnimateBoundary);

        ValueAnimator animator = new ValueAnimator();
        animator.setIntValues(colorTo, colorFrom);
        animator.setEvaluator(new ArgbEvaluator());
        animator.addUpdateListener(valueAnimator -> {
            if (dialogContainer != null) {
                dialogContainer.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            } else {
                animator.cancel();
            }
        });
        animator.setDuration(ANIMATE_DURATION_CLOSE_CHAT_TIME);
        animator.start();
    }

    @NonNull
    public List<DialogItem> getItems() {
        return items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        switch (viewType) {
            case ITEM_DIALOG:
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog, parent, false);
                return new DialogViewHolder(dialogView);

            default:
                throw new IllegalArgumentException("invalid view type");
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DialogItem item = items.get(position);
        int unreadMessagesCount = item.getUnread();
        boolean readState = item.getMessage().isRead_state();

        switch (getItemViewType(position)) {
            case ITEM_DIALOG:
                DialogViewHolder dialogViewHolder = ((DialogViewHolder) holder);
                String imageUrl = item.getMessage().getPhoto_100();

                if (imageUrl != null) {
                    asyncImageLoad(imageUrl, dialogViewHolder.avatar);
                } else {
                    asyncImageLoad(getUrlForResource(R.drawable.default_avatar), dialogViewHolder.avatar);
                }

                if (item.isOnline()) {
                    dialogViewHolder.avatar.setBorderWidth(4.f);
                    dialogViewHolder.avatar.setBorderColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                } else {
                    dialogViewHolder.avatar.setBorderWidth(0.f);
                    dialogViewHolder.avatar.clearColorFilter();
                }

                String body = getBody(item.getMessage(), context);
                if (!body.equals(item.getMessage().getBody())) {
                    dialogViewHolder.message.setTextColor(ContextCompat.getColor(context, R.color.colorDialogMessageNotText));
                } else {
                    dialogViewHolder.message.setTextColor(ContextCompat.getColor(context, R.color.colorDialogMessageDefault));
                }

                if (unreadMessagesCount == 0) {
                    dialogViewHolder.unreadCount.setVisibility(View.INVISIBLE);
                    if (!readState) {
                        dialogViewHolder.readState.setVisibility(View.VISIBLE);
                    } else {
                        dialogViewHolder.readState.setVisibility(View.INVISIBLE);
                    }
                } else {
                    dialogViewHolder.unreadCount.setText(Integer.toString(unreadMessagesCount));
                    dialogViewHolder.unreadCount.setVisibility(View.VISIBLE);
                    dialogViewHolder.readState.setVisibility(View.INVISIBLE);
                }

                dialogViewHolder.message.setText(body);
                dialogViewHolder.title.setText(item.getMessage().getTitle());
                dialogViewHolder.time.setText(getDateTimeForDialog(item.getMessage().getDate(), context));
                break;

            default:
                throw new IllegalArgumentException("invalid view type");
        }

        if (isTimeToRequestDialogs(position)) {
            dialogsCallbacks.getMoreDialogs(getItemCount());
        }
    }

    private boolean isTimeToRequestDialogs(int position) {
        return items.size() - position < minItemsToRequestDialogs;
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_DIALOG;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class DialogViewHolder extends RecyclerView.ViewHolder {

        final LinearLayout dialogContainer;
        final RoundedImageView avatar;
        final TextView title;
        final TextView message;
        final TextView time;
        final TextView unreadCount;
        final TextView readState;

        DialogViewHolder(final View itemView) {
            super(itemView);

            dialogContainer = itemView.findViewById(R.id.dialogContainer);
            avatar = itemView.findViewById(R.id.dialogAvatar);
            title = itemView.findViewById(R.id.dialogTitle);
            message = itemView.findViewById(R.id.dialogMessage);
            time = itemView.findViewById(R.id.dialogTime);
            unreadCount = itemView.findViewById(R.id.dialogUnreadCount);
            readState = itemView.findViewById(R.id.dialogReadState);

            itemView.setOnClickListener(new onDialogClickListener(this));
        }
    }

    class onDialogClickListener implements View.OnClickListener {
        final DialogViewHolder holder;

        onDialogClickListener(DialogViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            int position = holder.getLayoutPosition();
            if (position != RecyclerView.NO_POSITION) {
                DialogItem item = items.get(position);
                int peer_id = getPeerId(item);
                String title = item.getMessage().getTitle();
                holder.dialogContainer.setBackgroundColor(context.getResources().getColor(R.color.colorDialog));
                dialogsCallbacks.openChat(peer_id, title);
            }
        }
    }
}