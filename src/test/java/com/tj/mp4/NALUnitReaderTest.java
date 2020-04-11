package com.tj.mp4;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class NALUnitReaderTest {

    @Test
    public void test() throws IOException {
        MediaInfo media = MP4Reader.generateMediaInfo("E:\\Documents\\My Videos\\Brooklyn nine-nine\\Futurama Movie-1.m4v");
        TrackInfo track = media.getTracks().get(0);
        SampleReader reader = new SampleReader(track);
        NALUnitReader nalReader = new NALUnitReader(track.getSampleReader());
        NALUnit nalu = nalReader.next();
        Assert.assertEquals(0x000002F4, nalu.getSize());
    }

}
