package com.caezar.vklite.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caezar.vklite.R;
import com.caezar.vklite.activities.ChatActivity;
import com.caezar.vklite.network.models.DialogsResponse.Response.DialogItem;

import java.util.List;

/**
 * Created by seva on 01.04.18 in 18:12.
 */

public class DialogsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
                    Glide.with(context).load(imageUrl).into(dialogViewHolder.avatar);
                } else {
                    Glide.with(context).load(R.drawable.default_avatar).into(dialogViewHolder.avatar);
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
            avatar = itemView.findViewById(R.id.dialog_avatar);
            title = itemView.findViewById(R.id.dialog_title);
            message = itemView.findViewById(R.id.dialog_message);

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
            int position = holder.getLayoutPosition();
            if (position != RecyclerView.NO_POSITION) {
                DialogItem item = items.get(position);

                int peer_id;
                if (item.getMessage().getChat_id() == 0) {
                    peer_id = item.getMessage().getUser_id();

                } else {
                    peer_id = 2000000000 + item.getMessage().getChat_id();
                }

                Context context = holder.itemView.getContext();

                Intent intent = new Intent(context, ChatActivity.class);
                Bundle b = new Bundle();
                b.putInt("peer_id", peer_id);
                b.putString("title", item.getMessage().getTitle());
                intent.putExtras(b);
                context.startActivity(intent);
            }
        }
    }
}