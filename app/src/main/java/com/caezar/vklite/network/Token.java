package com.caezar.vklite.network;

/**
 * Created by seva on 25.03.18 in 22:00.
 */

public class Token {
    /**
     * Пользователь разрешил отправлять ему уведомления (для flash/iframe-приложений)
     */
    private final String notify = "notify";

    /**
     * Доступ к друзьям
     */
    private final String friends = "friends";

    /**
     * Доступ к фотографиям
     */
    private final String photos = "photos";

    /**
     * Доступ к аудиозаписям
     */
    private final String audio = "audio";

    /**
     * Доступ к видеозаписям
     */
    private final String video = "video";

    /**
     * Доступ к историям
     */
    private final String stories = "stories";

    /**
     * Доступ к wiki-страницам
     */
    private final String pages = "pages";

    /**
     * Добавление ссылки на приложение в меню слева
     */
    private final String link = "link";

    /**
     * Доступ к статусу пользователя
     */
    private final String status = "status";

    /**
     * Доступ к заметкам пользователя
     */
    private final String notes = "notes";

    /**
     * Доступ к расширенным методам работы с сообщениями (только для Standalone-приложений)
     */
    private final String messages = "messages";

    /**
     * Доступ к обычным и расширенным методам работы со стеной.
     * Данное право доступа по умолчанию недоступно для сайтов
     * (игнорируется при попытке авторизации для приложений с типом «Веб-сайт» или по схеме Authorization Code Flow).
     */
    private final String wall = "wall";

    /**
     * Доступ к расширенным методам работы с рекламным API.
     * Доступно для авторизации по схеме Implicit Flow или Authorization Code Flow.
     */
    private final String ads = "ads";

    /**
     * Доступ к API в любое время (при использовании этой опции
     * параметр expires_in, возвращаемый вместе с access_token, содержит 0 — токен бессрочный).
     * Не применяется в Open API
     */
    private final String offline = "offline";

    /**
     * Доступ к документам
     */
    private final String docs = "docs";

    /**
     * Доступ к группам пользователя
     */
    private final String groups = "groups";

    /**
     * Доступ к оповещениям об ответах пользователю
     */
    private final String notifications = "notifications";

    /**
     * Доступ к статистике групп и приложений пользователя, администратором которых он является
     */
    private final String stats = "stats";

    /**
     * Доступ к email пользователя
     */
    private final String email = "email";

    /**
     * Доступ к товарам
     */
    private final String market = "market";

    private String getScope() {
        return messages;
    }

    public Token() {
    }
}