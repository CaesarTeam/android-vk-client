package com.caezar.vklite.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caezar.vklite.R;
import com.caezar.vklite.network.MetaInfo;
import com.caezar.vklite.network.models.DialogMessage;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.TEXT_ALIGNMENT_VIEW_START;

/**
 * Created by seva on 03.04.18 in 15:40.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DialogMessage> items;

    public ChatAdapter() {
        items = new ArrayList<>();
    }

    public static final String TAG = "ChatAdapter";

    static final int MESSAGE = R.layout.message;
    static final int LEFT_MESSAGE = 1;
    static final int LEFT_IMAGE = 2;
    static final int RIGHT_MESSAGE = 3;
    static final int RIGHT_IMAGE = 4;

    public void addDataToTop(List<DialogMessage> itemList) {
        items.addAll(0, itemList);
//        notifyItemInserted(0); почему это не работает? появляются дубликаты
        notifyDataSetChanged();
    }

    public void addDataToEnd(DialogMessage dialogMessage) {
        items.add(dialogMessage);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        switch (viewType) {
            case LEFT_MESSAGE:
            case RIGHT_MESSAGE:
            case LEFT_IMAGE:
            case RIGHT_IMAGE:
                View messageView = LayoutInflater.from(context).inflate(MESSAGE, parent, false);
                return new MessageViewHolder(messageView);

            default:
                throw new IllegalArgumentException("invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DialogMessage item = items.get(position);
        MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);

        String message = item.getBody();
        messageViewHolder.message.setText(message);
        Context context = holder.itemView.getContext();

        switch (getItemViewType(position)) {
            case LEFT_MESSAGE:

                messageViewHolder.message.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                break;
            case RIGHT_MESSAGE:

                messageViewHolder.message.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                break;
            case RIGHT_IMAGE:
                messageViewHolder.imageView.setVisibility(View.VISIBLE);
                Glide.with(context).load(item.getAttachments()[0].getPhoto().getPhoto_604()).into(messageViewHolder.imageView);
                messageViewHolder.imageView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                break;
            case LEFT_IMAGE:
                messageViewHolder.imageView.setVisibility(View.VISIBLE);
                Glide.with(context).load(item.getAttachments()[0].getPhoto().getPhoto_604()).into(messageViewHolder.imageView);
                messageViewHolder.imageView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                break;
            default:
                throw new IllegalArgumentException("invalid view type");
        }
    }

    @Override
    public int getItemViewType(int position) {
        DialogMessage item = items.get(position);
        boolean side = item.getFrom_id() == MetaInfo.getMyselfId();

        if (item.getAttachments() != null) {
            // todo: switch
            if (item.getAttachments()[0].getType().equals("photo")) {
                if (side) {
                    return RIGHT_IMAGE;
                }

                return LEFT_IMAGE;
            }
        }

        if (side) {
            return RIGHT_MESSAGE;
        }

        return LEFT_MESSAGE;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView message;
        ImageView imageView;

        MessageViewHolder(final View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.message);
            imageView = itemView.findViewById(R.id.messageImage);
        }
    }

//    class AddRemoveClickListener implements View.OnClickListener {
//        DialogViewHolder holder;
//
//        AddRemoveClickListener(DialogViewHolder holder) {
//            this.holder = holder;
//        }
//
//        @Override
//        public void onClick(View v) {
//            int position = holder.getLayoutPosition();
//            if (position != RecyclerView.NO_POSITION) {
//                DialogsResponse.Response.DialogItem item = items.get(position);
//
//                Log.d(TAG, items.get(position).getMessage().getTitle());
//
//                // todo: open dialog
//            }
//        }
//    }
}