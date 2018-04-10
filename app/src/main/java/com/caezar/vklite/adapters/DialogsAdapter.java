package com.caezar.vklite.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caezar.vklite.R;
import com.caezar.vklite.activities.ChatActivity;
import com.caezar.vklite.network.models.DialogsResponse.Response.DialogItem;

import java.util.List;

import static com.caezar.vklite.libs.ImageLoader.asyncImageLoad;
import static com.caezar.vklite.libs.ImageLoader.getUrlForResource;

/**
 * Created by seva on 01.04.18 in 18:12.
 */

public class DialogsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String PHOTO_PARTICIPANTS = "photoParticipants";
    public static final String TITLE = "title";
    public static final String PEER_ID = "peer_id";
    public static final String IS_PRVATE_DIALOG = "isPrivateDialog";

    private List<DialogItem> items;

    public DialogsAdapter(List<DialogItem> dialogItems) {
        items = dialogItems;
    }

    static final int ITEM_DIALOGS = R.layout.dialog;

    public void changeItems(List<DialogItem> dialogItems) {
        items = dialogItems;
        notifyDataSetChanged();
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
        Context context = holder.itemView.getContext();

        switch (getItemViewType(position)) {
            case ITEM_DIALOGS:
                DialogViewHolder dialogViewHolder = ((DialogViewHolder) holder);

                String title = item.getMessage().getTitle();
                String message = item.getMessage().getBody();
                String imageUrl = item.getMessage().getPhoto_100();

                if(imageUrl != null) {
                    asyncImageLoad(context, imageUrl, dialogViewHolder.avatar);
                } else {
                    asyncImageLoad(context, getUrlForResource(R.drawable.default_avatar), dialogViewHolder.avatar);
                }

                dialogViewHolder.title.setText(title);
                dialogViewHolder.message.setText(message);
                break;

            default:
                throw new IllegalArgumentException("invalid view type");
        }
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

        DialogViewHolder(final View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.dialogAvatar);
            title = itemView.findViewById(R.id.dialogTitle);
            message = itemView.findViewById(R.id.dialogMessage);

            itemView.setOnClickListener(new AddRemoveClickListener(this));
        }
    }

    class AddRemoveClickListener implements View.OnClickListener {
        DialogViewHolder holder;

        AddRemoveClickListener(DialogViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            Context context = holder.itemView.getContext();
            int position = holder.getLayoutPosition();
            if (position != RecyclerView.NO_POSITION) {
                DialogItem item = items.get(position);

                int peer_id;
                boolean isPrivateDialog = false;

                if (item.getMessage().getChat_id() == 0) {
                    peer_id = item.getMessage().getUser_id();
                    isPrivateDialog = true;

                } else {
                    peer_id = Integer.parseInt(context.getString(R.string.peer_id_constant)) + item.getMessage().getChat_id();
                }

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(PEER_ID, peer_id);
                intent.putExtra(TITLE, item.getMessage().getTitle());
                intent.putExtra(IS_PRVATE_DIALOG, isPrivateDialog);
                intent.putExtra(PHOTO_PARTICIPANTS, item.getMessage().getChat_active());
                context.startActivity(intent);
            }
        }
    }
}