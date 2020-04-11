package com.rtp.stream;

import com.rtp.packet.RTPPacketGenerator;
import com.rtp.packet.RTPPacketGeneratorFactory;
import com.rtp.stream.playlist.Playlist;
import com.rtp.stream.socket.SocketManager;
import com.tj.mp4.MediaInfo;
import com.tj.mp4.TrackInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AVStreamManager implements MediaStreamManager, MediaListener {

    private Optional<MediaStream> audioStream = Optional.empty();
    private Optional<MediaStream> videoStream = Optional.empty();
    private Playlist playlist;
    private StreamState state = StreamState.INITIALIZING;
    private boolean repeat;
    private List<MediaListener> listeners;
    private float totalTime = 0;


    public AVStreamManager() {
        listeners = new ArrayList<>();
    }

    private void addVideoStream(MediaStream stream) {
        videoStream = Optional.of(stream);

    }

    private void addAudioStream(MediaStream stream) {
        audioStream = Optional.of(stream);
    }

    private MediaStream createStream(StreamType streamType, SocketManager socketManager, int lastSequenceNumber, float initialTimeInSeconds) {

        return StreamFactory.buildMediaStream()
                .ofType(streamType)
                .withSocketManager(socketManager)
                .withStartingTime(initialTimeInSeconds)
                .withLastSequenceNumber(lastSequenceNumber)
                .build();
    }

    private MediaStream setupStreamWithNextItemInPlaylist(StreamType streamType, SocketManager socketManager, int lastSequenceNumber, float initialTimeInSeconds) throws IOException {
        MediaStream stream = createStream(streamType, socketManager, lastSequenceNumber, initialTimeInSeconds);
        stream.addMediaListener(this);
        MediaInfo media = playlist.getCurrentItem();
        RTPPacketGenerator generator = RTPPacketGeneratorFactory.getPacketGenerator(streamType, media);
        stream.setupMedia(generator);
        return stream;
    }

    private MediaStream setupStreamWithNextItemInPlaylist(StreamType streamType, SocketManager socketManager) throws IOException {
        return setupStreamWithNextItemInPlaylist(streamType, socketManager, 0, 0);
    }

    @Override
    public void createMediaStream(Playlist playlist, StreamType streamType, SocketManager socketManager) throws IOException {
        this.playlist = playlist;
        playlist.next();
        MediaStream stream = setupStreamWithNextItemInPlaylist(streamType, socketManager);
        switch (streamType) {
            case AUDIO:
                addAudioStream(stream);
                break;
            case VIDEO:
                addVideoStream(stream);
                break;
        }
    }

    @Override
    public void start() {
        videoStream.ifPresent(MediaStream::start);
        audioStream.ifPresent(MediaStream::start);
        this.state = StreamState.PLAYING;
    }

    @Override
    public void play() {
        videoStream.ifPresent(MediaStream::play);
        audioStream.ifPresent(MediaStream::play);
        this.state = StreamState.READY;

    }

    @Override
    public void pause() {
        videoStream.ifPresent(MediaStream::pause);
        audioStream.ifPresent(MediaStream::pause);
        this.state = StreamState.PAUSED;
    }

    @Override
    public void terminate() {
        videoStream.ifPresent(MediaStream::terminate);
        audioStream.ifPresent(MediaStream::terminate);
        this.state = StreamState.FINISHED;

    }

    @Override
    public void addMediaListener(MediaListener listener) {
        listeners.add(listener);
    }

    @Override
    public StreamState getStreamState() {
        return state;
    }


    private boolean isStreamFinished(Optional<MediaStream> stream) {
        return stream.isEmpty() || stream
                .filter(s -> s.getStreamState() == StreamState.FINISHED)
                .isPresent();
    }

    private boolean areAllStreamsFinished() {
        return isStreamFinished(videoStream) && isStreamFinished(audioStream);
    }

    private Optional<MediaStream> resetStream(Optional<MediaStream> stream, float initialTimestampInSeconds) throws IOException {
        if (stream.isPresent()) {
            MediaStream mediaStream = stream.get();
            MediaStream newStream = setupStreamWithNextItemInPlaylist(mediaStream.getType(), mediaStream.getSocketManager(), mediaStream.getLastSequenceNumber(), initialTimestampInSeconds);
            return Optional.of(newStream);
        }
        return Optional.empty();
    }

    @Override
    public void onMediaFinished(Stream elementaryStream, TrackInfo trackInfo) throws IOException {
        if (areAllStreamsFinished()) {
            if (!playlist.hasNext()) {
                //terminate manager
            }
            MediaInfo oldItem = playlist.getCurrentItem();
            MediaInfo newItem = playlist.next();
            this.onMediaChanged(newItem, oldItem);

        }

    }

    @Override
    public void onMediaChanged(MediaInfo newMedia, MediaInfo oldMedia) throws IOException {
        for (MediaListener listener : listeners) {
            listener.onMediaChanged(newMedia, oldMedia);
        }
        totalTime += oldMedia.getDurationInSeconds();
        float initialTimestampInSeconds = totalTime;
        videoStream = resetStream(videoStream, initialTimestampInSeconds);
        audioStream = resetStream(audioStream, initialTimestampInSeconds);
        this.start();
    }


}
