package com.rtsp.server.request;

import com.rtsp.server.RTSPConnection;
import com.rtsp.server.exceptions.InvalidOperationException;
import com.rtsp.server.exceptions.InvalidResourceException;
import com.rtsp.server.request.processors.ResourceProcessor;
import com.rtsp.server.request.processors.ResourceProcessorFactory;
import com.rtsp.server.response.RTSPResponse;

public class RTSPRequestProcessor {

    public RTSPResponse processRequest(RTSPRequest request, RTSPConnection connection) throws InvalidOperationException,
            InvalidResourceException {
        RTSPResponse.Builder builder = RTSPResponse.builder(request.getSeqNum())
                .code(200, "OK")
                .version(request.getVersion())
                .session(request.getSessionId());
        ResourceProcessor processor = ResourceProcessorFactory.createResourceProcessor(request);
        if (!processor.isTypeSupported(request.getRequestType())) {
            throw new InvalidOperationException(request.getSeqNum());
        }
        switch (request.getRequestType()) {
            case ANNOUNCE:
                return processor.processAnnounce(request, builder,connection).build();
            case DESCRIBE:
                return processor.processDescribe(request, builder,connection).build();
            case GET_PARAMETER:
                return processor.processGetParameter(request, builder,connection).build();
            case OPTIONS:
                return processor.processOptions(request, builder,connection).build();
            case PAUSE:
                return processor.processPause(request, builder,connection).build();
            case PLAY:
                return processor.processPlay(request, builder,connection).build();
            case RECORD:
                return processor.processRecord(request, builder,connection).build();
            case REDIRECT:
                throw new InvalidOperationException("Redirects may not be issued by client",request.getSeqNum());
            case SETUP:
                return processor.processSetup(request, builder,connection).build();
            case SET_PARAMETER:
                return processor.processSetParameter(request, builder,connection).build();
            case TEARDOWN:
                return processor.processTeardown(request, builder,connection).build();
            case GET:
                return processor.processGet(request, builder,connection).build();
            case LIST:
                return processor.processList(request, builder,connection).build();
            default:
                throw new InvalidOperationException("Command is not supported: "+request.getRequestType().name(),request.getSeqNum());
        }

    }

}
