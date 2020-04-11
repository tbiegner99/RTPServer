package com.rtcp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.TimeZone;

public class RTCPPacket {
	private byte version = 0x02;
	private byte paddingBit = 0;
	private byte receptionReportCount = 0;
	private int payloadType = 200;
	private int senderSSRC;
	private int ntpSecs;
	private float ntpFraction;
	private int rtpTimestamp;
	private int senderPacketCount;
	private int senderOctetCount;

	public int getFirstLine() {
		int line = version << 30;
		//report count is 0
		line |= (payloadType << 16);
		short length = (short) 6; //words -1 
		line |= length;
		return line;
	}

	public DatagramPacket toDatagramPacket(InetAddress address, int port) {
		ByteBuffer buffer = ByteBuffer.allocate(28);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.putInt(this.getFirstLine());
		buffer.putInt(this.senderSSRC);
		buffer.putInt(this.ntpSecs);
		buffer.putFloat(this.ntpFraction);
		buffer.putInt(this.rtpTimestamp);
		buffer.putInt(this.senderPacketCount);
		buffer.putInt(this.senderOctetCount);
		byte[] buff = buffer.array();
		return new DatagramPacket(buff, buff.length, address, port);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private RTCPPacket item;

		private Builder() {
			item = new RTCPPacket();
		}

		public Builder withVersion(byte version) {
			item.version = version;
			return this;
		}

		public Builder withPaddingBit(byte paddingBit) {
			item.paddingBit = paddingBit;
			return this;
		}

		public Builder withReceptionReportCount(byte receptionReportCount) {
			item.receptionReportCount = receptionReportCount;
			return this;
		}

		public Builder withPayloadType(byte payloadType) {
			item.payloadType = payloadType;
			return this;
		}

		public Builder withSenderSSRC(int senderSSRC) {
			item.senderSSRC = senderSSRC;
			return this;
		}

		public Builder withCurrentTime() {
			TimeZone utcZone = TimeZone.getTimeZone("UTC");
			Calendar calendar = Calendar.getInstance(utcZone);
			calendar.set(1900, Calendar.JANUARY, 1, 0, 0, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			long msb1baseTime = calendar.getTime().getTime();
			long currentTime = Calendar.getInstance(utcZone).getTime().getTime();
			float ntpFraction = ((currentTime - msb1baseTime) % 1000) / 1000f;
			return this.withNtpSecs((int) ((currentTime - msb1baseTime) / 1000)).withNtpFraction(ntpFraction);
		}

		public Builder withNtpSecs(int ntpSecs) {
			item.ntpSecs = ntpSecs;
			return this;
		}

		public Builder withNtpFraction(float ntpFraction) {
			item.ntpFraction = ntpFraction;
			return this;
		}

		public Builder withRtpTimestamp(int rtpTimestamp) {
			item.rtpTimestamp = rtpTimestamp;
			return this;
		}

		public Builder withSenderPacketCount(int senderPacketCount) {
			item.senderPacketCount = senderPacketCount;
			return this;
		}

		public Builder withSenderOctetCount(int senderOctetCount) {
			item.senderOctetCount = senderOctetCount;
			return this;
		}

		public RTCPPacket build() {
			return item;
		}
	}
}
