package com.tj.mp4;

import com.tj.mp4.MediaInfo.MediaInfoBuilder;
import com.tj.mp4.boxes.Box;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;

public class MP4Reader implements Iterator<Box> {

    private RandomAccessFile stream;
    private long startPosition;
    private long contentSize;
    private MediaInfoBuilder context;

    public MP4Reader(String file) throws IOException {
        this(new File(file));
    }

    public MP4Reader(File file) throws IOException {
        this(file, new RandomAccessFile(file, "r"));
    }

    private MP4Reader(File file, RandomAccessFile fileInputStream) throws IOException {
        this(fileInputStream, 0, fileInputStream.length(), MediaInfo.builder(file).source(fileInputStream));
    }


    public MP4Reader(RandomAccessFile fileInputStream, long startPosition, long contentSize, MediaInfoBuilder context) {
        this.stream = fileInputStream;
        this.startPosition = startPosition;
        this.contentSize = contentSize;
        this.context = context;

    }

    public static MediaInfo generateMediaInfo(String file) throws IOException {
        return generateMediaInfo(new File(file));
    }

    public static MediaInfo generateMediaInfo(File file) throws IOException {
        return generateMediaInfo(file, new RandomAccessFile(file, "r"));
    }

    private static MediaInfo generateMediaInfo(File file, RandomAccessFile stream) throws IOException {
        MP4Reader reader = new MP4Reader(file, stream);
        while (reader.hasNext()) {
            reader.next();
        }
        return reader.getMediaInfo();
    }

    public static List<Box> readBoxes(RandomAccessFile stream) {
        return null;
    }

    public MediaInfoBuilder getContext() {
        return context;
    }

    public RandomAccessFile getStream() {
        return stream;
    }

    public MediaInfo getMediaInfo() {
        return context.build();
    }

    @Override
    public boolean hasNext() {
        try {
            return stream.getFilePointer() >= this.startPosition
                    && stream.getFilePointer() < this.startPosition + this.contentSize;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Box next() {
        try {
            long position = stream.getFilePointer();
            int nextSize = stream.readInt();
            String boxType = StreamUtils.readWord(stream, 4);
            Box ret = BoxFactory.createBox(this, position, nextSize, boxType);
            ret.updateContext(context);
            stream.seek(position + nextSize);
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}
