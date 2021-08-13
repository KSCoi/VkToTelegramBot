package ru.tsoyk.vk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tsoyk.tg.models.EventTypes;
import ru.tsoyk.vk.parcers.*;

@Component
public class VkEventsParserFactory {
    @Autowired
    private WallPostNewParcer wallPostNewParcer;
    @Autowired
    private MessageReplyParser messageReplyParser;
    @Autowired
    private PhotoCommentParser photoCommentParser;
    @Autowired
    private WallReplyParser wallReplyParser;
    @Autowired
    private VideoCommentParser videoCommentParser;
    @Autowired
    private NewMessageParser newMessageParser;
    @Autowired
    private TopicCommentParser topicCommentParser;
    @Autowired
    DeletedEventParser deletedEventParser;

    public VkParserInterface getVkParser(EventTypes eventType) {
        VkParserInterface vkParserInterface = null;
        switch (eventType) {
            case MESSAGE_NEW:
                vkParserInterface = newMessageParser;
                break;
            case MESSAGE_REPLY:
                vkParserInterface = messageReplyParser;
                break;
            case PHOTO_COMMENT_NEW:
                vkParserInterface = photoCommentParser;
                break;
            case WALL_POST_NEW:
                vkParserInterface = wallPostNewParcer;
                break;
            case BOARD_POST_NEW:
                vkParserInterface = topicCommentParser;
                break;
            case WALL_REPLY:
                vkParserInterface = wallReplyParser;
                break;
            case VIDEO_COMMENT_NEW:
                vkParserInterface = videoCommentParser;
                break;
            case DELETED_EVENT:
                vkParserInterface = deletedEventParser;
        }
        return vkParserInterface;
    }
}
