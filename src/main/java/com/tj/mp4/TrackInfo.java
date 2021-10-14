package com.tj.mp4;

import com.tj.mp4.CompositionTimeToSampleBox.CompositionOffset;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;
import com.tj.mp4.boxes.Box.DecoderConfigurationBox;
import com.tj.mp4.boxes.HandlerBox.HandlerType;
import com.tj.mp4.boxes.SampleToChunkBox.SampleToChunk;
import com.tj.mp4.boxes.TimeToSampleBox.TimeToSample;
import com.tj.mp4.descriptor.ESDescriptor;

import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TrackInfo {
    public static enum Type {
        AUDIO,
        VIDEO,
        OTHER,
    }

    private HandlerType handlerType;
    private MediaType mediaType;
    private DecoderConfigurationBox decoderConfiguration;
    private Map<Long, SampleToChunk> samplesToChunk;
    private List<Long> sampleSizes;
    private List<Long> chunkOffsets;
    private long timescale;
    private long duration;
    private String language;
    private List<TimeToSample> decodingTimes;
    private RandomAccessFile source;
    private SampleReader sampleReader;
    private ESDescriptor audioConfiguration;
    private List<CompositionOffset> compositionTimes;
    private MediaInfo mediaInfo;
    private int sampleRate;
    private long offset;
    private long lengthInSeconds;

    private TrackInfo() {

    }

    public Type getTrackType() {
        switch (handlerType) {
            case SOUN:
                return Type.AUDIO;
            case VIDE:
                return Type.VIDEO;
            default:
                return Type.OTHER;
        }
    }

    public long getOffset() {
        return offset;
    }

    public boolean isVideoTrack() {
        return getTrackType() == Type.VIDEO;
    }

    public boolean isAudioTrack() {
        return getTrackType() == Type.AUDIO;
    }


    public float getLengthInSeconds() {
        return (float) duration / timescale;
    }

    public long getLengthInMillis() {
        return (long) (getLengthInSeconds() * 1000);
    }

    public void resetSampleReader() {
        sampleReader = new SampleReader(this);
    }

    public void resetSampleReader(int timestamp, short seqNum) {
        sampleReader = new SampleReader(this, timestamp, seqNum);
    }

    public SampleReader getSampleReader() {
        if (sampleReader == null) {
            sampleReader = new SampleReader(this);
        }
        return sampleReader;
    }

    public static TrackInfoBuilder builder(MediaInfoBuilder mediaInfo) {
        return new TrackInfoBuilder(mediaInfo).source(mediaInfo.getSource());
    }

    public DecoderConfigurationBox getDecoderConfiguration() {
        return decoderConfiguration;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public HandlerType getHandlerType() {
        return handlerType;
    }

    public Map<Long, SampleToChunk> getSamplesToChunk() {
        return samplesToChunk;
    }

    public MediaInfo getMediaInfo() {
        return mediaInfo;
    }

    public long getTimescale() {
        return timescale;
    }

    public long getDuration() {
        return duration;
    }

    public String getLanguage() {
        return language;
    }

    public long getTrackLengthInSeconds() {
        return duration / timescale;
    }

    public List<Long> getSampleSizes() {
        return sampleSizes;
    }

    public List<Long> getChunkOffsets() {
        return chunkOffsets;
    }

    public List<TimeToSample> getDecodingTimes() {
        return decodingTimes;
    }

    public RandomAccessFile getSource() {
        return source;
    }

    public RandomAccessFile getSourceThreadSafe() {
        return mediaInfo.getSourceThreadSafe();
    }

    public void unlockSource() {
        mediaInfo.unlockSource();
    }

    public ESDescriptor getAudioConfiguration() {
        return audioConfiguration;
    }

    public List<CompositionOffset> getCompositionTimes() {
        return compositionTimes;
    }

    public CompositionTimeIterator getCompositionTimeIterator() {
        if (compositionTimes == null) {
            return null;
        }
        return new CompositionTimeIterator(compositionTimes.iterator());
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public static class CompositionTimeIterator implements Iterator<Long> {
        private int index = 0;
        private CompositionOffset currentSample;
        private Iterator<CompositionOffset> allSamples;

        private CompositionTimeIterator(Iterator<CompositionOffset> allSamples) {
            this.allSamples = allSamples;
        }

        @Override
        public boolean hasNext() {
            if (currentSample == null || index >= currentSample.getSampleCount()) {
                return allSamples.hasNext();
            }
            return true;
        }

        @Override
        public Long next() {
            if (currentSample == null || index >= currentSample.getSampleCount()) {
                currentSample = allSamples.next();
                index = 0;
            }
            index++;
            return currentSample.getSampleOffset();
        }

    }

    public static class TrackInfoBuilder {
        private TrackInfo item;
        private MediaInfoBuilder mediaInfo;

        public TrackInfoBuilder(MediaInfoBuilder mediaInfo) {
            this.item = new TrackInfo();
            this.mediaInfo = mediaInfo;
        }

        public TrackInfoBuilder source(RandomAccessFile source) {
            item.source = source;
            return this;
        }

        public MediaInfoBuilder done() {
            return this.mediaInfo;
        }

        //Really we need to do more than jst empty time at start.
        // edit lists are much more complex
        public TrackInfoBuilder addStartOffset(long offset) {
            this.item.offset = offset;
            return this;
        }

        public TrackInfo build() {
            this.item.mediaInfo = mediaInfo.build();
            return this.item;
        }

        public TrackInfoBuilder handlerType(HandlerType handlerType) {
            item.handlerType = handlerType;
            return this;
        }

        public TrackInfoBuilder trackMediaType(MediaType media) {
            item.mediaType = media;
            return this;
        }

        public TrackInfoBuilder decoderConfiguration(DecoderConfigurationBox configuration) {
            item.decoderConfiguration = configuration;
            return this;
        }

        public TrackInfoBuilder sampleToChunkInfo(Map<Long, SampleToChunk> offsets) {
            item.samplesToChunk = offsets;
            return this;
        }

        public TrackInfoBuilder timescale(long timescale) {
            item.timescale = timescale;
            return this;
        }

        public TrackInfoBuilder duration(long duration) {
            item.duration = duration;
            return this;

        }

        public TrackInfoBuilder language(String language) {
            item.language = language;
            return this;

        }

        public TrackInfoBuilder sampleSizes(List<Long> samples) {
            item.sampleSizes = samples;
            return this;

        }

        public TrackInfoBuilder chunkOffsets(List<Long> chunkOffset) {
            item.chunkOffsets = chunkOffset;
            return this;
        }

        public TrackInfoBuilder decodingTimes(List<TimeToSample> children) {
            item.decodingTimes = children;
            return this;

        }

        public TrackInfoBuilder compositionTimes(List<CompositionOffset> children) {
            item.compositionTimes = children;
            return this;

        }

        public TrackInfoBuilder audioConfiguration(ESDescriptor esDescriptor) {
            item.audioConfiguration = esDescriptor;
            return this;
        }

        public TrackInfoBuilder sampleRate(int rate) {
            item.sampleRate = rate;
            return this;
        }
    }

}
