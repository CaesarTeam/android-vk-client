package com.caezar.vklite.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.caezar.vklite.models.network.Attachments;
import com.caezar.vklite.models.network.DialogMessage;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import static com.caezar.vklite.Config.minItemsToRequestChat;
import static com.caezar.vklite.helpers.ChatHelper.cleanItemsFromMessagesService;
import static com.caezar.vklite.helpers.ChatHelper.constructMessageService;
import static com.caezar.vklite.helpers.ChatHelper.getDocSize;
import static com.caezar.vklite.helpers.ChatHelper.getMessageImageMax;
import static com.caezar.vklite.helpers.ChatHelper.getMessageImageUrl;
import static com.caezar.vklite.helpers.ChatHelper.getMessageStickerUrl;
import static com.caezar.vklite.helpers.ChatHelper.getPositionToScrollChat;
import static com.caezar.vklite.helpers.ChatHelper.isNonDuplicatesAvatar;
import static com.caezar.vklite.helpers.ChatHelper.setAlignTextViewRight;
import static com.caezar.vklite.helpers.ChatHelper.unsetAlignLayoutRight;
import static com.caezar.vklite.helpers.ChatHelper.setAlignLayoutRight;
import static com.caezar.vklite.helpers.ChatHelper.setAvatar;
import static com.caezar.vklite.helpers.ChatHelper.unsetAlignTextViewRight;
import static com.caezar.vklite.helpers.ChatHelper.unsetAvatar;
import static com.caezar.vklite.libs.Guava.findIndexMessage;
import static com.caezar.vklite.libs.ImageLoader.asyncImageLoad;
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
    private int sizeWithoutServiceMessage;
    private int countMessagesChat;

    public void setCountMessagesChat(int countMessagesChat) {
        this.countMessagesChat = countMessagesChat;
    }

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
        boolean needScrolling = false;
        if (items.size() == 0) {
            needScrolling = true;
        }

        items.addAll(itemList);

        cleanItemsFromMessagesService(items);
        sizeWithoutServiceMessage = items.size();
        addMessagesServiceTime();

        notifyDataSetChanged();

        if (needScrolling) {
            int position = getPositionToScrollChat(items);
            if (position != -1) {
                chatCallbacks.scrollToPosition(position);
            }
        }
    }

    public void changeItems(@NonNull List<DialogMessage> dialogMessages) {
        for (DialogMessage dialogMessage: dialogMessages) {
            int index = findIndexMessage(new ArrayList<>(items), dialogMessage.getId());
            if (index != -1) {
                items.get(index).setBody(dialogMessage.getBody());
            }
        }
        notifyDataSetChanged();
    }

    public void deleteItem(@NonNull List<Integer> messageIds) {
        for (Integer messageId : messageIds) {
            int index = findIndexMessage(new ArrayList<>(items), messageId);
            if (index != -1) {
                items.remove(index);

            }
        }

        cleanItemsFromMessagesService(items);
        sizeWithoutServiceMessage = items.size();
        addMessagesServiceTime();

        notifyDataSetChanged();
    }

    public void addItemsToEnd(@NonNull List<DialogMessage> itemList) {
        items.addAll(0, itemList);

        cleanItemsFromMessagesService(items);
        sizeWithoutServiceMessage = items.size();
        addMessagesServiceTime();

        notifyDataSetChanged();
    }

    private void addMessagesServiceTime() {
        items.add(constructMessageService(items.get(items.size() - 1).getDate(), context));

        for (int i = 0; i < items.size() - 1; i++) {
            DialogMessage item1 = items.get(i);
            DialogMessage item2 = items.get(i + 1);

            if (isDifferentDays(item1.getDate(), item2.getDate())) {
                items.add(i + 1, constructMessageService(item1.getDate(), context));
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        position = holder.getAdapterPosition();
        DialogMessage item = items.get(position);
        final int userIdFrom = item.getFrom_id();
        boolean isReadState = item.isRead_state();
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

                if (side) {
                    messageTextViewHolder.container.setBackgroundResource(R.drawable.message_text_from_me_container);
                } else {
                    messageTextViewHolder.container.setBackgroundResource(R.drawable.message_text_to_me_container);
                }

                break;
            case IMAGE_MESSAGE:
                MessageImageViewHolder messageImageViewHolder = ((MessageImageViewHolder) holder);
                messageImageViewHolder.messageImageText.setText(item.getBody());

                messageImageViewHolder.messageImage.removeAllViews();
                for (Attachments attachment : item.getAttachments()) {
                    View messageImageView = LayoutInflater.from(context).inflate(R.layout.chat_image_item, messageImageViewHolder.messageImage, false);
                    RoundedImageView messageImage = messageImageView.findViewById(R.id.messageImage);
                    TextView messageImageTime = messageImageView.findViewById(R.id.messageImageTime);

                    asyncImageLoad(getMessageImageUrl(attachment.getPhoto()), messageImage);
                    messageImage.setOnClickListener((View v) -> chatCallbacks.createFragmentFullSizeImageMessage(getMessageImageMax(attachment.getPhoto())));
                    messageImageTime.setText(time);
                    messageImageTime.bringToFront();

                    messageImageViewHolder.messageImage.addView(messageImageView);
                }

                if (side) {
                    messageImageViewHolder.container.setBackgroundResource(R.drawable.message_text_from_me_container);
                } else {
                    messageImageViewHolder.container.setBackgroundResource(R.drawable.message_text_to_me_container);
                }

                if (TextUtils.isEmpty(item.getBody())) {
                    messageImageViewHolder.messageImageText.setVisibility(View.GONE);
                    if (item.getAttachments().length == 1) {
                        messageImageViewHolder.container.setBackground(null);
                    }
                } else {
                    messageImageViewHolder.messageImageText.setVisibility(View.VISIBLE);
                }

                break;
            case STICKER_MESSAGE:
                MessageStickerViewHolder messageStickerViewHolder = ((MessageStickerViewHolder) holder);
                asyncImageLoad(getMessageStickerUrl(item), messageStickerViewHolder.messageSticker);
                messageStickerViewHolder.messageStickerTime.setText(time);
                messageStickerViewHolder.messageStickerTime.bringToFront();

                break;
            case DOC_MESSAGE:
                MessageDocViewHolder messageDocViewHolder = ((MessageDocViewHolder) holder);
                messageDocViewHolder.messageDocText.setText(item.getBody());

                messageDocViewHolder.messageDoc.removeAllViews();
                for (Attachments attachment : item.getAttachments()) {
                    View messageDocView = LayoutInflater.from(context).inflate(R.layout.chat_document_item, messageDocViewHolder.messageDoc, false);
                    RoundedImageView messageDocDownload = messageDocView.findViewById(R.id.messageDocDownload);
                    TextView messageDocName = messageDocView.findViewById(R.id.messageDocName);
                    TextView messageDocSize = messageDocView.findViewById(R.id.messageDocSize);
                    TextView messageDocTime = messageDocView.findViewById(R.id.messageDocTime);

                    messageDocDownload.setOnClickListener((View v) -> chatCallbacks.downloadDocument(attachment.getDoc().getUrl(), attachment.getDoc().getTitle()));
                    messageDocName.setText(attachment.getDoc().getTitle());
                    messageDocSize.setText(getDocSize(attachment.getDoc().getSize(), context));
                    messageDocTime.setText(time);
                    messageDocTime.bringToFront();

                    messageDocViewHolder.messageDoc.addView(messageDocView);
                }

                if (side) {
                    messageDocViewHolder.container.setBackgroundResource(R.drawable.message_text_from_me_container);
                } else {
                    messageDocViewHolder.container.setBackgroundResource(R.drawable.message_text_to_me_container);
                }

                if (TextUtils.isEmpty(item.getBody())) {
                    messageDocViewHolder.messageDocText.setVisibility(View.GONE);
                } else {
                    messageDocViewHolder.messageDocText.setVisibility(View.VISIBLE);
                }

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
            setAlignLayoutRight(chatViewHolder.container);
            unsetAvatar(chatViewHolder.avatar);

        } else {
            unsetAlignLayoutRight(chatViewHolder.container);
            if (!isPrivateDialog && avatarUrl != null) {
                setAvatar(isNonDuplicatesAvatar, chatViewHolder.avatar, avatarUrl);
            }
        }

        if (isReadState) {
            chatViewHolder.messageReadState.setVisibility(View.GONE);
        } else {
            chatViewHolder.messageReadState.setVisibility(View.VISIBLE);
            if (side) {
                unsetAlignTextViewRight(chatViewHolder.messageReadState);
            } else {
                setAlignTextViewRight(chatViewHolder.messageReadState);
            }
        }

        prevUserIdFrom = userIdFrom;
        prevPosition = position;

        if (isTimeToRequestDialogs(position)) {
            chatCallbacks.getMoreMessages(sizeWithoutServiceMessage);
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
        return countMessagesChat > sizeWithoutServiceMessage && sizeWithoutServiceMessage - position < minItemsToRequestChat;
    }

    class MessageTextViewHolder extends ChatViewHolder {

        final TextView messageText;
        final TextView messageTextTime;
        int position;

        MessageTextViewHolder(final View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.messageTextAvatar);
            container = (RelativeLayout) itemView.findViewById(R.id.messageTextContainer);
            messageText = itemView.findViewById(R.id.messageText);
            messageTextTime = itemView.findViewById(R.id.messageTextTime);
            messageReadState = itemView.findViewById(R.id.messageTextReadState);

            container.setOnClickListener(v -> chatCallbacks.createFragmentDialogMessageType(items.get(position), items.get(position).getFrom_id() == myselfId));
        }
    }

    class MessageImageViewHolder extends ChatViewHolder {

        final TextView messageImageText;
        final GridLayout messageImage;

        MessageImageViewHolder(final View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.messageImageAvatar);
            container = (RelativeLayout) itemView.findViewById(R.id.messageImageContainer);
            messageReadState = itemView.findViewById(R.id.messageImageReadState);
            messageImageText = itemView.findViewById(R.id.messageImageText);
            messageImage = itemView.findViewById(R.id.messageImage);
        }
    }

    class MessageStickerViewHolder extends ChatViewHolder {

        final ImageView messageSticker;
        final TextView messageStickerTime;

        MessageStickerViewHolder(final View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.messageStickerAvatar);
            container = (RelativeLayout) itemView.findViewById(R.id.messageStickerContainer);
            messageSticker = itemView.findViewById(R.id.messageSticker);
            messageStickerTime = itemView.findViewById(R.id.messageStickerTime);
            messageReadState = itemView.findViewById(R.id.messageStickerReadState);
        }
    }

    class MessageDocViewHolder extends ChatViewHolder {

        final TextView messageDocText;
        final GridLayout messageDoc;

        MessageDocViewHolder(final View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.messageDocAvatar);
            container = (RelativeLayout) itemView.findViewById(R.id.messageDocContainer);
            messageReadState = itemView.findViewById(R.id.messageDocReadState);
            messageDocText = itemView.findViewById(R.id.messageDocText);
            messageDoc = itemView.findViewById(R.id.messageDoc);
        }
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView avatar;
        ViewGroup container;
        TextView messageReadState;

        ChatViewHolder(View itemView) {
            super(itemView);
        }
    }

    class MessageServiceViewHolder extends RecyclerView.ViewHolder {

        final TextView messageService;

        MessageServiceViewHolder(final View itemView) {
            super(itemView);
            messageService = itemView.findViewById(R.id.messageService);
        }
    }
}