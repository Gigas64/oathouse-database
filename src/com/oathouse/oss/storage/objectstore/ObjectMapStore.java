/**
 * @(#)ObjectMapStore.java
 *
 * Copyright:	Copyright (c) 2009
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.storage.exceptions.NoSuchIdentifierException;
import com.oathouse.oss.storage.exceptions.NullObjectException;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * The {@code ObjectMapStore} Class is a class used to Store other objects and provide a central
 * management class for storing Beans. Beans stored must extend ObjectBean. ObjectSetStore is an
 * implementation of Object Store exposing set of ObjectBeans that can be grouped into key referenced
 * groups.
 *
 * @param <T> The T specialisation class
 *
 * @author 		Darryl Oatridge
 * @version 	2.01 20-July-2010
 * @see ObjectBean
 */
public class ObjectMapStore<T extends ObjectBean> extends ObjectDBMS<T> {

    /**
     * The constructor for {@code ObjectMapStore} Class with preset manager name and
     * optional {@code ObjectBean} storage type
     *
     * @param managerName the name under which the persistence data will be stored
     * @param dataOptions [optional] object data options
     * @see ObjectDataOptionsEnum
     */
    public ObjectMapStore(String managerName, ObjectDataOptionsEnum... dataOptions) {
        super(managerName, dataOptions);
    }

    /**
     * The constructor for {@code ObjectMapStore} Class, used
     * when persistence isn't required
     */
    public ObjectMapStore() {
        this("", ObjectDataOptionsEnum.MEMORY);
    }

    /**
     * returns the object T with the specified identifier and key.
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param key reference value to a set of identifiers
     * @param identifier the identifier of the object T
     * @param options optional ARCHIVE or PERSIST
     * @return T the object bean
     * @throws NoSuchIdentifierException
     * @throws PersistenceException
     */
    public T getObject(int key, int identifier, ObjectDataOptionsEnum... options) throws NoSuchIdentifierException, PersistenceException {
        T obj = getObjectInKey(key, identifier, options);
        if(obj != null) {
            return (obj);
        }
        throw new NoSuchIdentifierException("Unable to get object. The Identifier '" + identifier + "' does not exist");
    }

    /**
     * If the key is unknown this will search all the keys to find the first occurrence of
     * object with the identifier.
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param identifier the identifier of the object T
     * @param options optional ARCHIVE or PERSIST
     * @return The first occurrence of T object
     * @throws NoSuchIdentifierException
     * @throws PersistenceException
     */
    public T getObject(int identifier, ObjectDataOptionsEnum... options) throws NoSuchIdentifierException, PersistenceException {
        for(int key : getAllKeysInMap()) {
            if(getAllIdentifiersInKey(key, options).contains(identifier)) {
                return (getObject(key, identifier, options));
            }
        }
        throw new NoSuchIdentifierException("No object exists with this identifier");
    }

    /**
     * returns all objects for a specified key
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param key reference value to a set of identifiers
     * @param options optional ARCHIVE or PERSIST
     * @return LinkedList of T
     * @throws PersistenceException
     */
    public List<T> getAllObjects(int key, ObjectDataOptionsEnum... options) throws PersistenceException {
        return (super.getAllObjectsInKey(key, options));
    }

    /**
     * returns all objects in every key. This returns the entire store
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param options optional ARCHIVE or PERSIST
     * @return LinkedList of T
     * @throws PersistenceException
     */
    public List<T> getAllObjects(ObjectDataOptionsEnum... options) throws PersistenceException {
        LinkedList<T> rtnList = new LinkedList<>();
        for(int key : super.getAllKeysInMap(options)) {
            rtnList.addAll(super.getAllObjectsInKey(key, options));
        }
        return rtnList;
    }

    /**
     * Returns all the keys that are currently being held
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param options optional ARCHIVE or PERSIST
     * @return set of integers
     * @throws PersistenceException
     */
    public Set<Integer> getAllKeys(ObjectDataOptionsEnum... options) throws PersistenceException {
        return (super.getAllKeysInMap(options));
    }

    /**
     * returns all the identifiers currently being held. This returns every identifier under
     * every key.
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param options optional ARCHIVE or PERSIST
     * @return a set of Integer Id values
     * @throws PersistenceException
     */
    public Set<Integer> getAllIdentifier(ObjectDataOptionsEnum... options) throws PersistenceException {
        Set<Integer> allIdentifier = new ConcurrentSkipListSet<>();
        for(int key : super.getAllKeysInMap(options)) {
            allIdentifier.addAll(super.getAllIdentifiersInKey(key, options));
        }
        return (allIdentifier);
    }

