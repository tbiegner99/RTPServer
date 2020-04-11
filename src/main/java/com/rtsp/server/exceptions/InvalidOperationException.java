package com.rtsp.server.exceptions;

public class InvalidOperationException extends Exception {

	private Integer seqNum;
	public InvalidOperationException(String message,Integer seqNum) {
		super(message);
		this.seqNum = seqNum;
	}
	public InvalidOperationException(Integer seqNum) {
		this.seqNum = seqNum;
	}

	public Integer getSeqNum() {
		return seqNum;
	}

}
