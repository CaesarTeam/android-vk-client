package com.caezar.vklite.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caezar.vklite.R;
import com.caezar.vklite.fragments.ChatFragment;
import com.caezar.vklite.libs.Time;
import com.caezar.vklite.Config;
import com.caezar.vklite.models.network.DialogMessage;
import com.caezar.vklite.models.network.Photo;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import static com.caezar.vklite.Config.minItemsToRequestChat;
import static com.caezar.vklite.helpers.ChatHelper.getDocSize;
import static com.caezar.vklite.helpers.ChatHelper.getMessageImageUrl;
import static com.caezar.vklite.helpers.ChatHelper.getMessageStickerUrl;
import static com.caezar.vklite.helpers.ChatHelper.isNonDuplicatesAvatar;
import static com.caezar.vklite.helpers.ChatHelper.unsetAlignLayoutRight;
import static com.caezar.vklite.helpers.ChatHelper.setAlignLayoutRight;
import static com.caezar.vklite.helpers.ChatHelper.setAvatar;
import static com.caezar.vklite.helpers.ChatHelper.unsetAvatar;
import static com.caezar.vklite.libs.Guava.findIndexMessage;
import static com.caezar.vklite.libs.ImageLoader.asyncImageLoad;
import static com.caezar.vklite.libs.Time.constructDate;
import static com.caezar.vklite.libs.Time.getDateTime;
import static com.caezar.vklite.libs.Time.isDifferentDays;

