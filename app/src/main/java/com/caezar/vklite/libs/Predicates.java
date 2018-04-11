package com.caezar.vklite.libs;

import android.text.TextUtils;

import com.caezar.vklite.models.DialogItem;
import com.google.common.base.Predicate;

/**
 * Created by seva on 11.04.18 in 23:37.
 */

public class Predicates {
    public final static Predicate<DialogItem> isEmptyTitle = new Predicate<DialogItem>() {
        @Override
        public boolean apply(DialogItem dialogItem) {
            return TextUtils.isEmpty(dialogItem.getMessage().getTitle());
        }
    };

    public final static Predicate<DialogItem> isPositiveUserId = new Predicate<DialogItem>() {
        @Override
        public boolean apply(DialogItem dialogItem) {
            return dialogItem.getMessage().getUser_id() > 0;
        }
    };
}
