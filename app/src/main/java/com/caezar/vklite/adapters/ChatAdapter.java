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

import static android.view.View.TEXT_ALIGNMENT_VIEW_END;
import static android.view.View.TEXT_ALIGNMENT_VIEW_START;

/**
 * Created by seva on 03.04.18 in 15:40.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DialogMessage> items;
    private int myselfId;

    public ChatAdapter() {
        items = new ArrayList<>();
        myselfId = MetaInfo.getMyselfId();
    }

    static final int MESSAGE_TEXT = R.layout.message_text;
    static final int MESSAGE_IMAGE = R.layout.message_image;

    private static final int LEFT_MESSAGE = 1;
    private static final int LEFT_IMAGE = 2;
    private static final int RIGHT_MESSAGE = 3;
    private static final int RIGHT_IMAGE = 4;

    public void addItemsToTop(List<DialogMessage> itemList) {
        items.addAll(0, itemList);
        // todo: почему это не работает? появляются дубликаты
//        notifyItemInserted(0);
        notifyDataSetChanged();
    }

    public void addItemToEnd(DialogMessage dialogMessage) {
        items.add(dialogMessage);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        switch (viewType) {
            case LEFT_MESSAGE:
            case RIGHT_MESSAGE:
                View messageTextView = LayoutInflater.from(context).inflate(MESSAGE_TEXT, parent, false);
                return new MessageTextViewHolder(messageTextView);
            case LEFT_IMAGE:
            case RIGHT_IMAGE:
                View messageImageView = LayoutInflater.from(context).inflate(MESSAGE_IMAGE, parent, false);
                return new MessageTextViewHolder(messageImageView);

            default:
                throw new IllegalArgumentException("invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DialogMessage item = items.get(position);
        Context context = holder.itemView.getContext();

        int textAlign;

        switch (getItemViewType(position)) {
            case LEFT_MESSAGE:
            case RIGHT_MESSAGE:
                String message = item.getBody();
                MessageTextViewHolder messageTextViewHolder = ((MessageTextViewHolder) holder);
                messageTextViewHolder.message.setText(message);

                textAlign = getItemViewType(position) == LEFT_MESSAGE ? TEXT_ALIGNMENT_VIEW_START : TEXT_ALIGNMENT_VIEW_END;
                messageTextViewHolder.message.setTextAlignment(textAlign);

                break;
            case LEFT_IMAGE:
            case RIGHT_IMAGE:
                MessageImageHolder messageImageHolder = ((MessageImageHolder) holder);
                Glide.with(context).load(item.getAttachments()[0].getPhoto().getPhoto_604()).into(messageImageHolder.imageMessage);

                textAlign = getItemViewType(position) == LEFT_IMAGE ? TEXT_ALIGNMENT_VIEW_START : TEXT_ALIGNMENT_VIEW_END;
                messageImageHolder.imageMessage.setTextAlignment(textAlign);

                break;
            default:
                throw new IllegalArgumentException("invalid view type");
        }
    }

    @Override
    public int getItemViewType(int position) {
        DialogMessage item = items.get(position);
        boolean side = item.getFrom_id() == myselfId;

        if (item.getAttachments() != null) {
            // todo: switch
            if (item.getAttachments()[0].getType().equals("photo")) {
                return side ? RIGHT_IMAGE : LEFT_IMAGE;
            }
        }

        return side ? RIGHT_MESSAGE : LEFT_MESSAGE;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MessageTextViewHolder extends RecyclerView.ViewHolder {

        TextView message;

        MessageTextViewHolder(final View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.messageText);
        }
    }

    class MessageImageHolder extends RecyclerView.ViewHolder {

        ImageView imageMessage;

        MessageImageHolder(final View itemView) {
            super(itemView);

            imageMessage = itemView.findViewById(R.id.messageImage);
        }
    }

}