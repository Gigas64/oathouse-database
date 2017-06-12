/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oathouse.oss.server.transport.codec;

import com.oathouse.oss.server.transport.codec.RequestEncoder;
import java.nio.ByteBuffer;
import com.oathouse.oss.server.transport.Command;
import com.oathouse.oss.server.transport.Request;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Darryl Oatridge
 */
public class RequestEncoderTest {
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

        Request req1 = new Request(Command.SET, "auth1", "man1", 11, 21, sb.toString());
        Request req2 = new Request(Command.GET, "auth2", "man2", 12, 22);
        Request req3 = new Request(Command.SET_ORDER, "auth3", "man3", 13, sb.toString());
        Request req4 = new Request(Command.GET_IDS, "auth4", "man4", 14);
        Request req5 = new Request(Command.GET_KEYS, "auth5", "man5");

        ByteBuffer bReq = RequestEncoder.encode(req1);
        bReq.getShort();
        Request rtn = RequestEncoder.decode(bReq);


        ByteBuffer bReq1 = RequestEncoder.encode(req1);
        bReq1.getShort();
        assertEquals(req1,RequestEncoder.decode(bReq1));
        ByteBuffer bReq2 = RequestEncoder.encode(req2);
        bReq2.getShort();
        assertEquals(req2,RequestEncoder.decode(bReq2));
        ByteBuffer bReq3 = RequestEncoder.encode(req3);
        bReq3.getShort();
        assertEquals(req3,RequestEncoder.decode(bReq3));
        ByteBuffer bReq4 = RequestEncoder.encode(req4);
        bReq4.getShort();
        assertEquals(req4,RequestEncoder.decode(bReq4));
        ByteBuffer bReq5 = RequestEncoder.encode(req5);
        bReq5.getShort();
        assertEquals(req5,RequestEncoder.decode(bReq5));
    }
}
