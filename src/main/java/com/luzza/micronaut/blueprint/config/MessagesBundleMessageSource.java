package com.luzza.micronaut.blueprint.config;

import io.micronaut.context.i18n.ResourceBundleMessageSource;

import javax.inject.Singleton;

@Singleton
public class MessagesBundleMessageSource extends ResourceBundleMessageSource {

    public MessagesBundleMessageSource() {
        super("i18n.messages");
    }
}
