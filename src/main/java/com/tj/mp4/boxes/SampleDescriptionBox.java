package com.tj.mp4.boxes;

import com.tj.mp4.BoxFactory;
import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;
import com.tj.mp4.MediaType;
import com.tj.mp4.StreamUtils;
import com.tj.mp4.boxes.HandlerBox.HandlerType;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SampleDescriptionBox extends FullBox implements Container {

    private List<Box> children;

    public SampleDescriptionBox(MP4Reader reader, long position, long size) throws IOException {
        super(reader, position, size, Type.STSD);
    }

    @Override
    public List<Box> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    @Override
    public int getChildrenSize() {
        return children.size();
    }

    @Override
    protected void readFullBox(RandomAccessFile stream) throws IOException {
        HandlerType handler = this.getContext().currentTrack().build().getHandlerType();
        int entryCount = stream.readInt();
        this.children = new ArrayList<Box>();
        for (int i = 0; i < entryCount; i++) {
            long position = stream.getFilePointer();
            int size = stream.readInt();
            String boxType = StreamUtils.readWord(stream, 4);
            Box box = null;
            switch (handler) {
                case HINT:
                    box = new HintEntryBox(this.getReader(), position, size, boxType);
                    break;
                case SOUN:
                    box = new AudioEntryBox(this.getReader(), position, size, boxType);
                    break;
                case VIDE:
                    box = new VideoEntryBox(this.getReader(), position, size, boxType);
                    break;
                case DERIVED:
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Handler Type:" + handler.name());
            }
            if (box != null) {
                this.children.add(box);
                box.updateContext(this.getReader().getContext());
            }
            stream.seek(position + size);
        }

    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        // TODO Auto-generated method stub

    }

    public static abstract class EntryBox extends Box {

        private short dataReferenceIndex;
        private String codecName;

        public EntryBox(MP4Reader reader, long position, long size, String codecName) throws IOException {
            super(reader, position, size, Type._CODEC);
            this.codecName = codecName;
        }

        @Override
        protected void readBox(RandomAccessFile stream) throws IOException {
            stream.skipBytes(6);//reserved
            this.dataReferenceIndex = stream.readShort();
            this.readEntry(stream);

        }

        public short getDataReferenceIndex() {
            return dataReferenceIndex;
        }

        public String getCodecName() {
            return codecName;
        }

        protected abstract void readEntry(RandomAccessFile stream) throws IOException;

    }

    public static class VideoEntryBox extends EntryBox {

        private int width;
        private int height;
        private long horizResolution;
        private long vertResolution;
        private int frameCount;
        private String compressorName;
        private int depth;
        private Box codecInfo;

        public VideoEntryBox(MP4Reader reader, long position, long size, String codecName) throws IOException {
            super(reader, position, size, codecName);
        }

        @Override
        protected void readEntry(RandomAccessFile stream) throws IOException {
            stream.readShort();//pre-defined
            stream.readShort();//reserved
            stream.skipBytes(4 * 3);//predefined
            this.width = stream.readShort();
            this.height = stream.readShort();
            this.horizResolution = stream.readInt();
            this.vertResolution = stream.readInt();
            stream.readInt();//reserved
            this.frameCount = stream.readShort();
            this.compressorName = StreamUtils.readWord(stream, 32);
            this.depth = stream.readShort();
            stream.readShort(); //pre_defined
            if (stream.getFilePointer() < this.getStartPosition() + this.getContentSize()) {
                int size = stream.readInt();
                String type = StreamUtils.readWord(stream, 4);
                this.codecInfo = BoxFactory.createBox(this.getReader(), stream.getFilePointer(), size, type);
                this.codecInfo.updateContext(this.getContext());
            }
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public long getHorizResolution() {
            return horizResolution;
        }

        public void setHorizResolution(long horizResolution) {
            this.horizResolution = horizResolution;
        }

        public long getVertResolution() {
            return vertResolution;
        }

        public void setVertResolution(long vertResolution) {
            this.vertResolution = vertResolution;
        }

        public int getFrameCount() {
            return frameCount;
        }

        public void setFrameCount(int frameCount) {
            this.frameCount = frameCount;
        }

        public String getCompressorName() {
            return compressorName;
        }

        public void setCompressorName(String compressorName) {
            this.compressorName = compressorName;
        }

        public int getDepth() {
            return depth;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }

        @Override
        public void updateContext(MediaInfoBuilder context) {
            try {
                MediaType type = MediaType.valueOf(this.getCodecName().toUpperCase());
                context.currentTrack().trackMediaType(type);
            } catch (Exception e) {
                System.out.println("WARNING: Unknown media type: " + this.getCodecName());
            }
        }
    }

    public static class AudioEntryBox extends EntryBox {

        private int channelCount;
        private int sampleSize;
        private long sampleRate;
        private Box audioInfo;

        public AudioEntryBox(MP4Reader reader, long position, long size, String codecName) throws IOException {
            super(reader, position, size, codecName);
        }

        @Override
        protected void readEntry(RandomAccessFile stream) throws IOException {
            stream.skipBytes(4 * 2);//reserved
            this.channelCount = stream.readShort();
            this.sampleSize = stream.readShort();
            stream.readShort(); //predefined
            stream.readShort(); //reserved
            this.sampleRate = stream.readInt();
            if (stream.getFilePointer() < this.getStartPosition() + this.getContentSize()) {
                int size = stream.readInt();
                String type = StreamUtils.readWord(stream, 4);
                this.audioInfo = BoxFactory.createBox(this.getReader(), stream.getFilePointer(), size, type);
                this.audioInfo.updateContext(this.getContext());
            }
        }

        public int getChannelCount() {
            return channelCount;
        }

        public int getSampleSize() {
            return sampleSize;
        }

        public long getSampleRate() {
            return sampleRate;
        }

        public Box getAudioInfo() {
            return audioInfo;
        }

        @Override
        public void updateContext(MediaInfoBuilder context) {
            try {
                MediaType type = MediaType.valueOf(this.getCodecName().toUpperCase());
                context.currentTrack().trackMediaType(type);
            } catch (Exception e) {
                System.out.println("WARNING: Unknown media type: " + this.getCodecName());
            }
        }
    }

    public static class HintEntryBox extends EntryBox {

        private Byte[] data;

        public HintEntryBox(MP4Reader reader, long position, long size, String codecName) throws IOException {
            super(reader, position, size, codecName);
        }

        @Override
        protected void readEntry(RandomAccessFile stream) throws IOException {
            StreamUtils.readArray(stream, Byte.class, 8);
        }

        public Byte[] getData() {
            return data;
        }

        @Override
        public void updateContext(MediaInfoBuilder context) {
        }
    }
}
