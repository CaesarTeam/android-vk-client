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

import static com.caezar.vklite.libs.ChatHelper.getMessageImageUrl;
import static com.caezar.vklite.libs.ChatHelper.getMessageStickerUrl;
import static com.caezar.vklite.libs.ChatHelper.isNonDuplicatesAvatar;
import static com.caezar.vklite.libs.ChatHelper.setAlignLayoutRight;
import static com.caezar.vklite.libs.ChatHelper.setAvatar;
import static com.caezar.vklite.libs.ImageLoader.asyncImageLoad;
import static com.caezar.vklite.libs.Time.getDateTime;

/**
 * Created by seva on 03.04.18 in 15:40.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TEXT_MESSAGE = 1;
    private static final int TEXT_MESSAGE_FAKE = 2;
    private static final int IMAGE_MESSAGE = 3;
    private static final int IMAGE_MESSAGE_FAKE = 4;
    private static final int STICKER_MESSAGE = 5;
    private static final int STICKER_MESSAGE_FAKE = 6;

    // todo: to config and new name!
    private final int minDifferenceToRequest = 40;

    private Context context;
    private List<DialogMessage> items;
    private SparseArray<String> photoUsers;
    private int myselfId;
    private boolean isPrivateDialog;
    private int prevUserIdFrom;
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
        switch (viewType) {
            case TEXT_MESSAGE:
            case TEXT_MESSAGE_FAKE:
                View messageTextView = LayoutInflater.from(context).inflate(R.layout.message_text, parent, false);
                return new MessageTextViewHolder(messageTextView);
            case IMAGE_MESSAGE:
            case IMAGE_MESSAGE_FAKE:
                View messageImageView = LayoutInflater.from(context).inflate(R.layout.message_image, parent, false);
                return new MessageImageViewHolder(messageImageView);
            case STICKER_MESSAGE:
            case STICKER_MESSAGE_FAKE:
                View messageStickerView = LayoutInflater.from(context).inflate(R.layout.message_sticker, parent, false);
                return new MessageStickerViewHolder(messageStickerView);
            default:
                throw new IllegalArgumentException("invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        DialogMessage item = items.get(position);
        final int userIdFrom = item.getFrom_id();
        boolean side = userIdFrom == myselfId;
        final String time = getDateTime(item.getDate(), Time.Format.HOURS_MINUTES_SECONDS);
        final boolean scrollUp = position > prevPosition;
        int nextUserIdFrom = 0;
        if (items.size() > position + 1) {
            nextUserIdFrom = items.get(position + 1).getFrom_id();
        }
        final boolean isNonDuplicatesAvatar = isNonDuplicatesAvatar(items.size(), position, userIdFrom, prevUserIdFrom, nextUserIdFrom, scrollUp);
        String avatarUrl = photoUsers.get(userIdFrom);

        switch (getItemViewType(position)) {
            case TEXT_MESSAGE:
            case TEXT_MESSAGE_FAKE:
                MessageTextViewHolder messageTextViewHolder = ((MessageTextViewHolder) holder);
                messageTextViewHolder.messageText.setText(item.getBody());
                messageTextViewHolder.messageTextTime.setText(time);
                if (side) {
                    setAlignLayoutRight(messageTextViewHolder.messageTextContainer);
                } else if (!isPrivateDialog && avatarUrl != null) {
                    setAvatar(isNonDuplicatesAvatar, messageTextViewHolder.messageTextAvatar, avatarUrl);
                }

                break;
            case IMAGE_MESSAGE:
            case IMAGE_MESSAGE_FAKE:
                MessageImageViewHolder messageImageViewHolder = ((MessageImageViewHolder) holder);
                messageImageViewHolder.position = position;
                asyncImageLoad(getMessageImageUrl(item), messageImageViewHolder.messageImage);
                messageImageViewHolder.messageImageTime.setText(time);
                messageImageViewHolder.messageImageTime.bringToFront();
                if (side) {
                    setAlignLayoutRight(messageImageViewHolder.messageImageContainer);
                } else if (!isPrivateDialog && avatarUrl != null) {
                    setAvatar(isNonDuplicatesAvatar, messageImageViewHolder.messageImageAvatar, avatarUrl);
                }

                break;
            case STICKER_MESSAGE:
            case STICKER_MESSAGE_FAKE:
                MessageStickerViewHolder messageStickerViewHolder = ((MessageStickerViewHolder) holder);
                asyncImageLoad(getMessageStickerUrl(item), messageStickerViewHolder.messageSticker);
                messageStickerViewHolder.messageStickerTime.setText(time);
                messageStickerViewHolder.messageStickerTime.bringToFront();
                if (side) {
                    setAlignLayoutRight(messageStickerViewHolder.messageStickerContainer);
                } else if (!isPrivateDialog && avatarUrl != null) {
                    setAvatar(isNonDuplicatesAvatar, messageStickerViewHolder.messageStickerAvatar, avatarUrl);
                }

                break;
            default:
                throw new IllegalArgumentException("invalid view type");
        }

        prevUserIdFrom = userIdFrom;
        prevPosition = position;

        if (isTimeToRequestDialogs(position)) {
            ((ChatActivity)context).getMessageCallback(getItemCount());
        }
    }

    @Override
    public int getItemViewType(int position) {
        DialogMessage item = items.get(position);
        boolean side = item.getFrom_id() == myselfId;

        if (item.getAttachments() != null) {
            // todo: switch
            if (item.getAttachments()[0].getType().equals("photo")) {
                return side ? IMAGE_MESSAGE : IMAGE_MESSAGE_FAKE;
            }

            if (item.getAttachments()[0].getType().equals("sticker")) {
                return side ? STICKER_MESSAGE : STICKER_MESSAGE_FAKE;
            }
        }

        return side ? TEXT_MESSAGE : TEXT_MESSAGE_FAKE;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private boolean isTimeToRequestDialogs(int position) {
        return items.size() - position < minDifferenceToRequest;
    }

    class MessageTextViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView messageTextAvatar;
        RelativeLayout messageTextContainer;
        TextView messageText;
        TextView messageTextTime;

        MessageTextViewHolder(final View itemView) {
            super(itemView);

            final boolean isPort = context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
            messageTextAvatar = (RoundedImageView) (isPort ? itemView.findViewById(R.id.messageTextAvatar) : itemView.findViewById(R.id.messageTextAvatarLand));
            messageTextContainer = (RelativeLayout) (isPort ? itemView.findViewById(R.id.messageTextContainer) : itemView.findViewById(R.id.messageTextContainerLand));
            messageText = (TextView) (isPort ? itemView.findViewById(R.id.messageText) : itemView.findViewById(R.id.messageTextLand));
            messageTextTime = (TextView) (isPort ? itemView.findViewById(R.id.messageTextTime) : itemView.findViewById(R.id.messageTextTimeLand));
        }
    }

    class MessageImageViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView messageImageAvatar;
        RelativeLayout messageImageContainer;
        ImageView messageImage;
        TextView messageImageTime;
        int position;

        MessageImageViewHolder(final View itemView) {
            super(itemView);

            final boolean isPort = context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
            messageImageAvatar = (RoundedImageView) (isPort ? itemView.findViewById(R.id.messageImageAvatar) : itemView.findViewById(R.id.messageImageAvatarLand));
            messageImageContainer = (RelativeLayout) (isPort ? itemView.findViewById(R.id.messageImageContainer) : itemView.findViewById(R.id.messageImageContainerLand));
            messageImage = (ImageView) (isPort ? itemView.findViewById(R.id.messageImage) : itemView.findViewById(R.id.messageImageLand));
            messageImageTime = (TextView) (isPort ? itemView.findViewById(R.id.messageImageTime) : itemView.findViewById(R.id.messageImageTimeLand));

            messageImage.setOnClickListener(new View.OnClickListener() {
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

    class MessageStickerViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView messageStickerAvatar;
        RelativeLayout messageStickerContainer;
        ImageView messageSticker;
        TextView messageStickerTime;

        MessageStickerViewHolder(final View itemView) {
            super(itemView);

            final boolean isPort = context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
            messageStickerAvatar = (RoundedImageView) (isPort ? itemView.findViewById(R.id.messageStickerAvatar) : itemView.findViewById(R.id.messageStickerAvatarLand));
            messageStickerContainer = (RelativeLayout) (isPort ? itemView.findViewById(R.id.messageStickerContainer) : itemView.findViewById(R.id.messageStickerContainerLand));
            messageSticker = (ImageView) (isPort ? itemView.findViewById(R.id.messageSticker) : itemView.findViewById(R.id.messageStickerLand));
            messageStickerTime = (TextView) (isPort ? itemView.findViewById(R.id.messageStickerTime) : itemView.findViewById(R.id.messageStickerTimeLand));
        }
    }

}