package com.tj.mp4;

import com.tj.mp4.SampleReader.Sample;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;

public class NALUnitReader implements Iterator<NALUnit> {
    private SampleNALUnitExtractor currentExtractor;
    private SampleReader sampleReader;
    private int nalIndex = 0;

    public NALUnitReader(TrackInfo track) {
        sampleReader = new SampleReader(track);
    }

    public NALUnitReader(SampleReader reader) {
        sampleReader = reader;
    }

    @Override
    public boolean hasNext() {
        return (currentExtractor != null && currentExtractor.hasNext()) || sampleReader.hasNext();
    }

    @Override
    public NALUnit next() {
        if (!hasNext()) {
            return null;
        }
        if (currentExtractor == null || !currentExtractor.hasNext()) {
            currentExtractor = new SampleNALUnitExtractor(sampleReader.next());
        }
        return currentExtractor.next();
    }

    private class SampleNALUnitExtractor implements Iterator<NALUnit> {
        private DataInputStream content;
        private Sample sample;

        public SampleNALUnitExtractor(Sample sample) {
            this.sample = sample;
            this.content = new DataInputStream(new ByteArrayInputStream(sample.getContent()));
        }

        @Override
        public boolean hasNext() {
            try {
                return content.available() > 0;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        public NALUnit next() {
            try {
                long nalSize = content.readInt();
                if (nalSize < 0) {
                    System.out.println(nalSize + " " + sample.getSampleNumber() + " " + sample.getSize());
                }
                byte[] content = new byte[(int) nalSize];
                this.content.read(content);
                return NALUnit.builder().number(nalIndex++)
                        .timestampOffset(sample.getTimestampOffset())
                        .size((int) nalSize)
                        .content(content)
                        .build();
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }
}