/**
 * Created by seva on 03.04.18 in 15:40.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TEXT_MESSAGE = 1;
    private static final int IMAGE_MESSAGE = 2;
    private static final int STICKER_MESSAGE = 3;
    private static final int DOC_MESSAGE = 4;
    private static final int SERVICE_MESSAGE = 5;

    private final Context context;
    private final ChatFragment.ChatCallbacks chatCallbacks;
    @NonNull private final List<DialogMessage> items = new ArrayList<>();
    @NonNull private final SparseArray<String> photoUsers = new SparseArray<>();
    final private int myselfId;
    final private boolean isPrivateDialog;
    private int prevUserIdFrom;
    private int prevPosition;

    public ChatAdapter(ChatFragment.ChatCallbacks chatCallbacks, Context context, boolean isPrivateDialog) {
        myselfId = Config.getMyselfId();
        this.chatCallbacks = chatCallbacks;
        this.isPrivateDialog = isPrivateDialog;
        this.context = context;
    }

    public void setUsersAvatar(@NonNull SparseArray<String> photoUsers) {
        for (int i = 0; i < photoUsers.size(); i++) {
            final int key = photoUsers.keyAt(i);
            this.photoUsers.put(key, photoUsers.get(key));
        }
        notifyDataSetChanged();
    }

    public void addItemsToTop(@NonNull List<DialogMessage> itemList) {
        items.addAll(itemList);
        checkTime();
        notifyDataSetChanged();
    }

    public void changeItem(@NonNull DialogMessage dialogMessage) {
        int index = findIndexMessage(new ArrayList<>(items), dialogMessage.getId());
        items.get(index).setBody(dialogMessage.getBody());
        notifyDataSetChanged();
    }

    public void deleteItem(int messageId) {
        int index = findIndexMessage(new ArrayList<>(items), messageId);
        items.remove(index);
        checkTime();
        notifyDataSetChanged();
    }

    public void addItemToEnd(@NonNull DialogMessage dialogMessage) {
        items.add(0, dialogMessage);
        checkTime();
        notifyDataSetChanged();
    }

    private void checkTime() {
        for (int i = 0; i < items.size() - 1; i++) {
            DialogMessage item1 = items.get(i);
            DialogMessage item2 = items.get(i + 1);

            if (isDifferentDays(item1.getDate(), item2.getDate())) {
                DialogMessage dialogMessage = new DialogMessage();
                dialogMessage.setBody(constructDate(item1.getDate(), context));
                dialogMessage.setServiceMessage(true);
                items.add(i + 1, dialogMessage);
            }
        }
    }

    @NonNull
    public List<DialogMessage> getItems() {
        return items;
    }

    @NonNull
    public SparseArray<String> getPhotoUsers() {
        return photoUsers;
    }

    public int getPhotoUsersSize() {
        return photoUsers.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TEXT_MESSAGE:
                View messageTextView = LayoutInflater.from(context).inflate(R.layout.message_text, parent, false);
                return new MessageTextViewHolder(messageTextView);
            case IMAGE_MESSAGE:
                View messageImageView = LayoutInflater.from(context).inflate(R.layout.message_image, parent, false);
                return new MessageImageViewHolder(messageImageView);
            case STICKER_MESSAGE:
                View messageStickerView = LayoutInflater.from(context).inflate(R.layout.message_sticker, parent, false);
                return new MessageStickerViewHolder(messageStickerView);
            case DOC_MESSAGE:
                View messageDocView = LayoutInflater.from(context).inflate(R.layout.message_doc, parent, false);
                return new MessageDocViewHolder(messageDocView);
            case SERVICE_MESSAGE:
                View messageServiceView = LayoutInflater.from(context).inflate(R.layout.message_service, parent, false);
                return new MessageServiceViewHolder(messageServiceView);
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
                MessageTextViewHolder messageTextViewHolder = ((MessageTextViewHolder) holder);
                messageTextViewHolder.position = position;
                messageTextViewHolder.messageText.setText(item.getBody());
                messageTextViewHolder.messageTextTime.setText(time);

                break;
            case IMAGE_MESSAGE:
                MessageImageViewHolder messageImageViewHolder = ((MessageImageViewHolder) holder);
                messageImageViewHolder.position = position;
                asyncImageLoad(getMessageImageUrl(item), messageImageViewHolder.messageImage);
                messageImageViewHolder.messageImageTime.setText(time);
                messageImageViewHolder.messageImageTime.bringToFront();

                break;
            case STICKER_MESSAGE:
                MessageStickerViewHolder messageStickerViewHolder = ((MessageStickerViewHolder) holder);
                asyncImageLoad(getMessageStickerUrl(item), messageStickerViewHolder.messageSticker);
                messageStickerViewHolder.messageStickerTime.setText(time);
                messageStickerViewHolder.messageStickerTime.bringToFront();

                break;
            case DOC_MESSAGE:
                MessageDocViewHolder messageDocViewHolder = ((MessageDocViewHolder) holder);
                messageDocViewHolder.messageDocName.setText(item.getAttachments()[0].getDoc().getTitle());
                messageDocViewHolder.messageDocSize.setText(getDocSize(item.getAttachments()[0].getDoc().getSize(), context));
                messageDocViewHolder.messageDocTime.setText(time);
                messageDocViewHolder.messageDocTime.bringToFront();

                break;
            case SERVICE_MESSAGE:
                MessageServiceViewHolder messageServiceViewHolder = ((MessageServiceViewHolder) holder);
                messageServiceViewHolder.messageService.setText(item.getBody());

                return;
            default:
                throw new IllegalArgumentException("invalid view type");
        }

        ChatViewHolder chatViewHolder = ((ChatViewHolder) holder);
        if (side) {
            chatViewHolder.container.setBackgroundResource(R.drawable.message_text_from_me_container);
            setAlignLayoutRight(chatViewHolder.container);
            unsetAvatar(chatViewHolder.avatar);

        } else {
            chatViewHolder.container.setBackgroundResource(R.drawable.message_text_to_me_container);
            unsetAlignLayoutRight(chatViewHolder.container);
            if (!isPrivateDialog && avatarUrl != null) {
                setAvatar(isNonDuplicatesAvatar, chatViewHolder.avatar, avatarUrl);
            }
        }

        prevUserIdFrom = userIdFrom;
        prevPosition = position;

        if (isTimeToRequestDialogs(position)) {
            chatCallbacks.getMoreMessages(getItemCount());
        }
    }

    @Override
    public int getItemViewType(int position) {
        DialogMessage item = items.get(position);

        if (item.isServiceMessage()) {
            return SERVICE_MESSAGE;
        }

        if (item.getAttachments() != null && item.getAttachments()[0].getType() != null) {
            switch (item.getAttachments()[0].getType()) {
                case PHOTO:
                    return IMAGE_MESSAGE;
                case STICKER:
                    return STICKER_MESSAGE;
                case DOC:
                    return DOC_MESSAGE;
                default:
                    break;
            }
        }

        return TEXT_MESSAGE;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private boolean isTimeToRequestDialogs(int position) {
        return items.size() - position < minItemsToRequestChat;
    }

    class MessageTextViewHolder extends ChatViewHolder {

        final TextView messageText;
        final TextView messageTextTime;
        int position;

        MessageTextViewHolder(final View itemView) {
            super(itemView);

            final boolean isPort = context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
            avatar = (RoundedImageView) (isPort ? itemView.findViewById(R.id.messageTextAvatar) : itemView.findViewById(R.id.messageTextAvatarLand));
            container = (RelativeLayout) (isPort ? itemView.findViewById(R.id.messageTextContainer) : itemView.findViewById(R.id.messageTextContainerLand));
            messageText = (TextView) (isPort ? itemView.findViewById(R.id.messageText) : itemView.findViewById(R.id.messageTextLand));
            messageTextTime = (TextView) (isPort ? itemView.findViewById(R.id.messageTextTime) : itemView.findViewById(R.id.messageTextTimeLand));

            container.setOnClickListener(v -> {
                chatCallbacks.createFragmentDialogMessageType(items.get(position), items.get(position).getFrom_id() == myselfId);
            });
        }
    }

    class MessageImageViewHolder extends ChatViewHolder {

        final ImageView messageImage;
        final TextView messageImageTime;
        int position;

        MessageImageViewHolder(final View itemView) {
            super(itemView);

            final boolean isPort = context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
            avatar = (RoundedImageView) (isPort ? itemView.findViewById(R.id.messageImageAvatar) : itemView.findViewById(R.id.messageImageAvatarLand));
            container = (RelativeLayout) (isPort ? itemView.findViewById(R.id.messageImageContainer) : itemView.findViewById(R.id.messageImageContainerLand));
            messageImage = (ImageView) (isPort ? itemView.findViewById(R.id.messageImage) : itemView.findViewById(R.id.messageImageLand));
            messageImageTime = (TextView) (isPort ? itemView.findViewById(R.id.messageImageTime) : itemView.findViewById(R.id.messageImageTimeLand));

            messageImage.setOnClickListener(v -> {
                Photo photo = items.get(position).getAttachments()[0].getPhoto();
                String maxPhotoSize = photo.getPhoto_1280();
                if (maxPhotoSize == null) {
                    maxPhotoSize = photo.getPhoto_807();
                }
                if (maxPhotoSize == null) {
                    maxPhotoSize = photo.getPhoto_604();
                }


                chatCallbacks.createFragmentFullSizeImageMessage(maxPhotoSize);
            });
        }
    }

    class MessageStickerViewHolder extends ChatViewHolder {

        final ImageView messageSticker;
        final TextView messageStickerTime;

        MessageStickerViewHolder(final View itemView) {
            super(itemView);

            final boolean isPort = context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
            avatar = (RoundedImageView) (isPort ? itemView.findViewById(R.id.messageStickerAvatar) : itemView.findViewById(R.id.messageStickerAvatarLand));
            container = (RelativeLayout) (isPort ? itemView.findViewById(R.id.messageStickerContainer) : itemView.findViewById(R.id.messageStickerContainerLand));
            messageSticker = (ImageView) (isPort ? itemView.findViewById(R.id.messageSticker) : itemView.findViewById(R.id.messageStickerLand));
            messageStickerTime = (TextView) (isPort ? itemView.findViewById(R.id.messageStickerTime) : itemView.findViewById(R.id.messageStickerTimeLand));
        }
    }

    class MessageDocViewHolder extends ChatViewHolder {

        final RoundedImageView messageDocDownload;
        final TextView messageDocName;
        final TextView messageDocSize;
        final TextView messageDocTime;

        MessageDocViewHolder(final View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.messageDocAvatar);
            container = itemView.findViewById(R.id.messageDocContainer);
            messageDocDownload = itemView.findViewById(R.id.messageDocDownload);
            messageDocName = itemView.findViewById(R.id.messageDocName);
            messageDocSize = itemView.findViewById(R.id.messageDocSize);
            messageDocTime = itemView.findViewById(R.id.messageDocTime);
        }
    }

    class MessageServiceViewHolder extends RecyclerView.ViewHolder {

        final TextView messageService;

        MessageServiceViewHolder(final View itemView) {
            super(itemView);
            messageService = itemView.findViewById(R.id.messageService);
        }
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView avatar;
        RelativeLayout container;

        public ChatViewHolder(View itemView) {
            super(itemView);
        }
    }
}