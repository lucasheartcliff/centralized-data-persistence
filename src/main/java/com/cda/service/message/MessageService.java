package com.cda.service.message;

import com.cda.model.Message;
import com.cda.viewmodel.MessageViewModel;

import java.util.List;

public interface MessageService {
    List<Message> getMessages();

    Message createMessage(MessageViewModel model) throws Exception;

    void deleteMessage(Long messageId) throws Exception;
}
