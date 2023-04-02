package com.cda.repository.message;

import com.cda.model.Message;
import com.cda.repository.BaseRepository;

import javax.persistence.EntityManager;

public class MessageRepositoryImpl extends BaseRepository<Message, Long> implements MessageRepository {

    public MessageRepositoryImpl(EntityManager entityManager) {
        super(entityManager, Message.class);
    }
}
