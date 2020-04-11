package com.tj.mp4;

import com.tj.mp4.boxes.AVCConfigBox;
import com.tj.mp4.boxes.Box;
import com.tj.mp4.boxes.Box.Type;
import com.tj.mp4.boxes.FTYPBox;
import com.tj.mp4.boxes.MoovBox;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MP4ReaderTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testHasNext() {
        fail("Not yet implemented");
    }

    @Test
    public void testNext() throws IOException {
        MP4Reader reader = new MP4Reader("E:\\Documents\\My Videos\\Brooklyn nine-nine\\Futurama Movie-1.m4v");
        Box box = reader.next();
        assertEquals(box.getBoxSize(), 0x20);
        assertEquals(box.getBoxCode(), Type.FTYP);
        assertEquals(((FTYPBox) box).getCompatibleBrands().length, 4);

        box = reader.next();
        assertEquals(box.getBoxSize(), 8);
        assertEquals(box.getBoxCode(), Type.FREE);

        box = reader.next();
        assertEquals(box.getStartPosition(), 0x28);
        assertEquals(box.getBoxSize(), 0x202a175a);
        assertEquals(box.getBoxCode(), Type.MDAT);

        box = reader.next();
        assertEquals(box.getBoxCode(), Type.MOOV);
        assertEquals(box.getBoxSize(), 0x004c7dbf);
        assertEquals(((MoovBox) box).getChildrenSize(), 6);

        assertEquals(reader.hasNext(), false);
        assertEquals(reader.getMediaInfo().getTracks().size(), 3);

        assertEquals(reader.getMediaInfo().getTracks().get(0).getDecoderConfiguration().getClass(), AVCConfigBox.class);
        AVCConfigBox config = (AVCConfigBox) reader.getMediaInfo().getTracks().get(0).getDecoderConfiguration();
        assertEquals("674040", config.getProfileLevelId());
    }

}
