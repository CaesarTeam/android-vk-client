package com.caezar.vklite.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caezar.vklite.R;
import com.caezar.vklite.activities.ChatActivity;
import com.caezar.vklite.libs.Time;
import com.caezar.vklite.Config;
import com.caezar.vklite.models.network.DialogMessage;
import com.caezar.vklite.models.network.Photo;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import static com.caezar.vklite.libs.ImageLoader.asyncImageLoad;
import static com.caezar.vklite.libs.Time.getDateTime;

/**
 * Created by seva on 03.04.18 in 15:40.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static final int MESSAGE_TEXT = R.layout.message_text;
    static final int MESSAGE_IMAGE = R.layout.message_image;

    private static final int LEFT_MESSAGE = 1;
    private static final int LEFT_IMAGE = 2;
    private static final int RIGHT_MESSAGE = 3;
    private static final int RIGHT_IMAGE = 4;

    // todo: to config and new name!
    private final int minDifferenceToRequest = 40;

    private Context context;
    private List<DialogMessage> items;
    private SparseArray<String> photoUsers;
    private int myselfId;
    private boolean isPrivateDialog;
    private int prevUserId;
    private int prevPosition;

    public ChatAdapter(Context context, boolean isPrivateDialog) {
        items = new ArrayList<>();
        photoUsers = new SparseArray<>();
        myselfId = Config.getMyselfId();
        this.isPrivateDialog = isPrivateDialog;
        this.context = context;
    }

    public void setUsersAvatar(SparseArray<String> photoUsers) {
        this.photoUsers = photoUsers;
        notifyDataSetChanged();
    }

    public void addItemsToTop(@NonNull List<DialogMessage> itemList) {
        items.addAll(itemList);
        notifyDataSetChanged();
    }

    public void addItemToEnd(@NonNull DialogMessage dialogMessage) {
        items.add(0, dialogMessage);
        notifyDataSetChanged();
    }

    public List<DialogMessage> getItems() {
        return items;
    }

    public SparseArray<String> getPhotoUsers() {
        return photoUsers;
    }

    public int getPhotoUsersSize() {
        if (photoUsers == null) {
            return 0;
        }

        return photoUsers.size();
    }

    @NonNull
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

        final String time = getDateTime(item.getDate(), Time.Format.HOURS_MINUTES_SECONDS);
        final int userId = item.getFrom_id();
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
                } else if (!isPrivateDialog && photoUsers.get(item.getFrom_id()) != null) {
                    int nextUserId = 0;
                    if (items.size() > position + 1) {
                        nextUserId = items.get(position + 1).getFrom_id();
                    }

                    if (isNonDuplicatesAvatar(items.size(), position, userId, prevUserId, nextUserId, scrollUp)) {
                        asyncImageLoad(photoUsers.get(item.getFrom_id()), messageTextViewHolder.messageAvatar);
                        messageTextViewHolder.messageAvatar.setVisibility(View.VISIBLE);
                    } else {
                        messageTextViewHolder.messageAvatar.setVisibility(View.INVISIBLE);
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
                } else if (!isPrivateDialog && photoUsers.get(item.getFrom_id()) != null) {
                    asyncImageLoad(photoUsers.get(item.getFrom_id()), messageImageViewHolder.messageAvatar);
                    messageImageViewHolder.messageAvatar.setVisibility(View.VISIBLE);
                }

                break;
            default:
                throw new IllegalArgumentException("invalid view type");
        }

        prevUserId = userId;
        prevPosition = position;

        if (isTimeToRequestDialogs(position)) {
            ((ChatActivity)context).getMessageCallback(getItemCount());
        }
    }

    private boolean isNonDuplicatesAvatar(int itemSize, int position, int userId, int prevUserId, int nextUserId, boolean scrollUp) {
        final boolean isLastItem = (itemSize - 1) == position;
        final boolean isMessageSameAuthorBelow = userId != prevUserId && !scrollUp;
        final boolean isMessageSameAuthorUp = nextUserId != userId && scrollUp;
        return isLastItem || isMessageSameAuthorBelow || isMessageSameAuthorUp;
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

    private boolean isTimeToRequestDialogs(int position) {
        return items.size() - position < minDifferenceToRequest;
    }

    class MessageTextViewHolder extends RecyclerView.ViewHolder {

        TextView message;
        TextView messageTextTime;
        RelativeLayout messageTextContainer;
        RoundedImageView messageAvatar;

        MessageTextViewHolder(final View itemView) {
            super(itemView);

            if (context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                message = itemView.findViewById(R.id.messageText);
                messageTextTime = itemView.findViewById(R.id.messageTextTime);
                messageTextContainer = itemView.findViewById(R.id.messageTextContainer);
                messageAvatar = itemView.findViewById(R.id.messageAvatar);
            } else {
                message = itemView.findViewById(R.id.messageTextLand);
                messageTextTime = itemView.findViewById(R.id.messageTextTimeLand);
                messageTextContainer = itemView.findViewById(R.id.messageTextContainerLand);
                messageAvatar = itemView.findViewById(R.id.messageAvatarLand);
            }

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

            if (context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                imageMessage = itemView.findViewById(R.id.messageImage);
                messageTextTime = itemView.findViewById(R.id.messageTextTime);
                messageTextContainer = itemView.findViewById(R.id.messageTextContainer);
                messageAvatar = itemView.findViewById(R.id.messageAvatar);
            } else {
                imageMessage = itemView.findViewById(R.id.messageImageLand);
                messageTextTime = itemView.findViewById(R.id.messageTextTimeLand);
                messageTextContainer = itemView.findViewById(R.id.messageTextContainerLand);
                messageAvatar = itemView.findViewById(R.id.messageAvatarLand);
            }


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