package com.caezar.vklite.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caezar.vklite.R;
import com.caezar.vklite.network.models.DialogsResponse;
import com.caezar.vklite.network.modelsResponse.DialogMessage;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.TEXT_ALIGNMENT_VIEW_START;

/**
 * Created by seva on 03.04.18 in 15:40.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DialogMessage> items;
    private final int myId = 105532261;

    public ChatAdapter() {
        items = new ArrayList<DialogMessage>();
    }

    public static final String TAG = "ChatAdapter";

    static final int MESSAGE = R.layout.message;
    static final int LEFT_MESSAGE = 1;
    static final int RIGHT_MESSAGE = 2;

    public void addData(List<DialogMessage> itemList) {
        items.addAll(0, itemList);
//        notifyItemInserted(0); почему это не работает? появляются дубликаты
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        switch (viewType) {
            case LEFT_MESSAGE:
            case RIGHT_MESSAGE:
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

        switch (getItemViewType(position)) {
            case LEFT_MESSAGE:


                messageViewHolder.message.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                break;
            case RIGHT_MESSAGE:

                messageViewHolder.message.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                break;
            default:
                throw new IllegalArgumentException("invalid view type");
        }
    }

    @Override
    public int getItemViewType(int position) {
        DialogMessage item = items.get(position);
        if (item.getFrom_id() == myId) {
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

        MessageViewHolder(final View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
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