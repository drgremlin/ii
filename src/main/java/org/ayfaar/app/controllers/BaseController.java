package org.ayfaar.app.controllers;

import org.ayfaar.app.model.Topic;

/**
 * Created by ddsz on 14.04.2016.
 */
public interface BaseController {
    void importTopics(String topics) throws Exception;
    Topic addFor(String uri,
                        String name,
                        String quote,
                        String comment,
                        Float rate) throws Exception;
    void addFor(String from,
                       String to,
                       String topicName,
                       String rangeName,
                       String quote,
                       String comment,
                       Float rate) throws Exception;
    void updateRate(String forUri, String topicUri, Float rate) throws Exception;

    void updateComment(String forUri, String name, String comment) throws Exception;

    void addChild(String name, String child);
}
