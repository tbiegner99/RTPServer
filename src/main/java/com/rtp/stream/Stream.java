package com.rtp.stream;

public interface Stream {

	public static enum StreamState {
		INITIALIZING,
		PLAYING,
		PAUSED,
		FINISHED,
		READY;
    }

	void start();

	void play();

	void pause();

	void terminate();

	void addMediaListener(MediaListener listener);

	StreamState getStreamState();

}
