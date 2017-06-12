/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * @(#)StringEncoder.java
 *
 * Copyright:	Copyright (c) 2010
 * Company:		Oathouse.com Ltd
 */
package com.oathouse.oss.server.transport.codec;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import org.apache.log4j.Logger;

/**
 * The {@code StringEncoder} Class
 *
 * @author Darryl Oatridge
 * @version 1.00 09-Jun-2011
 */
public class StringEncoder {

    private static Logger LOGGER = Logger.getLogger(StringEncoder.class);

    public static ByteBuffer encode(String s) {
        Charset charset = Charset.forName("UTF-8");
        CharsetEncoder encoder = charset.newEncoder();
        encoder.onMalformedInput(CodingErrorAction.REPORT);
        encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        encoder.reset();

        ByteBuffer bbuff = null;
        try {
            bbuff = encoder.encode(CharBuffer.wrap(s));
        } catch(CharacterCodingException cce) {
            LOGGER.error("Encoding error: ", cce);
            return(null);
        }
        encoder.flush(bbuff);
        bbuff.rewind();
        return bbuff;
    }

    public static String decode(ByteBuffer in) {
        Charset charset = Charset.forName("UTF-8");
        CharsetDecoder decoder = charset.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPORT);
        decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);

        StringBuilder sb = new StringBuilder();
        decoder.reset();
        CharBuffer out = CharBuffer.allocate(1024);
        while(in.hasRemaining()) {
            decoder.decode(in, out, false);
            out.flip();
            sb.append(out);
            out.clear();
        }
        decoder.decode(in, out, true);
        decoder.flush(out);
        out.flip();
        sb.append(out);
        out.clear();
        return (sb.toString());
    }

    private StringEncoder() {
    }
}
