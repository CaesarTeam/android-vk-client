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
import com.caezar.vklite.activities.DialogsActivity;
import com.caezar.vklite.network.models.DialogsResponse.Response.DialogItem;

import java.util.List;

import static com.caezar.vklite.libs.ImageLoader.asyncImageLoad;
import static com.caezar.vklite.libs.ImageLoader.getUrlForResource;

/**
 * Created by seva on 01.04.18 in 18:12.
 */

public class DialogsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DialogItem> items;
    private Context context;

    public DialogsAdapter(Context context, List<DialogItem> dialogItems) {
        items = dialogItems;
        this.context = context;
    }

    private final int ITEM_DIALOGS = R.layout.dialog;

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

        switch (getItemViewType(position)) {
            case ITEM_DIALOGS:
                DialogViewHolder dialogViewHolder = ((DialogViewHolder) holder);

                String title = item.getMessage().getTitle();
                String message = item.getMessage().getBody();
                String imageUrl = item.getMessage().getPhoto_100();

                if(imageUrl != null) {
                    asyncImageLoad(imageUrl, dialogViewHolder.avatar);
                } else {
                    asyncImageLoad(getUrlForResource(R.drawable.default_avatar), dialogViewHolder.avatar);
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

            itemView.setOnClickListener(new onDialogClickListener(this));
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

                int peer_id;

                if (item.getMessage().getChat_id() == 0) {
                    peer_id = item.getMessage().getUser_id();

                } else {
                    peer_id = Integer.parseInt(context.getString(R.string.peer_id_constant)) + item.getMessage().getChat_id();
                }

                if (context instanceof DialogsActivity){
                    ((DialogsActivity)context).openChat(peer_id, item.getMessage().getTitle(), item.getMessage().getChat_active());
                }
            }
        }
    }
}