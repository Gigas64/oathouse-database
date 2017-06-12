/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.server.transport.codec;

import com.oathouse.oss.server.transport.codec.ResponseEncoder;
import com.oathouse.oss.server.transport.Response;
import com.oathouse.oss.server.transport.Status;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentSkipListSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Darryl Oatridge
 */
public class ResponseEncoderTest {
    @Before
    public void setUp() {
    }

    /**
     * Test of encode method, of class RequestEncoder.
     */
    @Test
    public void testEncode() {
        StringBuilder sb = new StringBuilder();
        sb.append("var dds = Y.DD.DDM._drags;").append("\n").append("var list = {};").append("\n").append("var lis = Y.all('#' + k + ' li.item');").append("\n").append("var xml = \"<modules page='${page}'>\";").append("\n").append("Y.each(dds, function(v, k) {").append("\n").append("  var par = v.get('node').get('parentNode');").append("\n").append("  if(par.test('ul.list')) {").append("\n").append("    if(!list[par.get('id')]) {").append("\n").append("      list[par.get('id')] = [];").append("\n").append("      var minned = (mod.hasClass('minned')) ? 0:1;").append("\n").append("    }").append("\n").append("  }").append("\n").append("  !\"£$%^&*()+}{[]\\~#@':;?/><|").append("\n").append("  xml += \"<module id='\" + moduleId + \"' col='\" + k.substring(4) + \"'/>\";").append("\n").append("});").append("\n");

        ConcurrentSkipListSet<Integer> values = new ConcurrentSkipListSet<Integer>();
        values.add(3);
        values.add(5);
        values.add(7);
        Response resp1 = new Response(Status.FAILURE);
        Response resp2 = new Response(Status.SUCCESS, sb.toString());
        Response resp3 = new Response(Status.SUCCESS, values);
        Response resp4 = new Response(Status.SUCCESS, values, sb.toString());


        ByteBuffer bReq1 = ResponseEncoder.encode(resp1);
        bReq1.getShort();
        assertEquals(resp1,ResponseEncoder.decode(bReq1));
        ByteBuffer bReq2 = ResponseEncoder.encode(resp2);
        bReq2.getShort();
        assertEquals(resp2,ResponseEncoder.decode(bReq2));
        ByteBuffer bReq3 = ResponseEncoder.encode(resp3);
        bReq3.getShort();
        assertEquals(resp3,ResponseEncoder.decode(bReq3));
        ByteBuffer bReq4 = ResponseEncoder.encode(resp4);
        bReq4.getShort();
        assertEquals(resp4,ResponseEncoder.decode(bReq4));
    }
}
