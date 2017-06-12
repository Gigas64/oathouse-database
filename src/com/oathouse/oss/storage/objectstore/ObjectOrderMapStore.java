/**
 * @(#)ObjectStore.java
 *
 * Copyright:	Copyright (c) 2009
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.storage.exceptions.NoSuchIdentifierException;
import com.oathouse.oss.storage.exceptions.NullObjectException;
import com.oathouse.oss.storage.exceptions.PersistenceException;

/**
 * The {@code ObjectOrderMapStore} Class is a class used to Store other objects and provide a central
 * management class for storing Beans. Beans stored must extend ObjectBean. In addition
 * {@code ObjectOrderMapStore} also maintains the position of the stored objects and allows
 * the objects to be placed at specific index within the store. the order of the store is then
 * kept even through persistence.
 *
 * @param <T> The T specialisation class
 *
 * @author 		Darryl Oatridge
 * @version 	1.00 12-Jun-2010
 * @see ObjectBean
 */
public class ObjectOrderMapStore<T extends ObjectBean> extends ObjectMapStore<T> {

    /**
     * The constructor for {@code ObjectOrderMapStore} Class
     *
     * @param managerName the name under which the persistence data will be stored
     * @param dataOptions [optional] object data options 
     */
    public ObjectOrderMapStore(String managerName, ObjectDataOptionsEnum... dataOptions) {
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
     * @param key reference value to a list of objects
     * @param index the index of the object
     * @param options optional ARCHIVE or PERSIST
     * @return the object T
     * @throws NoSuchIdentifierException
     * @throws PersistenceException
     */
    public T getObjectAt(int key, int index, ObjectDataOptionsEnum... options) throws NoSuchIdentifierException, PersistenceException {
        T obj = getObjectInKeyAt(key, index, options);
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
     * @param key reference value to a list of objects
     * @param options optional ARCHIVE or PERSIST
     * @return the object T
     * @throws PersistenceException
     * @throws NoSuchIdentifierException
     */
    public T getFirstObject(int key, ObjectDataOptionsEnum... options) throws PersistenceException, NoSuchIdentifierException {
        T obj = getFirstObjectInKey(key, options);
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
     * @param key reference value to a list of objects
     * @param options optional ARCHIVE or PERSIST
     * @return the object T
     * @throws PersistenceException
     * @throws NoSuchIdentifierException
     */
    public T getLastObject(int key, ObjectDataOptionsEnum... options) throws PersistenceException, NoSuchIdentifierException {
        T obj = getLastObjectInKey(key, options);
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
     * @param key reference value to a list of objects
     * @param identifier the identifier of the object
     * @param options optional ARCHIVE or PERSIST
     * @return the index of the object
     * @throws PersistenceException
     * @throws NoSuchIdentifierException
     */
    public int getIndexOf(int key, int identifier, ObjectDataOptionsEnum... options) throws PersistenceException, NoSuchIdentifierException {
        int index = getIndexInKeyOf(key, identifier, options);
        if(index >= 0) {
            return (index);
        }
        throw new NoSuchIdentifierException("Unable to get object. The Identifier '" + identifier + "' does not exist");
    }

    /**
     * Sets an object to the first position in the list held within a key
     *
     * @param key reference value to a list of objects
     * @param ob the object to be stored
     * @return The object that was set
     * @throws PersistenceException
     * @throws NullObjectException
     */
    public T setFirstObject(int key, T ob) throws PersistenceException, NullObjectException {
        if(ob == null) {
            throw new NullObjectException("Unable to save object. The object passed is null");
        }
        return (setFirstObjectInKey(key, ob));
    }

    /**
     * Sets an object to the last position in the list held within a key
     *
     * @param key reference value to a list of objects
     * @param ob the object to be stored
     * @return The object that was set
     * @throws PersistenceException
     * @throws NullObjectException
     */
    public T setLastObject(int key, T ob) throws PersistenceException, NullObjectException {
        if(ob == null) {
            throw new NullObjectException("Unable to save object. The object passed is null");
        }
        return (setLastObjectInKey(key, ob));
    }

    /**
     * Sets an object to the specified position index in the list held within a key
     *
     * @param key reference value to a list of objects
     * @param ob the object to be stored
     * @param index the index where the object should be placed
     * @return The object that was set
     * @throws PersistenceException
     * @throws NullObjectException
     */
    public T setObjectAt(int key, T ob, int index) throws PersistenceException, NullObjectException {
        if(ob == null) {
            throw new NullObjectException("Unable to save object. The object passed is null");
        }
        return (setObjectInKeyAt(key, ob, index));
    }

    /**
     * This method allows the creation of a key but with no objects in it.
     *
     * @param key
     * @throws PersistenceException
     */
    @Override
    public void setKey(int key) throws PersistenceException {
        super.setKey(key);
    }
}
