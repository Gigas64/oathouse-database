/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * @(#)ServerPropertiesBean.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server;

import com.oathouse.oss.storage.objectstore.ObjectBean;
import java.io.File;

/**
 * The {@code ServerPropertiesBean} Class used to store global properties required by the server.
 * This is a singleton class.
 *
 * @author Darryl Oatridge
 * @version 1.00 21-Apr-2011
 */
public class OssProperties {
    // Singleton Instance
    private volatile static OssProperties properties;

    private volatile String host = "localhost";
    private volatile int port = 21021;
    private volatile int portRange = 1;
    private volatile String storePath = "." + File.separator + "oss";
    private volatile String authority = ObjectBean.SYSTEM_OWNED;
    private volatile Connection connection = Connection.FILE;
    private volatile String logConfigFile = null;
    private volatile String cipherKey = "6yHn48"; // cipher key length must be at least 6

    public static enum Connection {
        FILE,NIO;
    }

    /*
     * Singleton so constructor is private
     */
    private OssProperties() {
    }

    public static OssProperties getInstance() {
        if(properties == null) {
            synchronized (OssProperties.class) {
                // Check again just incase before we synchronised an instance was created
                if(properties == null) {
                    properties = new OssProperties();
                }
            }
        }
        return properties;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        if(port < 1024) {
            //return a default
            return(21021);
        }
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPortRange() {
        if(portRange < 1) {
            // return a default
            return(1);
        }
        return portRange;
    }

    public void setPortRange(int portRange) {
        this.portRange = portRange;
    }

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        if(authority.length() < 1) {
            return;
        }
        this.authority = authority;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getLogConfigFile() {
        return logConfigFile;
    }

    public void setLogConfigFile(String logConfigFile) {
        this.logConfigFile = logConfigFile;
    }

    public String getCipherKey() {
        return cipherKey;
    }

    /**
     * The cypher key must be 6 characters or larger
     * @param cipherKey
     */
    public void setCipherKey(String cipherKey) {
        if(cipherKey.length() < 6) {
            return;
        }
        this.cipherKey = cipherKey;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final OssProperties other = (OssProperties) obj;
        if((this.host == null) ? (other.host != null) : !this.host.equals(other.host)) {
            return false;
        }
        if(this.port != other.port) {
            return false;
        }
        if(this.portRange != other.portRange) {
            return false;
        }
        if((this.storePath == null) ? (other.storePath != null) : !this.storePath.equals(other.storePath)) {
            return false;
        }
        if((this.authority == null) ? (other.authority != null) : !this.authority.equals(other.authority)) {
            return false;
        }
        if(this.connection != other.connection) {
            return false;
        }
        if((this.logConfigFile == null) ? (other.logConfigFile != null) : !this.logConfigFile.equals(other.logConfigFile)) {
            return false;
        }
        if((this.cipherKey == null) ? (other.cipherKey != null) : !this.cipherKey.equals(other.cipherKey)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.host != null ? this.host.hashCode() : 0);
        hash = 89 * hash + this.port;
        hash = 89 * hash + this.portRange;
        hash = 89 * hash + (this.storePath != null ? this.storePath.hashCode() : 0);
        hash = 89 * hash + (this.authority != null ? this.authority.hashCode() : 0);
        hash = 89 * hash + (this.connection != null ? this.connection.hashCode() : 0);
        hash = 89 * hash + (this.logConfigFile != null ? this.logConfigFile.hashCode() : 0);
        hash = 89 * hash + (this.cipherKey != null ? this.cipherKey.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "OssProperties ->" +
                "\n  connection    = " + connection +
                "\n  storePath     = " + storePath +
                "\n  authority     = " + authority +
                "\n  host          = " + host +
                "\n  port          = " + port +
                "\n  portRange     = " + portRange +
                "\n  logConfigFile = " + logConfigFile;
    }

}
