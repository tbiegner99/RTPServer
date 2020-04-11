package com.tj.mp4;

import com.tj.mp4.SampleReader.Sample;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SampleReaderTest {

    @Test
    public void test() throws IOException {
        MediaInfo media = MP4Reader.generateMediaInfo("E:\\Documents\\My Videos\\Brooklyn nine-nine\\Futurama Movie-1.m4v");
        TrackInfo track = media.getTracks().get(0);
        SampleReader reader = track.getSampleReader();
        int i = 0;
        while (reader.hasNext()) {
            Sample sample = reader.next();
            //System.out.println("Sample: " + i + 1);
            //System.out.println("Timestamp: " + sample.getTimestampOffset());
            //System.out.println("Size: " + sample.getSize());
            assertEquals(i + 1, sample.getSampleNumber());
            long expectPosition = track.getChunkOffsets().get(i).longValue();
            long actualPosition = sample.getFilePosition();
            assertEquals(
                    "For i=" + i + " position is wrong: Expected " + expectPosition + " and got " + actualPosition,
                    expectPosition, actualPosition);
            i++;
        }
        assertEquals(127797, i);
    }

}
