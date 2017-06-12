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
import java.util.List;
import java.util.Set;

/**
 * The {@code ObjectSetStore} Class is a class used to Store other objects and provide a central
 * management class for storing Beans. Beans stored must extend ObjectBean. ObjectSetStore is an
 * implementation of Object Store exposing only a single Map key and should be used when only
 * a single set of related data is to be stored.
 *
 * @param <T> The T specialisation class
 *
 * @author 		Darryl Oatridge
 * @version 	2.01 20-July-2010
 * @see ObjectBean
 */
public class ObjectSetStore<T extends ObjectBean> extends ObjectDBMS<T> {

    /**
     * The constructor for {@code ObjectSetStore} Class used when
     * defining the type of storage required. If no ObjectDataOptionsEnum are
     * defined then set to MEMORY and PERSIST
     *
     * @param managerName the name under which the persistence data will be stored
     * @param dataOptions [optional] object data options
     */
    public ObjectSetStore(String managerName, ObjectDataOptionsEnum... dataOptions) {
        super(managerName, dataOptions);
    }

    /**
     * The constructor for {@code ObjectSetStore} Class, used
     * when persistence isn't required
     */
    public ObjectSetStore() {
        this("", ObjectDataOptionsEnum.MEMORY);
    }

    /**
     * returns the object T with the specified identifier.
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param identifier the identifier of the object T
     * @param options optional ARCHIVE or PERSIST
     * @return T the object bean
     * @throws NoSuchIdentifierException
     * @throws PersistenceException
     */
    public T getObject(int identifier, ObjectDataOptionsEnum... options) throws NoSuchIdentifierException, PersistenceException {
        T rtnObj = getObjectInKey(ObjectEnum.SINGLE_KEY.value(), identifier, options);
        if(rtnObj != null) {
            return (rtnObj);
        }
        throw new NoSuchIdentifierException("Unable to get object. The Identifier '" + identifier + "' does not exist");
    }

    /**
     * returns all the object in the store.
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
        return (super.getAllObjectsInKey(ObjectEnum.SINGLE_KEY.value(), options));
    }

    /**
     * returns all the identifiers currently being held.
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param options optional ARCHIVE or PERSIST
     * @return a set of Integer Id's
     * @throws PersistenceException
     */
    public Set<Integer> getAllIdentifier(ObjectDataOptionsEnum... options) throws PersistenceException {
        return (super.getAllIdentifiersInKey(ObjectEnum.SINGLE_KEY.value(), options));
    }

    /**
     * checks to see if a given object identifier is present
     * <p>
     * Optionally you can include the enumerations ARCHIVE or PERSIST from the ObjectDataOptionsEnum.
     * ARCHIVE will 'get' from current and archive data held on disk.
     * PERISTENCE will 'get' current data from disk even if store type is MEMORY
     * </p>
     *
     * @param identifier the identifier of the object T
     * @param options optional ARCHIVE or PERSIST
     * @return true if found, false if not found
     * @throws PersistenceException
     */
    public boolean isIdentifier(int identifier, ObjectDataOptionsEnum... options) throws PersistenceException {
        if(getAllIdentifier(options).contains(identifier)) {
            return (true);
        }
        return (false);
    }

    /**
     * saves an object to the store and persists the object to disk. If the object
     * identifier exists, the object replaces the existing one and modifies the
     * ObjectBean modified parameter.
     *
     * @param ob the object to be saved
     * @return The object saved
     * @throws PersistenceException
     * @throws NullObjectException the object being passed is null
     */
    public T setObject(T ob) throws PersistenceException, NullObjectException {
        if(ob == null) {
            throw new NullObjectException("Unable to save object. The object passed is null");
        }
        return (super.setObjectInKey(ObjectEnum.SINGLE_KEY.value(), ob));
    }

    /**
     * Removes a object with the provided identifier from the memory store and
     * the persistence store.
     *
     * @param identifier the id of the object to be removed
     * @return the object that was deleted
     * @throws PersistenceException
     */
    public T removeObject(int identifier) throws PersistenceException {
        return super.removeObjectInKey(ObjectEnum.SINGLE_KEY.value(), identifier);
    }

    /**
     * Reinstates an object bean from archive back into persistence and/or memory
     * The manager must have been set up with a store type of ARCHIVE.
     *
     * @param identifier
     * @throws PersistenceException
     */
    public void reinstateObjectFromArchive(int identifier) throws PersistenceException {
        super.reinstateArchiveObject(ObjectEnum.SINGLE_KEY.value(), identifier);
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
