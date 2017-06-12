/**
 * @(#)ObjectStore.java
 *
 * Copyright:	Copyright (c) 2009 Company:	Oathouse.com Ltd
 */
package com.oathouse.oss.storage.objectstore;

import com.oathouse.oss.server.OssProperties;
import com.oathouse.oss.server.handler.ClientRequestHandler;
import com.oathouse.oss.server.handler.FileRequestHandler;
import com.oathouse.oss.server.handler.ObjectRequestHandlerInterface;
import com.oathouse.oss.server.handler.cipher.SimpleStringCipher;
import com.oathouse.oss.storage.exceptions.MaxCountReachedException;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import static com.oathouse.oss.storage.objectstore.ObjectDataOptionsEnum.ARCHIVE;
import static com.oathouse.oss.storage.objectstore.ObjectDataOptionsEnum.CIPHERED;
import static com.oathouse.oss.storage.objectstore.ObjectDataOptionsEnum.COMPACTED;
import static com.oathouse.oss.storage.objectstore.ObjectDataOptionsEnum.MEMORY;
import static com.oathouse.oss.storage.objectstore.ObjectDataOptionsEnum.ORDERED;
import static com.oathouse.oss.storage.objectstore.ObjectDataOptionsEnum.PERSIST;
import static com.oathouse.oss.storage.objectstore.ObjectDataOptionsEnum.TRIMMED;
import com.oathouse.oss.storage.valueholder.CalendarStatic;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

/**
 * The {@code ObjectDBMS} Class is an abstract class that contains the core methods for the storage of T objects that
 * extend ObjectBean. T Objects are reference through a {@code Map<Integer, Map<Integer, T>>} by: <p> key -&gt; Identifier
 * -&gt; T </p> <p> NOTE: The system properties must be set: <br> server.host server.port application.authority </p>
 *
 * @param <T> The T specialisation class
 *
 * @author Darryl Oatridge
 * @version 3.00 18-December-2010
 * @see ObjectBean
 * @see ObjectSetStore
 * @see ObjectMapStore
 * @see ObjectOrderSetStore
 * @see ObjectOrderMapStore
 */
public abstract class ObjectDBMS<T extends ObjectBean> {

    // logger that is set up via a properites file found in ossProperties
    private static Logger logger = Logger.getLogger(ObjectDBMS.class);
    // the reference that has created this instance (used for logging)
    private final String instanceRef;
    // data persistence information
    private final ObjectRequestHandlerInterface requestHandler;
    // default object
    private volatile T defaultObject;
    // memory store
    private volatile ConcurrentSkipListMap<Integer, ConcurrentSkipListMap<Integer, T>> objectMap;
    // initialisation check
    private volatile boolean initialised;
    // The storage type options
    private final ObjectDataOptionsEnum[] storeType;
    // time reserved id map:  id -&gt; timestamp
    private final ConcurrentSkipListMap<Integer, Long> timeReservedExcludesMap;

