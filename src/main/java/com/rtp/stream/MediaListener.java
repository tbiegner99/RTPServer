package com.rtp.stream;

import com.tj.mp4.MediaInfo;
import com.tj.mp4.TrackInfo;

import java.io.IOException;

public interface MediaListener {


    void onMediaFinished(Stream elementaryStream, TrackInfo trackInfo) throws IOException;

    void onMediaChanged(MediaInfo newMedia, MediaInfo oldMedia) throws IOException;
}
