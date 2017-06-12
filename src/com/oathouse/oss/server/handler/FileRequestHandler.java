/*
 * @(#)FileRequestHandler.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.handler;

import com.oathouse.oss.storage.exceptions.NoSuchIdentifierException;
import com.oathouse.oss.storage.exceptions.NoSuchKeyException;
import com.oathouse.oss.storage.exceptions.PersistenceException;
import com.oathouse.oss.storage.objectstore.ObjectDataOptionsEnum;
import static com.oathouse.oss.storage.objectstore.ObjectDataOptionsEnum.*;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * The {@code FileRequestHandler} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 28-Apr-2011
 */
public class FileRequestHandler implements ObjectRequestHandlerInterface {

    private static final String sep = File.separator;
    private static Logger logger = Logger.getLogger(FileRequestHandler.class);
    private final File storePath;
    private volatile String authority;
    private volatile String manager;
    private final String keyPrefix;
    private final String fileSuffix;

    public FileRequestHandler(String rootStorePath, String authority, String manager) {
        this.authority = authority;
        this.manager = manager;
        this.storePath = new File(rootStorePath, "files");
        this.keyPrefix = "key_";
        this.fileSuffix = ".xml";
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public static Set<String> getAllAuthorities(String rootStorePath) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - getAuthorities()");
        }
        final Set<String> rtnSet = list(new File(rootStorePath, "files"), "", "");
        if(logger.isTraceEnabled()) {
            logger.trace("FILE return - authorities" + rtnSet.toString());
        }
        return rtnSet;
    }

    public static boolean clearAuthority(String rootStorePath, String authority) {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - clearAuthority(" + authority + ")");
        }
        File storePath = new File(rootStorePath, "files");
        File authPath = new File(storePath, authority);
        try {
            if(authPath.exists()) {
                return FileDeleteStrategy.FORCE.deleteQuietly(authPath);
            }
        } catch(IllegalArgumentException iae) {
            // ignore as the file isn't there.
        }
        return (true);
    }

    public static Set<String> getAllManagers(String rootStorePath, String authority) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - getManagers()");
        }
        File storePath = new File(rootStorePath, "files");
        File authPath = new File(storePath, authority);
        final Set<String> rtnSet = list(authPath, "", "");
        if(logger.isTraceEnabled()) {
            logger.trace("FILE return - managers" + rtnSet.toString());
        }
        return rtnSet;
    }

    @Override
    public boolean isAlive() throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - isAlive");
        }
        return true;
    }

    @Override
    public String getObject(int key, int identifier) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - getObject(" + key + ", " + identifier + ")");
        }
        return getObject(identifier, relativePath(key));
    }

    @Override
    public String getObjectArchive(int key, int identifier) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - getArchiveObject(" + key + ", " + identifier + ")");
        }
        return getObject(identifier, relativePath(key, ARCHIVE));
    }

    private String getObject(int identifier, String relativePath) throws PersistenceException {
        final File path = new File(storePath, relativePath);
        final File file = new File(path, Integer.toString(identifier) + fileSuffix);
        try {
            return get(path, file);
        } catch(NoSuchIdentifierException | NoSuchKeyException ex) {
            // ignore
        }
        return null;
    }

    @Override
    public String getOrder(int key) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - getOrder(" + key + ")");
        }
        return getOrder(key, relativeOrderPath());
    }

    @Override
    public String getOrderArchive(int key) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - getOrder(" + key + ")");
        }
        return getOrder(key, relativeOrderPath(ARCHIVE));
    }

    private String getOrder(int key, String relativeOrderPath) throws PersistenceException {
        final File path = new File(storePath, relativeOrderPath);
        final File file = new File(path, Integer.toString(key) + fileSuffix);
        try {
            return get(path, file);
        } catch(NoSuchIdentifierException | NoSuchKeyException ex) {
            // ignore
        }
        return null;
    }

    @Override
    public Set<Integer> getKeys() throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - getKeys()");
        }
        return getKeys(authority + sep + manager);
    }

    @Override
    public Set<Integer> getKeysArchive() throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - getArchiveKeys()");
        }
        return getKeys(authority + sep + manager + sep + "archive");
    }

    private Set<Integer> getKeys(String relativePath) throws PersistenceException {
        final ConcurrentSkipListSet<Integer> rtnSet = new ConcurrentSkipListSet<>();
        final File path = new File(storePath, relativePath);
        for(String name : list(path, keyPrefix, "")) {
            rtnSet.add(Integer.valueOf(name));
        }
        if(logger.isTraceEnabled()) {
            logger.trace("FILE return - keys" + rtnSet.toString());
        }
        return rtnSet;
    }

    @Override
    public Set<Integer> getObjectIds(int key) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - getObjectIds(" + key + ")");
        }
        return getObjectIds(relativePath(key));
    }

    @Override
    public Set<Integer> getObjectIdsArchive(int key) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - getArchiveObjectIds(" + key + ")");
        }
        return getObjectIds(relativePath(key, ARCHIVE));
    }

    private Set<Integer> getObjectIds(String relativePath) throws PersistenceException {
        final ConcurrentSkipListSet<Integer> rtnSet = new ConcurrentSkipListSet<>();
        final File path = new File(storePath, relativePath);
        for(String name : list(path, "", fileSuffix)) {
            rtnSet.add(Integer.valueOf(name));
        }
        if(logger.isTraceEnabled()) {
            logger.trace("FILE return - ids" + rtnSet.toString());
        }
        return rtnSet;
    }

    @Override
    public Set<Integer> getOrderIds() throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - getOrderIds()");
        }
        return getOrderIds(relativeOrderPath());
    }

    @Override
    public Set<Integer> getOrderIdsArchive() throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - getArchiveOrderIds()");
        }
        return getOrderIds(relativeOrderPath(ARCHIVE));
    }

    private Set<Integer> getOrderIds(String relativeOrderPath) throws PersistenceException {
        final ConcurrentSkipListSet<Integer> rtnSet = new ConcurrentSkipListSet<>();
        final File path = new File(storePath, relativeOrderPath);
        for(String name : list(path, "", fileSuffix)) {
            rtnSet.add(Integer.valueOf(name));
        }
        if(logger.isTraceEnabled()) {
            logger.trace("FILE return - orders" + rtnSet.toString());
        }
        return rtnSet;
    }

    @Override
    public boolean setObject(int key, int identifier, String data) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - setObject(" + key + ", " + identifier + ", " + data.substring(0, 10) + "...)");
        }
        return setObject(identifier, data, relativePath(key));
    }

    @Override
    public boolean setObjectArchive(int key, int identifier, String data) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - setArchiveObject(" + key + ", " + identifier + ", " + data.substring(0, 10) + "...)");
        }
        return setObject(identifier, data, relativePath(key, ARCHIVE));
    }

    private boolean setObject(int identifier, String data, String relativePath) throws PersistenceException {
        final File path = new File(storePath, relativePath);
        final File file = new File(path, Integer.toString(identifier) + fileSuffix);
        final boolean result = writeFile(path, file, data);
        if(logger.isTraceEnabled()) {
            String s = result ? "set succeeded" : "set failed";
            logger.trace("FILE return - " + s);
        }
        return (result);
    }

    @Override
    public boolean setOrder(int key, String data) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - setOrder(" + key + ", " + data.substring(0, 10) + "...)");
        }
        return setOrder(key, data, relativeOrderPath());
    }

    @Override
    public boolean setOrderArchive(int key, String data) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - setArchiveOrder(" + key + ", " + data.substring(0, 10) + "...)");
        }
        return setOrder(key, data, relativeOrderPath(ARCHIVE));
    }

    private boolean setOrder(int key, String data, String relativeOrderPath) throws PersistenceException {
        final File path = new File(storePath, relativeOrderPath);
        final File file = new File(path, Integer.toString(key) + fileSuffix);
        final boolean result = writeFile(path, file, data);
        if(logger.isTraceEnabled()) {
            String s = result ? "setOrder succeeded" : "setOrder failed";
            logger.trace("FILE return - " + s);
        }
        return (result);
    }

    @Override
    public boolean removeObject(int key, int identifier) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - removeObject(" + key + ", " + identifier + ")");
        }
        return removeObject(identifier, relativePath(key));
    }

    @Override
    public boolean removeObjectArchive(int key, int identifier) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - removeArchiveObject(" + key + ", " + identifier + ")");
        }
        return removeObject(identifier, relativePath(key, ARCHIVE));
    }

    private boolean removeObject(int identifier, String relativePath) throws PersistenceException {
        final File path = new File(storePath, relativePath);
        final File file = new File(path, Integer.toString(identifier) + fileSuffix);
        final boolean result = remove(file, true);
        if(logger.isTraceEnabled()) {
            String s = result ? "remove succeeded" : "remove failed";
            logger.trace("FILE return - " + s);
        }
        // remove the path if nothing in it
        remove(path, false);
        return (result);
    }

    @Override
    public boolean removeOrder(int key) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - removeOrder(" + key + ")");
        }
        return removeOrder(key, relativeOrderPath());
    }

    @Override
    public boolean removeOrderArchive(int key) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - removeArchiveOrder(" + key + ")");
        }
        return removeOrder(key, relativeOrderPath(ARCHIVE));
    }

    private boolean removeOrder(int key, String relativeOrderPath) throws PersistenceException {
        final File path = new File(storePath, relativeOrderPath);
        final File file = new File(path, Integer.toString(key) + fileSuffix);
        final boolean result = remove(file, true);
        if(logger.isTraceEnabled()) {
            String s = result ? "remove succeeded" : "remove failed";
            logger.trace("FILE return - " + s);
        }
        // remove the path if nothing in it
        remove(path, false);
        return (result);
    }

    @Override
    public boolean removeAllObjects(int key) throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - removeAllObject(" + key + ")");
        }
        final File path = new File(storePath, relativePath(key));
        final boolean result = remove(path, true);
        if(logger.isTraceEnabled()) {
            String s = result ? "removeAllObjects succeeded" : "removeAllObjects failed";
            logger.trace("FILE return - " + s);
        }
        return (result);
    }

    /**
     * This is mostly used for testing as it removes all of an authority
     *
     * @return
     * @throws PersistenceException
     */
    @Override
    public boolean clear() throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - clear()");
        }
        final File path = new File(storePath, authority);
        // try deleting the authority
        final boolean result = remove(path, true);
        if(logger.isTraceEnabled()) {
            String s = result ? "clear succeeded" : "clear failed";
            logger.trace("FILE return - " + s);
        }
        return (result);
    }

    @Override
    public void exit() throws PersistenceException {
        if(logger.isTraceEnabled()) {
            logger.trace("FILE - exit()");
        }
    }

    /* *************************************************
     * SUB PRIVATE METHODS
     * *************************************************/
    private String relativePath(int key, ObjectDataOptionsEnum... args) {
        String archive = ObjectDataOptionsEnum.ARCHIVE.isIn(args) ? "archive" + sep : "";
        return authority + sep + manager + sep + archive + keyPrefix + Integer.toString(key);
    }

    private String relativeOrderPath(ObjectDataOptionsEnum... args) {
        String orderPath = ObjectDataOptionsEnum.ARCHIVE.isIn(args) ? "archive" + sep + "order" : "order";
        return authority + sep + manager + sep + orderPath;
    }

    private static synchronized Set<String> list(File path, String prefix, String suffix) throws PersistenceException {
        final ConcurrentSkipListSet<String> allNames = new ConcurrentSkipListSet<>();
        if(path.exists()) {
            String[] fileList = path.list();
            if(fileList != null) {
                for(int keyRef = 0; keyRef < fileList.length; keyRef++) {
                    String name = fileList[keyRef];
                    if(!prefix.isEmpty()) {
                        if(name.startsWith(prefix)) {
                            // key
                            allNames.add(name.substring(prefix.length()));
                        }
                    } else if(!suffix.isEmpty()) {
                        if(name.endsWith(suffix)) {
                            // id
                            allNames.add(name.substring(0, name.length() - suffix.length()));
                        }
                    } else {
                        // auth
                        allNames.add(name);
                    }
                }
            }
        }
        return (allNames);
    }

    private synchronized String get(File path, File file) throws PersistenceException, NoSuchIdentifierException, NoSuchKeyException {
        if(path.exists()) {
            if(file.exists()) {
                try {
                    return (FileUtils.readFileToString(file));
                } catch(IOException ioe) {
                    //logger.error("ObjectStore Error: Unable to read data from disk", ioe);
                    throw new PersistenceException("ObjectStore Error: Unable to read data from disk: " + ioe.getMessage());
                }
            }
            throw new NoSuchIdentifierException("Identifier not found");
        }
        throw new NoSuchKeyException("Key not found");
    }

    private boolean writeFile(File path, File file, String data) throws PersistenceException {
        if(data == null || data.isEmpty()) {
            return false;
        }
        try {
            write(path, file, data);
            return true;
        } catch(IOException | IllegalArgumentException e1) {
            try {
                Thread.sleep(2);
                write(path, file, data);
                return true;
            } catch(InterruptedException | IOException | IllegalArgumentException e2) {
                throw new PersistenceException("ObjectStore Error: Unable to write data to disk: " + e2.getMessage());
            }
        }
    }

    private synchronized void write(File path, File file, String data) throws IOException, IllegalArgumentException {
        FileUtils.forceMkdir(path);
        FileUtils.writeStringToFile(file, data);
    }

    private synchronized boolean remove(File path, boolean isForced) throws PersistenceException {
        try {
            if(isForced) {
                if(path.exists()) {
                    return FileDeleteStrategy.FORCE.deleteQuietly(path);
                }
            }
            return FileDeleteStrategy.NORMAL.deleteQuietly(path);
        } catch(IllegalArgumentException iae) {
            // ignore as the file isn't there.
            return (true);
        }
    }
}