    /**
     * The constructor for {@code ObjectDBMS} Class provides information to set up a connection the the persistence
     * server along with the type of data store to implement. <p> The host and port identify the persistence server to
     * connect to while the groupTag and sectionTag section how the data will be stored on the server. The groupTag is a
     * high level tag so application wide data can be grouped together. The sectionTag then allows data to be sub
     * sectioned under the collective groupTag. If Manager Pattern is being used the groupTag would be an application
     * instance identifier while the sectionTag would be a manager instance identifier. </p> <p> The store type
     * identifies how the underlying data should be stored. The options are SERVER_ONLY, PERSIST_MEMORY, MEMORY_ONLY.
     * </p>
     *
     * @param manager an identifier for the module instance
     * @param storageOptions an array of the type of storage to be used.
     */
    public ObjectDBMS(String manager, ObjectDataOptionsEnum... storageOptions) {
        this.defaultObject = null;
        this.objectMap = null;
        this.initialised = false;
        this.timeReservedExcludesMap = new ConcurrentSkipListMap<>();

        // ObjectStore Rules:
        if(!MEMORY.isIn(storageOptions) && !PERSIST.isIn(storageOptions)) {
            // if no store types then add both memory and persistence
            storeType = ObjectDataOptionsEnum.addToArray(storageOptions, MEMORY, PERSIST);
        } else if(!PERSIST.isIn(storageOptions) && ARCHIVE.isIn(storageOptions)) {
            // if archive must have persistence
            storeType = ObjectDataOptionsEnum.addToArray(storageOptions, PERSIST);
        } else if(!PERSIST.isIn(storageOptions) && ORDERED.isIn(storageOptions)) {
            // if ORDERED must have persistence
            storeType = ObjectDataOptionsEnum.addToArray(storageOptions, PERSIST);
        } else {
            storeType = storageOptions;
        }

        // set up the data handler
        final String rootStorePath = OssProperties.getInstance().getStorePath();
        final String authority = OssProperties.getInstance().getAuthority();
        switch(OssProperties.getInstance().getConnection()) {
            case NIO:
                this.requestHandler = new ClientRequestHandler(manager);
                break;
            case FILE: // file based system
            default:
                // default to file
                this.requestHandler = new FileRequestHandler(rootStorePath, authority, manager);
        }
        this.instanceRef = "[" + authority + "->" + manager + "] ";

        // set up the log file
        String loggerConfig = OssProperties.getInstance().getLogConfigFile();
        if(loggerConfig == null || !(new File(loggerConfig).exists())) {
            logger.setLevel(Level.OFF);
        } else {
            PropertyConfigurator.configure(loggerConfig);
        }
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "Constructed\n" + OssProperties.getInstance().toString());
        }
    }

    /**
     * Upon creation of an instance of the class it has to be initialised. If this is not done then a
     * PersistenceException is thrown. Initialisation ensures all stored data is loaded and appropriate values
     * initialised. This method can also be used to re-initialise
     *
     * @return
     * @throws PersistenceException
     */
    public ObjectDBMS<T> init() throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- init()");
        }
        if(MEMORY.isIn(storeType)) {
            // clean out the object map.
            // don't use clear() as unreliable
            objectMap = null;
            objectMap = new ConcurrentSkipListMap<>();

            // if isMemory and isPersist then we need to load the peristence into memory
            if(PERSIST.isIn(storeType)) {
                // check the server exists
                if(!requestHandler.isAlive()) {
                    throw new PersistenceException("The percistence store is not responding");
                }
                for(int key : requestHandler.getKeys()) {
                    objectMap.put(key, new ConcurrentSkipListMap<Integer, T>());
                    for(int id : requestHandler.getObjectIds(key)) {
                        objectMap.get(key).put(id, buildObjectBean(requestHandler.getObject(key, id), key, id));
                    }
                    if(ORDERED.isIn(storeType)) {
                        rebuldOrderBean(key, (ObjectOrderBean) buildObjectBean(requestHandler.getOrder(key), key, key));
                    }
                }
                // finished so exit
                requestHandler.exit();
            }
        }
        this.initialised = true;
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "Initialisation Complete");
        }
        return (this);
    }

    /**
     * Returns the manager name as set at instantiation.
     *
     * @return the name of the manager
     */
    public String getManager() {
        return requestHandler.getManager();
    }

    /**
     * Returns the authority name as set from OssProperties.
     *
     * @return the name of the authority
     */
    public String getAuthority() {
        return requestHandler.getAuthority();
    }

    /**
     * Returns the default Object or null if no object has been set.
     *
     * @return the default Object or null if not set
     */
    public T getDefaultObject() {
        return defaultObject;
    }

    /**
     * Sets or resets the default object. If the default object is set, when an Object is requested but not found, the
     * default Object will be returned instead. This default object will negate an NoSuchIdentifierException
     *
     * @param defaultObject
     */
    public void resetDefaultObject(T defaultObject) {
        defaultObject.setIdentifier(ObjectEnum.DEFAULT_ID.value());
        this.defaultObject = defaultObject;
    }

    /**
     * Used to generate a unique id when adding a new Bean. This finds the highest unused number from 1 and does not
     * reuse previously generated and deleted numbers. For example if the id 1 and 4 were used then the return id would
     * be 5
     *
     * @return a unique id
     * @throws MaxCountReachedException has exceeded Integer MAX_VALUE
     * @throws PersistenceException
     */
    public int generateIdentifier() throws MaxCountReachedException, PersistenceException {
        return (generateIdentifier(1, 300));
    }

    /**
     * Used to generate a unique id when adding a new Bean. This finds the highest unique number from 1 and does not
     * reuse previously generated and deleted numbers. For example if the id 1 and 4 were used then the return id would
     * be 5. The startValue indicates from where the search for the highest unique number should start. The timeout
     * is a period of time in milliseconds the generated number should remain excluded. This allows a number that has
     * been newly generated to be reserved for a period of time before it has to be used as an ObjectBean identifier or
     * released for consideration as a new identifier.
     *
     * @param startValue the value to start the count from.
     * @param exclusionTimeout a time value in milliseconds identifier should remain excluded.
     * @return a unique id
     * @throws MaxCountReachedException has exceeded Integer MAX_VALUE
     * @throws PersistenceException
     */
    public int generateIdentifier(int startValue, int exclusionTimeout) throws MaxCountReachedException, PersistenceException {
        // get all the current ids out
        ConcurrentSkipListSet<Integer> ids = new ConcurrentSkipListSet<>();
        for(int key : getAllKeysInMap()) {
            ids.addAll(getAllIdentifiersInKey(key));
        }
        // add all the time reserve exclusions
        ids.addAll(getTimeReservedExclusions());

        // find the highest
        int highestId = 1;
        if(!ids.isEmpty()) {
            highestId = ids.last() + 1;
        }
        // check we are not near the ceiling. 10 from the top is just an abratary buffer.
        if(highestId < (Integer.MAX_VALUE - 10)) {
            int rtnId = highestId < startValue ? startValue : highestId;
            return this.setTimeReservedExclusion(rtnId, exclusionTimeout);
        }
        throw new MaxCountReachedException("Identifier allocation used up from start number '" + startValue + "'");
    }

    /**
     * Used to regenerate a unique id when adding a new Bean. This automatically starts from 1 and will look to reuse
     * identifiers where possible. For example if the id 1 and 4 were used then the return id would be 2.
     *
     * @return a unique id
     * @throws MaxCountReachedException has exceeded Integer MAX_VALUE
     * @throws PersistenceException
     */
    public int regenerateIdentifier() throws MaxCountReachedException, PersistenceException {
        return (regenerateIdentifier(1, new ConcurrentSkipListSet<Integer>(), 300));
    }

    /**
     * Used to regenerate a unique id when adding a new Bean. This automatically starts from 1 and will look to reuse
     * identifiers where possible. For example if the id 1 and 4 were used then the return id would be 2. The startValue
     * indicates from where the search for a reusable unique number should start. the excludes are a list of integers to
     * exclude from the search. The timeout is a period of time in milliseconds the generated number should remain
     * excluded. This allows a number that has been newly generated to be reserved for a period of time before it has
     * to be used as an ObjectBean identifier or released for consideration as a new identifier.
     *
     * @param startValue the value to start the count from.
     * @param excludes additional values to exclude
     * @param exclusionTimeout a time value in milliseconds to reserve the generated number.
     * @return a unique id
     * @throws MaxCountReachedException has exceeded Integer MAX_VALUE
     * @throws PersistenceException
     */
    public int regenerateIdentifier(int startValue, Set<Integer> excludes, int exclusionTimeout) throws MaxCountReachedException, PersistenceException {
        ConcurrentSkipListSet<Integer> ids = new ConcurrentSkipListSet<>();
        // add all the excluded numbers
        if(excludes != null) {
            ids.addAll(excludes);
        }
        // add all the numbers currently in use
        for(int key : getAllKeysInMap()) {
            ids.addAll(getAllIdentifiersInKey(key));
        }
        // add all the time reserve exclusions
        ids.addAll(getTimeReservedExclusions());

        // find a unique number
        for(int rtnId = startValue; rtnId < Integer.MAX_VALUE; rtnId++) {
            if(!ids.contains(rtnId) && !ObjectEnum.isReserved(rtnId)) {
                return this.setTimeReservedExclusion(rtnId, exclusionTimeout);
            }
        }
        throw new MaxCountReachedException("Identifier allocation used up from start number '" + startValue + "'");
    }

    /**
     * used to reset the time reserve exclusions so no identifiers are now reserved. This utility
     * method can be used after a sequence of number generators has been called without setting the
     * object. It ensures no identifiers are still being reserved that are not going to be used without
     * waiting for the reserve timeout to expire.
     */
    public void resetTimeReserveExclusions() {
        timeReservedExcludesMap.clear();
        // just to make sure
        for(int key : timeReservedExcludesMap.keySet()) {
            timeReservedExcludesMap.remove(key);
        }
    }

    /**
     * Provides a safe way of fully cloning an ObjectBean T
     *
     * @param identifier the new identifier for the clone
     * @param ob the T to be cloned
     * @return the cloned T
     * @throws PersistenceException
     */
    public T cloneObjectBean(int identifier, T ob) throws PersistenceException {
        T rtnOb = buildObjectBean(this.encrypt(ob.toXML(TRIMMED, COMPACTED)), ob.getGroupKey(), ob.getIdentifier());
        rtnOb.setIdentifier(identifier);
        return (rtnOb);
    }

    /**
     * removes all objects. USE WITH CARE!
     *
     * @return false if the clear() call fails
     * @throws PersistenceException
     */
    public boolean clear() throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- clear()");
        }
        //don't use clear() as unreliable
        objectMap = null;
        objectMap = new ConcurrentSkipListMap<>();

        if(PERSIST.isIn(storeType)) {
            boolean rtn = requestHandler.clear();
            // finished so exit
            requestHandler.exit();
            return (rtn);
        }
        return (true);
    }

    /**
     * Cleanly shuts down the connection to the server
     *
     * @throws PersistenceException
     */
    public void shutdown() throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- shutdown()");
        }
        this.defaultObject = null;
        this.objectMap = null;
        this.initialised = false;
        if(PERSIST.isIn(storeType)) {
            requestHandler.exit();
        }
    }

    /**
     * Static method to retrieve all currently held authorities in persistence
     *
     * @return set of authority names
     * @throws PersistenceException
     */
    public static Set<String> getAllAuthorities() throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug("[Static Global] - getAuthorities()");
        }
        String rootStorePath = OssProperties.getInstance().getStorePath();
        switch(OssProperties.getInstance().getConnection()) {
            case FILE: // file based system
                return FileRequestHandler.getAllAuthorities(rootStorePath);
            case NIO:
                return null;
            default:
                // default to file
                return null;
        }
    }

    /**
     * Static method generally used for testing to clear out the data store for an authority in persistence.
     *
     * @param authority the name of the authority
     * @return true if the authority was cleared
     */
    public static boolean clearAuthority(String authority) {
        if(logger.isDebugEnabled()) {
            logger.debug("[Static Global] - clearAuthority(" + authority + ")");
        }
        String rootStorePath = OssProperties.getInstance().getStorePath();
        switch(OssProperties.getInstance().getConnection()) {
            case FILE: // file based system
                return FileRequestHandler.clearAuthority(rootStorePath, authority);
            case NIO:
                return false;
            default:
                // default to file
                return false;
        }
    }

    /**
     * Static method to retrieve all the managers held in persistence.
     *
     * @param authority the name of the authority
     * @return set of Manager names
     * @throws PersistenceException
     */
    public static Set<String> getAllManagers(String authority) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug("[Static Global] - getManagers()");
        }
        String rootStorePath = OssProperties.getInstance().getStorePath();
        switch(OssProperties.getInstance().getConnection()) {
            case FILE: // file based system
                return FileRequestHandler.getAllManagers(rootStorePath, authority);
            case NIO:
                return null;
            default:
                // default to file
                return null;
        }
    }

    /*
     * ********************************************************
     * P R O T E C T E D    G E T    M E T H O D S
     * ******************************************************
     */
    /**
     * returns the T specialisation class referenced by key and identifier. The options
     * provides a mechanism to override the default behaviour and force persistence retrieval
     * using PERSIST or force archive retrieval using ARCHIVE.
     *
     * @param key reference value to a set of identifiers
     * @param identifier reference value to a set of T objects
     * @param options option choices for persistence data retrieval
     * @return T object
     * @throws PersistenceException
     * @see ObjectDataOptionsEnum
     */
    protected T getObjectInKey(int key, int identifier, ObjectDataOptionsEnum... options) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- getObjectInKey( key = " + key + ", id = " + identifier + ")");
        }
        T rtnObject = null;
        if(ARCHIVE.isIn(storeType) && ARCHIVE.isIn(options)) {
            rtnObject = getObjectFromDisk(key, identifier, ARCHIVE);
        } else if(!MEMORY.isIn(storeType) || PERSIST.isIn(options)) {
            rtnObject = getObjectFromDisk(key, identifier);
        } else if(initialised) {
            // must be memory based so get from objectMap
            if(objectMap.containsKey(key) && objectMap.get(key).containsKey(identifier)) {
                rtnObject = objectMap.get(key).get(identifier);
            } else if(defaultObject != null) {
                rtnObject = defaultObject;
            }
        } else {
            throw new PersistenceException(this.getClass().getSimpleName() + " has not been initialised");
        }
        if(logger.isTraceEnabled()) {
            if(rtnObject == null) {
                logger.trace("return : NULL");
            } else {
                logger.trace("return :\n" + rtnObject.toString());
            }
        }
        return (rtnObject);
    }

    /**
     * retrieves an object at the specific index in a List referenced by the key
     *
     * @param key reference value to a list of objects
     * @param index the index of the object
     * @param options option choices for persistence data retrieval
     * @return the object T or null if not found
     * @throws PersistenceException
     */
    protected T getObjectInKeyAt(int key, int index, ObjectDataOptionsEnum... options) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- getObjectInKeyAt( key = " + key + ", index = " + index + ")");
        }
        if(ORDERED.isIn(storeType)) {
            if(initialised) {
                final ObjectOrderBean orderBean = getOrderBean(key, options);
                if(orderBean != null) {
                    if(index >= 0 && index < orderBean.getIdCount()) {
                        int id = orderBean.getIdAt(index);
                        return getObjectInKey(key, id, options);
                    }
                }
                if(logger.isTraceEnabled()) {
                    logger.trace("return : NULL");
                }
                return (null);
            }
            throw new PersistenceException(this.getClass().getSimpleName() + " has not been initialised");
        }
        throw new PersistenceException(this.getClass().getSimpleName() + " does not support ordered ObjectBeans");
    }

    /**
     * gets the first object in the list referenced by the key
     *
     * @param key reference value to a list of objects
     * @param options option choices for persistence data retrieval
     * @return the object T
     * @throws PersistenceException
     */
    protected T getFirstObjectInKey(int key, ObjectDataOptionsEnum... options) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- getFirstObjectInKey( key = " + key + ")");
        }
        if(ORDERED.isIn(storeType)) {
            if(initialised) {
                final ObjectOrderBean orderBean = getOrderBean(key, options);
                if(orderBean != null) {
                    if(orderBean.getIdCount() > 0) {
                        int id = orderBean.getFirstId();
                        return getObjectInKey(key, id, options);
                    }
                }
                if(logger.isTraceEnabled()) {
                    logger.trace("return : NULL");
                }
                return (null);
            }
            throw new PersistenceException(this.getClass().getSimpleName() + " has not been initialised");
        }
        throw new PersistenceException(this.getClass().getSimpleName() + " does not support ordered ObjectBeans");
    }

    /**
     * gets the last object in the list referenced by the key
     *
     * @param key reference value to a list of objects
     * @param options option choices for persistence data retrieval
     * @return the object T
     * @throws PersistenceException
     */
    protected T getLastObjectInKey(int key, ObjectDataOptionsEnum... options) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- getLastObjectInKey( key = " + key + ")");
        }
        if(ORDERED.isIn(storeType)) {
            if(initialised) {
                final ObjectOrderBean orderBean = getOrderBean(key, options);
                if(orderBean != null) {
                    if(orderBean.getIdCount() > 0) {
                        int id = orderBean.getLastId();
                        return getObjectInKey(key, id, options);
                    }
                }
                if(logger.isTraceEnabled()) {
                    logger.trace("return : NULL");
                }
                return (null);
            }
            throw new PersistenceException(this.getClass().getSimpleName() + " has not been initialised");
        }
        throw new PersistenceException(this.getClass().getSimpleName() + " does not support ordered ObjectBeans");
    }

    /**
     * gets the index of an object referenced by the key where the object has the identifier passed
     *
     * @param key reference value to a list of objects
     * @param identifier the identifier of the object
     * @param options option choices for persistence data retrieval
     * @return the index of the object
     * @throws PersistenceException
     */
    protected int getIndexInKeyOf(int key, int identifier, ObjectDataOptionsEnum... options) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- getIndexInKeyOf( key = " + key + ", id = " + identifier + ")");
        }
        if(ORDERED.isIn(storeType)) {
            if(initialised) {
                int index = getOrderBean(key, options).getIndexOf(identifier);
                if(logger.isTraceEnabled()) {
                    logger.trace("return : " + index);
                }
                return (index);
            }
            throw new PersistenceException(this.getClass().getSimpleName() + " has not been initialised");
        }
        throw new PersistenceException(this.getClass().getSimpleName() + " does not support ordered ObjectBeans");
    }

    /**
     * returns all the objects held under a key reference sorted by the comparable algorithm. The default is by Id.
     *
     * @param key reference value to a set of identifiers
     * @param options option choices for persistence data retrieval
     * @return a list of T objects
     * @throws PersistenceException
     */
    protected LinkedList<T> getAllObjectsInKey(int key, ObjectDataOptionsEnum... options) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- getAllObjectsInKey( key = " + key + ")");
        }
        if(ARCHIVE.isIn(options) && ARCHIVE.isIn(storeType)) {
            return (getAllObjectForKeyFromDisk(key, ARCHIVE));
        }
        if(!MEMORY.isIn(storeType) || (PERSIST.isIn(options)) && PERSIST.isIn(storeType)) {
            return (getAllObjectForKeyFromDisk(key));
        }
        // must be memory based so get from objectMap
        if(initialised) {
            LinkedList<T> rtnList;
            if(objectMap.containsKey(key)) {
                if(ORDERED.isIn(storeType)) {
                    return getAllOrderedObjectsInKey(key);
                }
                rtnList = new LinkedList<>(objectMap.get(key).values());
                Collections.sort(rtnList);
                return rtnList;
            }
            return new LinkedList<>();
        }
        throw new PersistenceException(this.getClass().getSimpleName() + " has not been initialised");
    }

    /**
     * returns all the objects held under a key reference sorted by the order in which they were entered.
     *
     * @param key reference value to a list of objects
     * @return a list of objects T
     * @throws PersistenceException
     */
    private LinkedList<T> getAllOrderedObjectsInKey(int key, ObjectDataOptionsEnum... options) throws PersistenceException {
        if(ORDERED.isIn(storeType)) {
            if(initialised) {
                final LinkedList<T> rtnList = new LinkedList<>();
                final ObjectOrderBean orderBean;
                orderBean = getOrderBean(key, options);
                for(int i = 0; i < orderBean.getIdCount(); i++) {
                    rtnList.add(getObjectInKey(key, orderBean.getIdAt(i), options));
                }
                return (rtnList);
            }
            throw new PersistenceException(this.getClass().getSimpleName() + " has not been initialised");
        }
        throw new PersistenceException(this.getClass().getSimpleName() + " does not support ordered ObjectBeans");
    }

    /**
     * Returns a set of identifiers held under a key
     *
     * @param key reference value to a set of identifiers
     * @param options
     * @return set of identifiers
     * @throws PersistenceException
     */
    protected Set<Integer> getAllIdentifiersInKey(int key, ObjectDataOptionsEnum... options) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- getAllIdentifiersInKey( key = " + key + ")");
        }
        if(ARCHIVE.isIn(options) && ARCHIVE.isIn(storeType)) {
            try {
                return (requestHandler.getObjectIdsArchive(key));
            } finally {
                // exit handler
                requestHandler.exit();
            }
        }
        if(!MEMORY.isIn(storeType) || (PERSIST.isIn(options)) && PERSIST.isIn(storeType)) {
            try {
                return (requestHandler.getObjectIds(key));
            } finally {
                // exit handler
                requestHandler.exit();
            }
        }
        // must be memory based so get from objectMap
        if(initialised) {
            if(objectMap.containsKey(key)) {
                return (objectMap.get(key).keySet());
            }
        }
        return (new ConcurrentSkipListSet<>());
    }

    /**
     * returns all the keys being held
     *
     * @param options
     * @return set of keys
     * @throws PersistenceException
     */
    protected Set<Integer> getAllKeysInMap(ObjectDataOptionsEnum... options) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- getAllKeysInMap()");
        }
        ConcurrentSkipListSet<Integer> rtnSet = new ConcurrentSkipListSet<>();
        if(ARCHIVE.isIn(options) && ARCHIVE.isIn(storeType)) {
            try {
                rtnSet.addAll(requestHandler.getKeysArchive());
            } finally {
                // exit handler
                requestHandler.exit();
            }
            return (rtnSet);
        }
        if(!MEMORY.isIn(storeType) || (PERSIST.isIn(options)) && PERSIST.isIn(storeType)) {
            try {
                rtnSet.addAll(requestHandler.getKeys());
            } finally {
                // exit handler
                requestHandler.exit();
            }
            return (rtnSet);
        }
        // must be memory based so get from objectMap
        if(initialised) {
            return (objectMap.keySet());
        }
        return (rtnSet);
    }

    /*
     * **********************************************
     * P R O T E C T E D    C H E C K    M E T H O D S
     * *********************************************
     */
    /**
     * returns the default T Object being held or null if non found
     *
     * @return The default T object
     */
    protected T checkDefault() {
        return defaultObject;
    }

    /**
     * a check to see if the class has been initialised
     *
     * @return true if has been initialised, false if not
     */
    protected boolean isInitialised() {
        return initialised;
    }

    /*
     * *******************************************
     * P R O T E C T E D    S E T    M E T H O D S
     * ******************************************
     */
    /**
     * Saves an object
     *
     * @param key reference value to a set of identifiers
     * @param ob the object to be saved
     * @return The T object @returnReturns a copy of the saved Object.
     * @throws PersistenceException
     */
    protected T setObjectInKey(int key, T ob) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- setObjectInKey( key = " + key + ", ObjectBean = " + ob.getClass().getSimpleName() + ")");
        }
        return saveObject(key, ob, -1);
    }

    /**
     * Sets an object to the first position in the list held within a key
     *
     * @param key reference value to a list of objects
     * @param ob the object to be stored
     * @return
     * @throws PersistenceException
     */
    protected T setFirstObjectInKey(int key, T ob) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- setFirstObjectInKey( key = " + key + ", ObjectBean = " + ob.getClass().getSimpleName() + ")");
        }
        return saveObject(key, ob, 0);
    }

    /**
     * Sets an object to the last position in the list held within a key
     *
     * @param key reference value to a list of objects
     * @param ob the object to be stored
     * @return
     * @throws PersistenceException
     */
    protected T setLastObjectInKey(int key, T ob) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- setLastObjectInKey( key = " + key + ", ObjectBean = " + ob.getClass().getSimpleName() + ")");
        }
        return saveObject(key, ob, -1);
    }

    /**
     * Sets an object to the specified position index in the list held within a key
     *
     * @param key reference value to a list of objects
     * @param ob the object to be stored
     * @param index the index where the object should be placed
     * @return
     * @throws PersistenceException
     */
    protected T setObjectInKeyAt(int key, T ob, int index) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- setObjectInKeyAt( key = " + key + ", ObjectBean = " + ob.getClass().getSimpleName() + ", index = " + index + ")");
        }
        return saveObject(key, ob, index);
    }

    /**
     * This method allows the creation of a key but with no objects in it.
     *
     * @param key
     * @throws PersistenceException
     */
    protected void setKey(int key) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- setKey( key = " + key + ")");
        }
        if(MEMORY.isIn(storeType)) {
            if(!initialised) {
                throw new PersistenceException(this.getClass().getSimpleName() + " has not been initialised");
            }
            saveObjectToMemory(key, null);
        }
    }

    private T saveObject(int key, T ob, int index) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            if(ob == null) {
                logger.trace(" Object is NULL");
            } else {
                logger.trace("set :\n" + ob.toString());

            }
        }
        // save to memory
        if(MEMORY.isIn(storeType) && ob != null) {
            if(initialised) {
                // save to map which will adjust the modify if exists (pass by reference)
                saveObjectToMemory(key, ob);
            } else {
                // you need to initialise first
                throw new PersistenceException(this.getClass().getSimpleName() + " has not been initialised");
            }
        }
        //save to percistence
        if(PERSIST.isIn(storeType) && ob != null) {
            // save to disk
            try {
                String xmlString = this.encrypt(ob.toXML(storeType));
                if(requestHandler.setObject(key, ob.getIdentifier(), xmlString)) {
                    // set in archive
                    if(ARCHIVE.isIn(storeType)) {
                        requestHandler.setObjectArchive(key, ob.getIdentifier(), xmlString);
                        // save archive order
                        if(ORDERED.isIn(storeType)) {
                            final ObjectOrderBean orderBean = getOrderBean(key, ARCHIVE);
                            if(orderBean.containsId(ob.getIdentifier())) {
                                orderBean.removeId(ob.getIdentifier());
                            }
                            orderBean.addIdAt(index, ob.getIdentifier());
                            saveOrder(key, orderBean, ARCHIVE);
                        }
                    }
                    // save order if ordered
                    if(ORDERED.isIn(storeType)) {
                        final ObjectOrderBean orderBean = getOrderBean(key);
                        if(orderBean.containsId(ob.getIdentifier())) {
                            orderBean.removeId(ob.getIdentifier());
                        }
                        orderBean.addIdAt(index, ob.getIdentifier());
                        saveOrder(key, orderBean);
                    }
                    return (ob);
                }
            } finally {
                // exit handler
                requestHandler.exit();
            }
            throw new PersistenceException("Server Error: Unable to save object to persistence");
        }
        return (ob);
    }

    /*
     * *************************************************
     * P R O T E C T E D    R E M O V E    M E T H O D S
     * ************************************************
     */
    /**
     * removes an object and identifier based on key and identifier values
     *
     * @param key reference value to a set of identifiers
     * @param identifier reference value to a set of T objects
     * @return T Object
     * @throws PersistenceException
     */
    protected T removeObjectInKey(int key, int identifier) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- removeObjectInKey( key = " + key + ", id = " + identifier + ")");
        }
        T rtnObject = null;
        if(MEMORY.isIn(storeType)) {
            if(!initialised) {
                throw new PersistenceException(this.getClass().getSimpleName() + " has not been initialised");
            }
            if(objectMap.containsKey(key)) {
                rtnObject = objectMap.get(key).remove(identifier);
                if(objectMap.get(key).isEmpty()) {
                    objectMap.remove(key);
                }
            }
        }
        if(PERSIST.isIn(storeType)) {
            try {
                rtnObject = getObjectFromDisk(key, identifier);
                if(rtnObject != null) {
                    if(requestHandler.removeObject(key, identifier)) {
                        if(ORDERED.isIn(storeType)) {
                            ObjectOrderBean orderBean = getOrderBean(key);
                            if(orderBean.containsId(identifier)) {
                                orderBean.removeId(identifier);
                            }
                            saveOrder(key, orderBean);
                        }
                    } else {
                        throw new PersistenceException("Server Error: Unable to remove object from persistence");
                    }
                    if(ARCHIVE.isIn(storeType)) {
                        // set the archive date to today for the archive object
                        if(!this.setArchiveYwd(key, identifier, CalendarStatic.getRelativeYWD(0))) {
                            logger.warn("Server Warning: Unable to set Archive YWD for removed object: key[" + key + "] id[" + identifier + "]");
                        }
                    }
                }
            } finally {
                // exit handler
                requestHandler.exit();
            }
        }
        return rtnObject;
    }

    /**
     * removes all the objects and identifiers under a key
     *
     * @param key reference value to a set of identifiers
     * @throws PersistenceException
     */
    protected void removeAllObjectsInKey(int key) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- removeAllObjectsInKey( key = " + key + ")");
        }
        if(MEMORY.isIn(storeType)) {
            if(!initialised) {
                throw new PersistenceException(this.getClass().getSimpleName() + " has not been initialised");
            }
            objectMap.remove(key);

        }
        if(PERSIST.isIn(storeType)) {
            try {
                if(!requestHandler.removeAllObjects(key)) {
                    throw new PersistenceException("Server Error: Unable to remove key from persistence");
                }
                if(ORDERED.isIn(storeType) && !requestHandler.removeOrder(key)) {
                    throw new PersistenceException("Server Error: Unable to remove order file from persistence");
                }
                if(ARCHIVE.isIn(storeType)) {
                    // set Archive data for all the object in the key
                    for(int identifier : requestHandler.getObjectIdsArchive(key)) {
                        this.setArchiveYwd(key, identifier, CalendarStatic.getRelativeYWD(0));
                    }
                }
            } finally {
                // exit handler
                requestHandler.exit();
            }
        }
    }

    /**
     * re-instate an archived key back into persistence with all the objects under that key. If applicable the order of the
     * objects is maintained.
     *
     * @param key the key to reinstate from Archive
     * @throws PersistenceException
     */
    protected void reinstateArchiveKey(int key) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- reinstateArchiveKey( key = " + key + ")");
        }
        if(!ARCHIVE.isIn(storeType)) {
            throw new PersistenceException(this.getClass().getSimpleName() + " does not support ARCHIVE");
        }
        // if there is no key in archive then throw exception
        if(!getAllKeysInMap(ARCHIVE).contains(key)) {
            throw new PersistenceException("Archive Error: The key " + key + " does not exist in the Archive");
        }
        // take each of the objects out of the key
        for(int id : getAllIdentifiersInKey(key, ARCHIVE)) {
            reinstateArchiveObject(key, id);
        }
    }

    /**
     *
     * @param key
     * @param identifier
     * @throws PersistenceException
     */
    protected void reinstateArchiveObject(int key, int identifier) throws PersistenceException {
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- reinstateArchiveObject( key = " + key + ", id = " + identifier + ")");
        }
        if(!ARCHIVE.isIn(storeType)) {
            throw new PersistenceException(this.getClass().getSimpleName() + " does not support ARCHIVE");
        }
        if(!getAllKeysInMap(ARCHIVE).contains(key)) {
            throw new PersistenceException("Archive Error: The key " + key + " does not exist in the Archive");
        }
        if(!getAllIdentifiersInKey(key, ARCHIVE).contains(identifier)) {
            throw new PersistenceException("Archive Error: The id " + identifier + " in key " + key + " does not exist in the Archive");
        }
        // get the object and reset the Archive date
        T ob = getObjectFromDisk(key, identifier, ARCHIVE);
        ob.setArchiveYwd(ObjectEnum.DEFAULT_VALUE.value());
        if(ORDERED.isIn(storeType)) {
            setObjectInKeyAt(key, ob, getIndexInKeyOf(key, identifier, ARCHIVE));
        } else {
            setObjectInKey(key, ob);
        }
    }

    /**
     *
     * @param fromTimestamp
     * @throws PersistenceException
     */
    protected void removeOldObjectsInArchive(long fromTimestamp) throws PersistenceException{
        if(logger.isDebugEnabled()) {
            logger.debug(instanceRef + "- removeAllObjectsInArchive( fromTimestamp = " + fromTimestamp + ")");
        }
        if(ARCHIVE.isIn(storeType)) {
            for(int key : getAllKeysInMap(ARCHIVE)) {
                // set of current Ids to check against
                Set<Integer> persistenceIdSet = getAllIdentifiersInKey(key, PERSIST);
                for(T ob : getAllObjectsInKey(key, ARCHIVE)) {
                    int identifier = ob.getIdentifier();
                    // older than the timestamp or still exists in persistence
                    if(ob.getArchiveYwd() <= ObjectEnum.DEFAULT_KEY.value() ||ob.getArchiveYwd() > fromTimestamp || persistenceIdSet.contains(identifier)) {
                        continue;
                    }
                    // remove the archive object
                    try {
                        requestHandler.removeObjectArchive(key, ob.getIdentifier());
                    } finally {
                        // exit handler
                        requestHandler.exit();
                    }
                }
                if(ORDERED.isIn(storeType)) {
                    this.rebuldOrderBean(key, null, ARCHIVE);
                }
            }
        }
    }


    /*
     * ********************************************************
     * P R I V A T E    M E T H O D S    F O R    O R D E R
     * ******************************************************
     */
    private ObjectOrderBean getOrderBean(int key, ObjectDataOptionsEnum... options) throws PersistenceException {
        try {
            String xmlString = ARCHIVE.isIn(options) ? requestHandler.getOrderArchive(key) : requestHandler.getOrder(key);
            ObjectOrderBean orderBean = (ObjectOrderBean) buildObjectBean(xmlString, key, key);
            if(orderBean == null || orderBean.getIdCount() != getAllIdentifiersInKey(key, options).size()) {
                orderBean = rebuldOrderBean(key, orderBean, options);
            }
            return (orderBean);
        } finally {
            // exit handler
            requestHandler.exit();
        }
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    private ObjectOrderBean rebuldOrderBean(int key, ObjectOrderBean orderBean, ObjectDataOptionsEnum... options) throws PersistenceException {
        if(orderBean == null) {
            orderBean = new ObjectOrderBean(key, new LinkedList<Integer>(), ObjectBean.SYSTEM_OWNED);
        }
        Set<Integer> allIds = ARCHIVE.isIn(options) ? requestHandler.getObjectIdsArchive(key) : requestHandler.getObjectIds(key);
        // add any missing
        for(int id : allIds) {
            if(!orderBean.containsId(id)) {
                orderBean.addLastId(id);
            }
        }
        // remove any that shouldn't be there
        if(orderBean.getIdCount() != allIds.size()) {
            for(int i = 0; i < orderBean.getIdCount(); i++) {
                int id = orderBean.getIdAt(i);
                if(!allIds.contains(id)) {
                    orderBean.removeId(id);
                }
            }
        }
        saveOrder(key, orderBean);
        return (orderBean);
    }

    private boolean saveOrder(int key, ObjectOrderBean orderBean, ObjectDataOptionsEnum... options) throws PersistenceException {
        try {
            if(orderBean.getIdCount() != 0) {
                String xmlString = this.encrypt(orderBean.toXML(storeType));
                return ARCHIVE.isIn(options) ? requestHandler.setOrderArchive(key, xmlString) : requestHandler.setOrder(key, xmlString);
            }
            // as it is empty delete it
            return ARCHIVE.isIn(options) ? requestHandler.removeOrderArchive(key) : requestHandler.removeOrder(key);
        } finally {
            //exit handler
            requestHandler.exit();
        }
    }

    /*
     * ******************************************************
     * P R I V A T E    M E T H O D S    F R O M    M E M O R Y
     * ****************************************************
     */
    /**
     * sets an object to memory checking and modifying if exists
     */
    private T saveObjectToMemory(int key, T ob) throws PersistenceException {

        objectMap.putIfAbsent(key, new ConcurrentSkipListMap<Integer, T>());
        if(ob == null) {
            return (null);
        }
        if(logger.isTraceEnabled()) {
            logger.trace("MEMORY - setObject(" + key + ", " + ob.getClass().getSimpleName() + ")");
        }
        if(objectMap.get(key).containsKey(ob.getIdentifier())) {
            T oldObject = objectMap.get(key).get(ob.getIdentifier());
            objectMap.get(key).remove(ob.getIdentifier());
            ob.setModified();
            ob.setCreated(oldObject.getCreated());
        }
        ob.setGroupKey(key);
        objectMap.get(key).put(ob.getIdentifier(), ob);
        return (ob);
    }

    /*
     * **************************************************
     * P R I V A T E    M E T H O D S    F R O M    D I S K
     * ************************************************
     */

    private boolean setArchiveYwd(int key, int identifier, int archiveYwd) throws PersistenceException {
        // check the object is in archive
        try {
            if(requestHandler.getObjectIdsArchive(key).contains(identifier)) {
                String xmlString = requestHandler.getObjectArchive(key, identifier);
                T ob = buildObjectBean(xmlString, key, identifier);
                ob.setArchiveYwd(archiveYwd);
                xmlString = this.encrypt(ob.toXML(storeType));
                return requestHandler.setObjectArchive(key, identifier, xmlString);
            }
            return false;
        } finally {
            //exit handler
            requestHandler.exit();
        }
    }

    /**
     * gets an object from persistence referenced by key and identifier
     */
    private T getObjectFromDisk(int key, int identifier, ObjectDataOptionsEnum... options) throws PersistenceException {
        try {
            String xmlString = ARCHIVE.isIn(options) ? requestHandler.getObjectArchive(key, identifier) : requestHandler.getObject(key, identifier);
            return buildObjectBean(xmlString, key, identifier);
        } finally {
            // exit handler
            requestHandler.exit();
        }
    }

    /**
     * gets all objects from persistence referenced by key
     */
    private LinkedList<T> getAllObjectForKeyFromDisk(int key, ObjectDataOptionsEnum... options) throws PersistenceException {
        LinkedList<T> rtnList = new LinkedList<>();
        try {
            for(int id : ARCHIVE.isIn(options) ? requestHandler.getObjectIdsArchive(key) : requestHandler.getObjectIds(key)) {
                rtnList.add(getObjectFromDisk(key, id, options));
            }
        } finally {
            // exit handler
            requestHandler.exit();
        }
        return (rtnList);
    }

    /**
     * get the XML out of a file being held on disk
     */
    @SuppressWarnings("AssignmentToMethodParameter")
    private synchronized T buildObjectBean(String xmlString, int key, int identifier) throws PersistenceException {
        if(xmlString == null || xmlString.isEmpty()) {
            return null;
        }
        if(CIPHERED.isIn(storeType)) {
            xmlString = SimpleStringCipher.decrypt(xmlString);
        }
        // initialise the xml builder
        final SAXBuilder builder = new SAXBuilder();
        builder.setXMLReaderFactory(XMLReaders.NONVALIDATING);
        Document doc = null;
        Object newInstance = null;
        try {
            doc = builder.build(new StringReader(xmlString));
            Class<?> myClass = Class.forName(doc.getRootElement().getAttributeValue("class"));
            newInstance = myClass.newInstance();
        } catch(JDOMException jde) {
            throw new PersistenceException("Unable to build ObjectBean from XML string: " + jde.getMessage());
        } catch(IOException ioe) {
            throw new PersistenceException("IO error when building ObjectBean from XML string: " + ioe.getMessage());
        } catch(ClassNotFoundException cnfe) {
            throw new PersistenceException("Class not found : " + cnfe.getMessage());
        } catch(InstantiationException ie) {
            throw new PersistenceException("Instanciation error : " + ie.getMessage());
        } catch(IllegalAccessException iae) {
            throw new PersistenceException("Illigal access error : " + iae.getMessage());
        } catch(IllegalStateException ise) {
            throw new PersistenceException("Illigal state error : " + ise.getMessage() + ": xml = " + xmlString);
        } catch(NullPointerException npe) {
            throw new PersistenceException("Reading the XML String produced a NullPointerException");
        }
        if(newInstance != null && doc != null) {
            @SuppressWarnings("unchecked")
            final T objectBean = (T) newInstance;
            objectBean.setIdentifier(identifier);
            objectBean.setGroupKey(key);
            objectBean.setXMLDOM(doc.getRootElement());
            // this is to ensure the file and the identifiers match
            objectBean.setIdentifier(identifier);
            objectBean.setGroupKey(key);
            return (objectBean);
        }
        return (null);
    }

    /*
     * manages the timeReservedExcludesMap, removing any out of date values
     * and returning the remaining time excluded identifiers
     */
    private ConcurrentSkipListSet<Integer> getTimeReservedExclusions() {
        long timestamp = System.currentTimeMillis();
        // remove all the out of date values
        for(int id : timeReservedExcludesMap.keySet()) {
            if(timeReservedExcludesMap.get(id) < timestamp) {
                timeReservedExcludesMap.remove(id);
                continue;
            }
        }
        // return all the values that are left
        return new ConcurrentSkipListSet<>(timeReservedExcludesMap.keySet());
    }

    /*
     * add a time excluded identifier to the timeReservedExcludesMap
     */
    private int setTimeReservedExclusion(int identifier, int reserveTime) {
        // check there is a time delay worth storing
        if(reserveTime > 0) {
            // add the identifier and expiry time to the excludes
            timeReservedExcludesMap.put(identifier, System.currentTimeMillis() + reserveTime);
        }
        return identifier;
    }

    /*
     * takes the xmlString and checks if ciphering is on, if so it then encrypts
     */
    private String encrypt(String stringData) throws PersistenceException {
        if(CIPHERED.isIn(storeType)) {
            return SimpleStringCipher.encrypt(stringData);
        }
        return stringData;
    }
}
