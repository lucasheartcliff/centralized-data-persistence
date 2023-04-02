package com.cda.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Message.class)
public abstract class Message_ {

	public static volatile SingularAttribute<Message, Date> createdAt;
	public static volatile SingularAttribute<Message, Long> id;
	public static volatile SingularAttribute<Message, String> message;
	public static volatile SingularAttribute<Message, User> user;
	public static volatile SingularAttribute<Message, Date> updatedAt;

	public static final String CREATED_AT = "createdAt";
	public static final String ID = "id";
	public static final String MESSAGE = "message";
	public static final String USER = "user";
	public static final String UPDATED_AT = "updatedAt";

}

