package com.caezar.vklite.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caezar.vklite.R;
import com.caezar.vklite.activities.DialogsActivity;
import com.caezar.vklite.models.network.DialogItem;
import com.caezar.vklite.models.network.DialogMessage;

import java.util.ArrayList;
import java.util.List;

import static com.caezar.vklite.Config.minItemsToRequestDialogs;
import static com.caezar.vklite.libs.DialogsHelper.getActionMessage;
import static com.caezar.vklite.libs.DialogsHelper.getAttachmentsMessage;
import static com.caezar.vklite.libs.DialogsHelper.getPeerId;
import static com.caezar.vklite.libs.ImageLoader.asyncImageLoad;
import static com.caezar.vklite.libs.ImageLoader.getUrlForResource;
import static com.caezar.vklite.libs.Time.getDateTimeForDialog;

/**
 * Created by seva on 01.04.18 in 18:12.
 */

public class DialogsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DIALOG = 1;

    @NonNull private final List<DialogItem> items = new ArrayList<>();
    private Context context;

    public DialogsAdapter(Context context) {
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

    @NonNull
    public List<DialogItem> getItems() {
        return items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        switch (viewType) {
            case ITEM_DIALOG:
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog, parent, false);
                return new DialogViewHolder(dialogView);

            default:
                throw new IllegalArgumentException("invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DialogItem item = items.get(position);

        switch (getItemViewType(position)) {
            case ITEM_DIALOG:
                DialogViewHolder dialogViewHolder = ((DialogViewHolder) holder);
                dialogViewHolder.bind(item);
                break;

            default:
                throw new IllegalArgumentException("invalid view type");
        }

        if (isTimeToRequestDialogs(position)) {
            ((DialogsActivity)context).getDialogsCallback(getItemCount());
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

        ImageView avatar;
        TextView title;
        TextView message;
        TextView time;

        DialogViewHolder(final View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.dialogAvatar);
            title = itemView.findViewById(R.id.dialogTitle);
            message = itemView.findViewById(R.id.dialogMessage);
            time = itemView.findViewById(R.id.dialogTime);

            itemView.setOnClickListener(new onDialogClickListener(this));
        }

        private void bind(DialogItem item) {
            String imageUrl = item.getMessage().getPhoto_100();

            if (imageUrl != null) {
                asyncImageLoad(imageUrl, avatar);
            } else {
                asyncImageLoad(getUrlForResource(R.drawable.default_avatar), avatar);
            }

            String body = getBody(item.getMessage());
            if (!body.equals(item.getMessage().getBody())) {
                message.setTextColor(ContextCompat.getColor(context, R.color.colorDialogNotMessageText));
            } else {
                message.setTextColor(ContextCompat.getColor(context, R.color.colorDefaultForTextView));
            }
            message.setText(body);
            title.setText(item.getMessage().getTitle());
            time.setText(getDateTimeForDialog(item.getMessage().getDate(), context));
        }

        private String getBody(DialogMessage dialogMessage) {

            String attachmentTypeMessage = getAttachmentsMessage(dialogMessage, context);
            if (attachmentTypeMessage != null) {
                return attachmentTypeMessage;
            }

            String actionTypeMessage = getActionMessage(dialogMessage, context);
            if (actionTypeMessage != null) {
                return actionTypeMessage;
            }

            if (dialogMessage.getFwd_messages() != null) {
                return context.getString(R.string.messageTypeForwardMessage);
            }

            return dialogMessage.getBody();
        }
    }

    class onDialogClickListener implements View.OnClickListener {
        DialogViewHolder holder;

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

                if (context instanceof DialogsActivity) {
                    ((DialogsActivity)context).openChatCallback(peer_id, title);
                }
            }
        }
    }
}