    /**
     * gets all the identifers for a particular key
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param key reference value to a set of identifiers
     * @param options optional ARCHIVE or PERSIST
     * @return a set of integer id values
     * @throws PersistenceException
     */
    public Set<Integer> getAllIdentifier(int key, ObjectDataOptionsEnum... options) throws PersistenceException {
        return (super.getAllIdentifiersInKey(key, options));
    }

    /**
     * returns a set of all keys where the identifier is found within it.
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param identifier the identifier of the object T
     * @param options optional ARCHIVE or PERSIST
     * @return set of key values where the identifier is found in the key.
     * @throws PersistenceException
     */
    public Set<Integer> getAllKeysForIdentifier(int identifier, ObjectDataOptionsEnum... options) throws PersistenceException {
        Set<Integer> rtnSet = new ConcurrentSkipListSet<>();
        for(int key : getAllKeysInMap(options)) {
            if(getAllIdentifiersInKey(key, options).contains(identifier)) {
                rtnSet.add(key);
            }
        }
        return rtnSet;
    }

    /**
     * checks to see if a given identifier is present in all keys
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param identifier identifier the identifier of the object T
     * @param options optional ARCHIVE or PERSIST
     * @return true if found, false if not
     * @throws PersistenceException
     */
    public boolean isIdentifier(int identifier, ObjectDataOptionsEnum... options) throws PersistenceException {
        if(getAllIdentifier(options).contains(identifier)) {
            return (true);
        }
        return (false);
    }

    /**
     * returns true if the identifier exists within a certain key.
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param key reference value to a set of identifiers
     * @param identifier the identifier of the object T
     * @param options optional ARCHIVE or PERSIST
     * @return true if found and false if not
     * @throws PersistenceException
     */
    public boolean isIdentifier(int key, int identifier, ObjectDataOptionsEnum... options) throws PersistenceException {
        if(getAllIdentifier(key, options).contains(identifier)) {
            return (true);
        }
        return (false);
    }

    /**
     * saves an object to the store and persists the object to disk. If the object
     * identifier exists, the object replaces the existing one and modifies the
     * ObjectBean modified parameter.
     *
     * @param key reference value to a set of identifiers
     * @param ob the object bean
     * @return the object set
     * @throws PersistenceException
     * @throws NullObjectException
     */
    public T setObject(int key, T ob) throws PersistenceException, NullObjectException {
        if(ob == null) {
            throw new NullObjectException("Unable to save object. The object passed is null");
        }
        return (super.setObjectInKey(key, ob));
    }

    /**
     * Removes a object with the provided key and identifier from the memory store and
     * the persistence store.
     *
     * @param key reference value to a set of identifiers
     * @param identifier the identifier of the object T
     * @return the object T that was deleted
     * @throws PersistenceException
     */
    public T removeObject(int key, int identifier) throws PersistenceException {
        return super.removeObjectInKey(key, identifier);
    }

    /**
     * Removes a whole key and all the objects within that key from the memory store
     * and from persistence
     *
     * @param key reference value to a set of identifiers
     * @throws PersistenceException
     */
    public void removeKey(int key) throws PersistenceException {
        super.removeAllObjectsInKey(key);
    }

    /**
     * Reinstates a whole key and all objects within that key from archive back
     * into persistence and/or memory store.
     * The manager must have been set up with a store type of ARCHIVE.
     *
     * @param key
     * @throws PersistenceException
     */
    public void reinstateKeyFromArchive(int key) throws PersistenceException {
        super.reinstateArchiveKey(key);
    }

    /**
     * Reinstates an object bean from archive back into persistence and/or memory
     * The manager must have been set up with a store type of ARCHIVE.
     *
     * @param key
     * @param identifier
     * @throws PersistenceException
     */
    public void reinstateObjectFromArchive(int key, int identifier) throws PersistenceException {
        super.reinstateArchiveObject(key, identifier);
    }

    /**
     * Maintenance Method used to tidy out old archive items older than the specified timestamp.
     * If an item still exists in the main store it will not be removed from archive.
     *
     * @param timestamp the timestamp before which to remove archives
     * @throws PersistenceException
     */
    public void removeOldArchive(long timestamp) throws PersistenceException {
        super.removeOldObjectsInArchive(timestamp);
    }
}
