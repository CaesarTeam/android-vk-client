package com.caezar.vklite.network;

/**
 * Created by seva on 25.03.18 in 22:00.
 */

public class Token {
    /**
     * Пользователь разрешил отправлять ему уведомления (для flash/iframe-приложений)
     */
    private final int notify = 1;

    /**
     * Доступ к друзьям
     */
    private final int friends = 2;

    /**
     * Доступ к фотографиям
     */
    private final int photos = 4;

    /**
     * Доступ к аудиозаписям
     */
    private final int audio = 8;

    /**
     * Доступ к видеозаписям
     */
    private final int video = 16;

    /**
     * Доступ к историям
     */
    private final int stories = 64;

    /**
     * Доступ к wiki-страницам
     */
    private final int pages = 128;

    /**
     * Добавление ссылки на приложение в меню слева
     */
    private final int link = 256;

    /**
     * Доступ к статусу пользователя
     */
    private final int status = 1024;

    /**
     * Доступ к заметкам пользователя
     */
    private final int notes = 2048;

    /**
     * Доступ к расширенным методам работы с сообщениями (только для Standalone-приложений)
     */
    private final int messages = 4096;

    /**
     * Доступ к обычным и расширенным методам работы со стеной.
     * Данное право доступа по умолчанию недоступно для сайтов
     * (игнорируется при попытке авторизации для приложений с типом «Веб-сайт» или по схеме Authorization Code Flow).
     */
    private final int wall = 8192;

    /**
     * Доступ к расширенным методам работы с рекламным API.
     * Доступно для авторизации по схеме Implicit Flow или Authorization Code Flow.
     */
    private final int ads = 32768;

    /**
     * Доступ к API в любое время (при использовании этой опции
     * параметр expires_in, возвращаемый вместе с access_token, содержит 0 — токен бессрочный).
     * Не применяется в Open API
     */
    private final int offline = 65536;

    /**
     * Доступ к документам
     */
    private final int docs = 131072;

    /**
     * Доступ к группам пользователя
     */
    private final int groups = 262144;

    /**
     * Доступ к оповещениям об ответах пользователю
     */
    private final int notifications = 524288;

    /**
     * Доступ к статистике групп и приложений пользователя, администратором которых он является
     */
    private final int stats = 1048576;

    /**
     * Доступ к email пользователя
     */
    private final int email = 4194304;

    /**
     * Доступ к товарам
     */
    private final int market = 134217728;

    private int getScope() {
        return messages;
    }

    public Token() {
    }
}