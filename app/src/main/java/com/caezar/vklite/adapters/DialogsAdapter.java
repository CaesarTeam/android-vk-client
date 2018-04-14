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

import java.util.ArrayList;
import java.util.List;

import static com.caezar.vklite.libs.DialogsHelper.getPeerId;
import static com.caezar.vklite.libs.ImageLoader.asyncImageLoad;
import static com.caezar.vklite.libs.ImageLoader.getUrlForResource;
import static com.caezar.vklite.libs.Time.getDateTimeForDialog;

/**
 * Created by seva on 01.04.18 in 18:12.
 */

public class DialogsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DIALOGS = R.layout.dialog;

    @NonNull private List<DialogItem> items;
    private Context context;
    // todo: to config and new name!
    private final int minDifferenceToRequest = 5;

    public DialogsAdapter(Context context) {
        items = new ArrayList<>();
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
            items = new ArrayList<>();
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
            case ITEM_DIALOGS:
                View dialogView = LayoutInflater.from(context).inflate(ITEM_DIALOGS, parent, false);
                return new DialogViewHolder(dialogView);

            default:
                throw new IllegalArgumentException("invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DialogItem item = items.get(position);

        switch (getItemViewType(position)) {
            case ITEM_DIALOGS:
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
        return items.size() - position < minDifferenceToRequest;
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_DIALOGS;
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
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
            // todo: remove костыль
            message.setTextColor(ContextCompat.getColor(context, R.color.colorDefaultForTextView));

            String imageUrl = item.getMessage().getPhoto_100();

            if (imageUrl != null) {
                asyncImageLoad(imageUrl, avatar);
            } else {
                asyncImageLoad(getUrlForResource(R.drawable.default_avatar), avatar);
            }

            String messageDialog = item.getMessage().getBody();
            if (item.getMessage().getAttachments() != null) {
                switch (item.getMessage().getAttachments()[0].getType()) {
                    case STICKER:
                        messageDialog = context.getString(R.string.messageTypeSticker);
                        break;
                    case PHOTO:
                        messageDialog = context.getString(R.string.messageTypePhoto);
                        break;
                    case DOC:
                        messageDialog = context.getString(R.string.messageTypeDoc);
                        break;
                    case GIFT:
                        messageDialog = context.getString(R.string.messageTypeGift);
                        break;
                    case LINK:
                        messageDialog = context.getString(R.string.messageTypeLink);
                        break;
                    case WALL:
                        messageDialog = context.getString(R.string.messageTypeWall);
                        break;
                    case WALL_REPLY:
                        messageDialog = context.getString(R.string.messageTypeWallReply);
                        break;
                    case AUDIO:
                        messageDialog = context.getString(R.string.messageTypeAudio);
                        break;
                    case VIDEO:
                        messageDialog = context.getString(R.string.messageTypeVideo);
                        break;
                    case MARKET:
                        messageDialog = context.getString(R.string.messageTypeMarket);
                        break;
                    case MARKET_ALBUM:
                        messageDialog = context.getString(R.string.messageTypeMarketAlbum);
                        break;
                    default:
                        break;
                }
                message.setTextColor(ContextCompat.getColor(context, R.color.colorDialogNotMessageText));
            }

            if (item.getMessage().getAction() != null) {
                switch (item.getMessage().getAction()) {
                    case CHAT_CREATE:
                        messageDialog = context.getString(R.string.messageTypeChatCreate);
                        break;
                    case CHAT_KICK_USER:
                        messageDialog = context.getString(R.string.messageTypeKickUser);
                        break;
                    case CHAT_INVITE_USER:
                        messageDialog = context.getString(R.string.messageTypeInviteUser);
                        break;
                    case CHAT_PIN_MESSAGE:
                        messageDialog = context.getString(R.string.messageTypePinMessage);
                        break;
                    case CHAT_PHOTO_REMOVE:
                        messageDialog = context.getString(R.string.messageTypeChatPhotoRemove);
                        break;
                    case CHAT_PHOTO_UPDATE:
                        messageDialog = context.getString(R.string.messageTypeChatPhotoUpdate);
                        break;
                    case CHAT_TITLE_UPDATE:
                        messageDialog = context.getString(R.string.messageTypeTitleUpdate);
                        break;
                    case CHAT_UNPIN_MESSAGE:
                        messageDialog = context.getString(R.string.messageTypeUnpinMessage);
                        break;
                    case CHAT_INVITE_USER_BY_LINK:
                        messageDialog = context.getString(R.string.messageTypeInviteUserByLink);
                        break;
                    default:
                        break;
                }
                message.setTextColor(ContextCompat.getColor(context, R.color.colorDialogNotMessageText));
            }

            message.setText(messageDialog);
            title.setText(item.getMessage().getTitle());
            time.setText(getDateTimeForDialog(item.getMessage().getDate(), context));
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