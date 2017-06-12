/**
 * @(#)ObjectSetStore.java
 *
 * Copyright:	Copyright (c) 2009
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.storage.exceptions.NoSuchIdentifierException;
import com.oathouse.oss.storage.exceptions.PersistenceException;

/**
 * The {@code ObjectSingleStore} Class is a class used to Store a single bean.
 *
 * @param <T> The T specialisation class
 *
 * @author 		Darryl Oatridge
 * @version 	2.01 20-July-2010
 * @see ObjectBean
 */
public class ObjectSingleStore<T extends ObjectBean> extends ObjectDBMS<T> {

    /**
     * The constructor for {@code ObjectSingleStore} Class. If the optional
     * ObjectDataOptionsEnum is not included the default is MEMORY and PERSIST
     *
     * @param managerName the name under which the persistence data will be stored
     * @param storeType optional list of ObjectDataOptionsEnum representing the type of store required
     * @see ObjectDataOptionsEnum
     */
    public ObjectSingleStore(String managerName, ObjectDataOptionsEnum... storeType) {
        super(managerName, storeType);
    }

    /**
     * returns the object T .
     *
     * @return T the object bean
     * @throws NoSuchIdentifierException
     * @throws PersistenceException
     */
    public T getObject() throws PersistenceException, NoSuchIdentifierException {
        T rtnObj = getObjectInKey(ObjectEnum.SINGLE_KEY.value(), ObjectEnum.DEFAULT_ID.value());
        if(rtnObj != null) {
            return (rtnObj);
        }
        throw new NoSuchIdentifierException("Unable to get bean. It may not have been created");
    }

    /**
     * saves an object to the store and persists the object to disk. If the object
     * exists, the object replaces the existing one and modifies the
     * ObjectBean modified parameter. The object identifier is always the DEAFULT_ID
     *
     * @param obj
     * @return The object saved
     * @throws PersistenceException
     */
    public T setObject(T obj) throws PersistenceException {
        obj.setIdentifier(ObjectEnum.DEFAULT_ID.value());
        return (super.setObjectInKey(ObjectEnum.SINGLE_KEY.value(), obj));
    }
}
