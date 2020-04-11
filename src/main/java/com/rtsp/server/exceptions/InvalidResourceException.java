package com.rtsp.server.exceptions;

public class InvalidResourceException extends Exception {

	private Integer seqNum;

	public InvalidResourceException(Integer seqNum) {
		this.seqNum = seqNum;
	}

	public Integer getSeqNum() {
		return seqNum;
	}

}
