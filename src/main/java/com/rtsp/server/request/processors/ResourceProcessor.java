package com.rtsp.server.request.processors;

import com.rtsp.server.RTSPConnection;
import com.rtsp.server.request.RTSPRequest;
import com.rtsp.server.request.RTSPRequest.Type;
import com.rtsp.server.response.RTSPResponse.Builder;

public interface ResourceProcessor {
	boolean isTypeSupported(RTSPRequest.Type type);

	Builder processGet(RTSPRequest request, Builder builder, RTSPConnection connection);

	Builder processPlay(RTSPRequest request, Builder builder, RTSPConnection connection);

	Builder processTeardown(RTSPRequest request, Builder builder, RTSPConnection connection);

	Builder processSetup(RTSPRequest request, Builder builder, RTSPConnection connection);

	Builder processDescribe(RTSPRequest request, Builder builder, RTSPConnection connection);

	Builder processOptions(RTSPRequest request, Builder builder, RTSPConnection connection);

	Builder processAnnounce(RTSPRequest request, Builder builder, RTSPConnection connection);

	Builder processGetParameter(RTSPRequest request, Builder builder, RTSPConnection connection);

	Builder processSetParameter(RTSPRequest request, Builder builder, RTSPConnection connection);

	Builder processRecord(RTSPRequest request, Builder builder, RTSPConnection connection);

	Builder processPause(RTSPRequest request, Builder builder, RTSPConnection connection);

	Builder processList(RTSPRequest request, Builder builder, RTSPConnection connection);

	Type[] getSupportedTypes();
}
