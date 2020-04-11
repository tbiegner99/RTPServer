package com.rtsp.server.request.processors;

import com.rtsp.server.exceptions.InvalidResourceException;
import com.rtsp.server.request.RTSPRequest;

public class ResourceProcessorFactory {

    public static ResourceProcessor createResourceProcessor(RTSPRequest request) throws
            InvalidResourceException {
        switch (request.getPathComponents()[0]) {
            case "kareoke":
                return new KareokeResourceProcessor();
            case "channel":
                return new ChannelResourceProcessor();
            case "playlist":
                return new PlaylistResourceProcessor();
            case "item":
                return new ItemResourceProcessor();
        }
        throw new InvalidResourceException(request.getSeqNum());
    }
}
