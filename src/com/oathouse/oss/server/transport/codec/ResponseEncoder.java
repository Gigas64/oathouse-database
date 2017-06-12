/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * @(#)ResponseEncoder.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.transport.codec;

import com.oathouse.oss.server.transport.Response;
import com.oathouse.oss.server.transport.Status;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * The {@code ResponseEncoder} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 27-Apr-2011
 */
public class ResponseEncoder {

    public static Response decode(ByteBuffer in) {
        Set<Integer> values = new ConcurrentSkipListSet<Integer>();
        String data = null;
        Status status = Status.getStatus(in.get());
        int lValues = in.getShort();
        for(int i = 0; i < lValues; i++) {
            values.add(in.getInt());
        }
        int lData = in.getShort();
        if(lData > 0) {
            byte[] bData = new byte[lData];
            in.get(bData);
            data = StringEncoder.decode(ByteBuffer.wrap(bData));
        }
        return new Response(status, values, data);
    }

    public static ByteBuffer encode(Response response) {
        ByteBuffer dBuff = null;
        short dLen = 0;
        short vLen = (short) response.getValues().size();
        if(response.getData().length() > 0) {
            dBuff = StringEncoder.encode(response.getData());
            dLen = (short) dBuff.limit();
        }
        // added in order just for clarity
        int capacity = 2 + 1 + 2 + (vLen * 4) + 2 + dLen;

        ByteBuffer rtnBuff = ByteBuffer.allocateDirect(capacity);
        rtnBuff.putShort((short) (capacity - 2));
        rtnBuff.put(response.getStatus().getByteValue());
        rtnBuff.putShort(vLen);
        for(int value : response.getValues()) {
            rtnBuff.putInt(value);
        }
        rtnBuff.putShort(dLen);
        if(dBuff != null) {
            rtnBuff.put(dBuff);
        }
        rtnBuff.flip();
        return rtnBuff;
    }

    private ResponseEncoder() {
    }
}
