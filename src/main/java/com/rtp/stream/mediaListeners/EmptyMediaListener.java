package com.rtp.stream.mediaListeners;

import com.rtp.stream.MediaListener;
import com.rtp.stream.Stream;
import com.tj.mp4.MediaInfo;
import com.tj.mp4.TrackInfo;

import java.io.IOException;

public class EmptyMediaListener implements MediaListener {


    @Override
    public void onMediaFinished(Stream elementaryStream, TrackInfo trackInfo) {

    }

    @Override
    public void onMediaChanged(MediaInfo newMedia, MediaInfo oldMedia) throws IOException {

    }

}
