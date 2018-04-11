package com.caezar.vklite.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caezar.vklite.R;
import com.caezar.vklite.activities.ChatActivity;
import com.caezar.vklite.libs.Time;
import com.caezar.vklite.MetaInfo;
import com.caezar.vklite.models.DialogMessage;
import com.caezar.vklite.models.Photo;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caezar.vklite.libs.ImageLoader.asyncImageLoad;

/**
 * Created by seva on 03.04.18 in 15:40.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DialogMessage> items;
    private int myselfId;
    private boolean isPrivateDialog;
    private Map<Integer, String> photoUsers;
    private int prevUserId;
    private int prevPosition;
    private Context context;

    // todo: SuppressLint
    @SuppressLint("UseSparseArrays")
    public ChatAdapter(Context context, boolean isPrivateDialog) {
        items = new ArrayList<>();
        photoUsers = new HashMap<>();
        myselfId = MetaInfo.getMyselfId();
        this.isPrivateDialog = isPrivateDialog;
        this.context = context;
    }

    public void setUsersAvatar(Map<Integer, String> photoUsers) {
        this.photoUsers = photoUsers;
    }

    static final int MESSAGE_TEXT = R.layout.message_text;
    static final int MESSAGE_IMAGE = R.layout.message_image;

    private static final int LEFT_MESSAGE = 1;
    private static final int LEFT_IMAGE = 2;
    private static final int RIGHT_MESSAGE = 3;
    private static final int RIGHT_IMAGE = 4;

    public void addItemsToTop(List<DialogMessage> itemList) {
        items.addAll( itemList);
        // todo: почему это не работает? появляются дубликаты
//        notifyItemInserted(0);
        notifyDataSetChanged();
    }

    public void addItemToEnd(DialogMessage dialogMessage) {
        items.add(0, dialogMessage);
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
                return new MessageImageViewHolder(messageImageView);

            default:
                throw new IllegalArgumentException("invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DialogMessage item = items.get(position);

        final String time = Time.getTime(item.getDate());
        final int userId = item.getUser_id();
        final boolean side = getItemViewType(position) == RIGHT_MESSAGE || getItemViewType(position) == RIGHT_IMAGE;
        final boolean scrollUp = position > prevPosition;

        switch (getItemViewType(position)) {
            case LEFT_MESSAGE:
            case RIGHT_MESSAGE:
                MessageTextViewHolder messageTextViewHolder = ((MessageTextViewHolder) holder);
                messageTextViewHolder.message.setText(item.getBody());
                messageTextViewHolder.messageTextTime.setText(time);

                if (side) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageTextViewHolder.messageTextContainer.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                    messageTextViewHolder.messageTextContainer.setLayoutParams(params);
                } else {
                    if (!isPrivateDialog && photoUsers.containsKey(item.getFrom_id())) {
                        asyncImageLoad(photoUsers.get(item.getFrom_id()), messageTextViewHolder.messageAvatar);
                        final int nextPosition = position + 1;
                        boolean nextItemExist = items.size() == nextPosition;
                        if ((userId != prevUserId && !scrollUp) || nextItemExist || (!nextItemExist && items.get(nextPosition).getUser_id() != userId)) {
                            messageTextViewHolder.messageAvatar.setVisibility(View.VISIBLE);
                        } else {
                            messageTextViewHolder.messageAvatar.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                break;
            case LEFT_IMAGE:
            case RIGHT_IMAGE:
                MessageImageViewHolder messageImageViewHolder = ((MessageImageViewHolder) holder);
                messageImageViewHolder.position = position;

                asyncImageLoad(item.getAttachments()[0].getPhoto().getPhoto_604(), messageImageViewHolder.imageMessage);

                messageImageViewHolder.messageTextTime.setText(time);
                messageImageViewHolder.messageTextTime.bringToFront();

                if (side) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageImageViewHolder.messageTextContainer.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                    messageImageViewHolder.messageTextContainer.setLayoutParams(params);
                } else {
                    asyncImageLoad(photoUsers.get(item.getFrom_id()), messageImageViewHolder.messageAvatar);
                    messageImageViewHolder.messageAvatar.setVisibility(View.VISIBLE);
                }

                break;
            default:
                throw new IllegalArgumentException("invalid view type");
        }

        prevUserId = userId;
        prevPosition = position;
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
        TextView messageTextTime;
        RelativeLayout messageTextContainer;
        RoundedImageView messageAvatar;

        MessageTextViewHolder(final View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.messageText);
            messageTextTime = itemView.findViewById(R.id.messageTextTime);
            messageTextContainer = itemView.findViewById(R.id.messageTextContainer);
            messageAvatar = itemView.findViewById(R.id.messageAvatar);
        }
    }

    class MessageImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageMessage;
        TextView messageTextTime;
        RelativeLayout messageTextContainer;
        RoundedImageView messageAvatar;
        int position;

        MessageImageViewHolder(final View itemView) {
            super(itemView);

            imageMessage = itemView.findViewById(R.id.messageImage);
            messageTextTime = itemView.findViewById(R.id.messageTextTime);
            messageTextContainer = itemView.findViewById(R.id.messageTextContainer);
            messageAvatar = itemView.findViewById(R.id.messageAvatar);

            imageMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Photo photo = items.get(position).getAttachments()[0].getPhoto();
                    String maxPhotoSize = photo.getPhoto_1280();
                    if (maxPhotoSize == null) {
                        maxPhotoSize = photo.getPhoto_807();
                    }
                    if (maxPhotoSize == null) {
                        maxPhotoSize = photo.getPhoto_604();
                    }

                    if (context instanceof ChatActivity){
                        ((ChatActivity)context).createFragmentFullSizeImageMessage(maxPhotoSize);
                    }
                }
            });
        }
    }

}