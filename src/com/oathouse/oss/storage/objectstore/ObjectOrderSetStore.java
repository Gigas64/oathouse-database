/**
 * @(#)ObjectSetStore.java
 *
 * Copyright:	Copyright (c) 2009
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.storage.exceptions.NoSuchIdentifierException;
import com.oathouse.oss.storage.exceptions.NullObjectException;
import com.oathouse.oss.storage.exceptions.PersistenceException;

/**
 * The {@code ObjectOrderSetStore} Class is a class used to Store other objects and provide a central
 * management class for storing Beans. Beans stored must extend ObjectBean. In addition
 * {@code ObjectOrderSetStore} also maintains the position of the stored objects and allows
 * the objects to be placed at specific index within the store. the order of the store is then
 * kept even through persistence. {@code ObjectOrderSetStore} is an
 * implementation of ObjectDB exposing only a single Map key and should be used when only
 * a single set of related data is to be stored. This extends ObjectSetStore.
 *
 * @param <T> The T specialisation class
 *
 * @author 		Darryl Oatridge
 * @version 	2.01 20-July-2010
 * @see ObjectBean
 */
public class ObjectOrderSetStore<T extends ObjectBean> extends ObjectSetStore<T> {

    /**
     * The constructor for {@code ObjectSetStore} Class
     *
     * @param managerName the name under which the persistence data will be stored
     * @param dataOptions [optional] object data options
     */
    public ObjectOrderSetStore(String managerName, ObjectDataOptionsEnum... dataOptions) {
        super(managerName, ObjectDataOptionsEnum.addToArray(dataOptions, ObjectDataOptionsEnum.MEMORY, ObjectDataOptionsEnum.PERSIST, ObjectDataOptionsEnum.ORDERED));
    }

    /**
     *  retrieves an object at the specific index in a List referenced by the key
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param index the index of the object
     * @param options optional ARCHIVE or PERSIST
     * @return the object T
     * @throws NoSuchIdentifierException
     * @throws PersistenceException
     */
    public T getObjectAt(int index, ObjectDataOptionsEnum... options) throws NoSuchIdentifierException, PersistenceException {
        T obj = getObjectInKeyAt(ObjectEnum.SINGLE_KEY.value(), index, options);
        if(obj != null) {
            return (obj);
        }
        throw new NoSuchIdentifierException("Unable to get object. The index '" + index + "' does not exist");
    }

    /**
     * gets the first object in the list referenced by the key
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param options optional ARCHIVE or PERSIST
     * @return the object T
     * @throws PersistenceException
     * @throws NoSuchIdentifierException
     */
    public T getFirstObject(ObjectDataOptionsEnum... options) throws PersistenceException, NoSuchIdentifierException {
        T obj = getFirstObjectInKey(ObjectEnum.SINGLE_KEY.value(), options);
        if(obj != null) {
            return (obj);
        }
        throw new NoSuchIdentifierException("Unable to get the first object.");
    }

    /**
     * gets the last object in the list referenced by the key
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param options optional ARCHIVE or PERSIST
     * @return the object T
     * @throws PersistenceException
     * @throws NoSuchIdentifierException
     */
    public T getLastObject(ObjectDataOptionsEnum... options) throws PersistenceException, NoSuchIdentifierException {
        T obj = getLastObjectInKey(ObjectEnum.SINGLE_KEY.value(), options);
        if(obj != null) {
            return (obj);
        }
        throw new NoSuchIdentifierException("Unable to get the last object.");
    }

    /**
     * gets the index of an object referenced by the key where the object has the identifier
     * passed
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param identifier the identifier of the object
     * @param options optional ARCHIVE or PERSIST
     * @return the index of the object
     * @throws PersistenceException
     * @throws NoSuchIdentifierException
     */
    public int getIndexOf(int identifier, ObjectDataOptionsEnum... options) throws PersistenceException, NoSuchIdentifierException {
        int index = getIndexInKeyOf(ObjectEnum.SINGLE_KEY.value(), identifier, options);
        if(index >= 0) {
            return (index);
        }
        throw new NoSuchIdentifierException("Unable to get object. The Identifier '" + identifier + "' does not exist");
    }

    /**
     * Sets an object to the first position in the list held within a key
     *
     * @param ob the object to be stored
     * @return
     * @throws PersistenceException
     * @throws NullObjectException
     */
    public T setFirstObject(T ob) throws PersistenceException, NullObjectException {
        if(ob == null) {
            throw new NullObjectException("Unable to save object. The object passed is null");
        }
        return (setFirstObjectInKey(ObjectEnum.SINGLE_KEY.value(), ob));
    }

    /**
     * Sets an object to the last position in the list held within a key
     *
     * @param ob the object to be stored
     * @return
     * @throws PersistenceException
     * @throws NullObjectException
     */
    public T setLastObject(T ob) throws PersistenceException, NullObjectException {
        if(ob == null) {
            throw new NullObjectException("Unable to save object. The object passed is null");
        }
        return (setLastObjectInKey(ObjectEnum.SINGLE_KEY.value(), ob));
    }

    /**
     * Sets an object to the specified position index in the list held within a key
     *
     * @param ob the object to be stored
     * @param index the index where the object should be placed
     * @return
     * @throws PersistenceException
     * @throws NullObjectException
     */
    public T setObjectAt(T ob, int index) throws PersistenceException, NullObjectException {
        if(ob == null) {
            throw new NullObjectException("Unable to save object. The object passed is null");
        }
        return (setObjectInKeyAt(ObjectEnum.SINGLE_KEY.value(), ob, index));
    }
}
