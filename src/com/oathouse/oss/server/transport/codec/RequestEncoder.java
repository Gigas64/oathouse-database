/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * @(#)RequestEncoder.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.transport.codec;

import com.oathouse.oss.server.transport.Command;
import com.oathouse.oss.server.transport.Request;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * The {@code RequestEncoder} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 27-Apr-2011
 */
public class RequestEncoder {

    public static Request decode(ByteBuffer in) {
        try {
            String auth = null;
            String manager = null;
            String data = null;
            Command cmd = Command.getCommand(in.get());
            int key = in.getInt();
            int id = in.getInt();
            int lAuth = in.getShort();
            if(lAuth > 0) {
                byte[] bAuth = new byte[lAuth];
                in.get(bAuth);
                auth = StringEncoder.decode(ByteBuffer.wrap(bAuth));
            }
            int lMan = in.getShort();
            if(lMan > 0) {
                byte[] bMan = new byte[lMan];
                in.get(bMan);
                manager = StringEncoder.decode(ByteBuffer.wrap(bMan));
            }
            int lData = in.getShort();
            if(lData > 0) {
                byte[] bData = new byte[lData];
                in.get(bData);
                data = StringEncoder.decode(ByteBuffer.wrap(bData));
            }
            return new Request(cmd, auth, manager, key, id, data);
        } catch(BufferUnderflowException bue) {
            return (null);
        }
    }

    public static ByteBuffer encode(Request request) {
        ByteBuffer aBuff = null, mBuff = null, dBuff = null;
        short aLen = 0, mLen = 0, dLen = 0;

        if(request.getAuthority().length() > 0) {
            aBuff = StringEncoder.encode(request.getAuthority());
            aLen = (short) aBuff.limit();
        }
        if(request.getManager().length() > 0) {
            mBuff = StringEncoder.encode(request.getManager());
            mLen = (short) mBuff.limit();
        }
        if(request.getData().length() > 0) {
            dBuff = StringEncoder.encode(request.getData());
            dLen = (short) dBuff.limit();
        }
        // added in order just for clarity
        int capacity = 2 + 1 + 4 + 4 + 2 + aLen + 2 + mLen + 2 + dLen;

        ByteBuffer rtnBuff = ByteBuffer.allocateDirect(capacity);
        rtnBuff.putShort((short) (capacity - 2));
        rtnBuff.put(request.getCmd().getByteValue());
        rtnBuff.putInt(request.getKey());
        rtnBuff.putInt(request.getId());
        rtnBuff.putShort(aLen);
        if(aBuff != null) {
            rtnBuff.put(aBuff);
        }
        rtnBuff.putShort(mLen);
        if(mBuff != null) {
            rtnBuff.put(mBuff);
        }
        rtnBuff.putShort(dLen);
        if(dBuff != null) {
            rtnBuff.put(dBuff);
        }
        rtnBuff.flip();
        return rtnBuff;
    }

    private RequestEncoder() {
    }
}
