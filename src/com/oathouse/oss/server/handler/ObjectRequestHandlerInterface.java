/**
 * @(#)ObjectBean.java
 *
 * Copyright:	Copyright (c) 2011
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.handler;

import com.oathouse.oss.storage.exceptions.PersistenceException;
import java.util.Set;

/**
 * This interface is used for multiple persistence mechanisms
 *
 * @author Darryl Oatridge
 */
public interface ObjectRequestHandlerInterface {

    public boolean isAlive() throws PersistenceException;

    public String getManager();

    public String getAuthority();

    public String getObject(int key, int identifier) throws PersistenceException;

    public String getObjectArchive(int key, int identifier) throws PersistenceException;

    public String getOrder(int key) throws PersistenceException;

    public String getOrderArchive(int key) throws PersistenceException;

    public Set<Integer> getKeys() throws PersistenceException;

    public Set<Integer> getKeysArchive() throws PersistenceException;

    public Set<Integer> getObjectIds(int key) throws PersistenceException;

    public Set<Integer> getObjectIdsArchive(int key) throws PersistenceException;

    public Set<Integer> getOrderIds() throws PersistenceException;

    public Set<Integer> getOrderIdsArchive() throws PersistenceException;

    public boolean setObject(int key, int identifier, String data) throws PersistenceException;

    public boolean setObjectArchive(int key, int identifier, String data) throws PersistenceException;

    public boolean setOrder(int key, String data) throws PersistenceException;

    public boolean setOrderArchive(int key, String data) throws PersistenceException;

    public boolean removeObject(int key, int identifier) throws PersistenceException;

    public boolean removeObjectArchive(int key, int identifier) throws PersistenceException;

    public boolean removeOrder(int key) throws PersistenceException;

    public boolean removeOrderArchive(int key) throws PersistenceException;

    public boolean removeAllObjects(int key) throws PersistenceException;

    public boolean clear() throws PersistenceException;

    public void exit() throws PersistenceException;
}
