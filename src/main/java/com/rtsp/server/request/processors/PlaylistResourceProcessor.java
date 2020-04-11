package com.rtsp.server.request.processors;

import com.rtp.stream.playlist.Playlist;
import com.rtsp.server.request.RTSPRequest;
import com.rtsp.server.request.RTSPRequest.Type;
import com.rtsp.server.response.RTSPResponse.Builder;

public class PlaylistResourceProcessor extends AbstractResourceProcessor {


	@Override
	public Playlist getPlaylist(RTSPRequest request) {
		return null;
	}
}
