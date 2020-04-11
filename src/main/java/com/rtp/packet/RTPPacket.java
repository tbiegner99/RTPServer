package com.rtp.packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RTPPacket {

	private static final int version = 2;
	private static final int padding = 0;
	private byte payloadType = 98;//mp4
	public static int syncSource = 46;// new Random().nextInt();
	private boolean markerBit = false;
	private int extensionBit;
	private int sequenceNumber;
	private int timestamp;
	private byte[] payload;
	private static int CLOCK_INCREMENT = 90000 / 10; //1/10th of a second defined by rtp packet
	private static int BITRATE = 809000 / 8; //in bytes per second
	public static int PAYLOAD_SIZE = BITRATE / 10;

	public static void main(String[] args) throws IOException {

		InetAddress group = InetAddress.getByName("233.5.6.7");

		MulticastSocket socket = new MulticastSocket(4000);
		socket.joinGroup(group);
		while (true) {
			byte[] buff = new byte[7000];
			DatagramPacket packet2 = new DatagramPacket(buff, 7000);
			socket.receive(packet2);
			System.out.println(RTPPacket.fromDatagramPacket(packet2));

		}
	}

	private RTPPacket() {
	}

	public RTPPacket(short seqnum, byte[] payload) {
		sequenceNumber = seqnum;
		timestamp = seqnum * CLOCK_INCREMENT;
		if (payload.length != PAYLOAD_SIZE) {
			throw new IllegalArgumentException();
		}
		this.payload = payload;

	}

	public int getNalType() {
		return (this.getPayload()[0] & 0b00011111);
	}

	public String toString() {
		return this.toString(null);
	}

	public String toString(Long clock) {
		StringBuilder builder = new StringBuilder();
		builder.append("Version: " + this.getVersion() + "\r\n");
		builder.append("Padding: " + this.getPadding() + "\r\n");
		builder.append("XBit: " + this.getExtensionBit() + "\r\n");
		builder.append("SequenceNumber: " + (int) this.getSequenceNumber() + "\r\n");
		builder.append("Timestamp: " + this.getTimestamp() + "\r\n");
		if (clock != null) {
			builder.append("Timestamp: " + this.getTimestamp() / (float) clock + "\r\n");
		}
		builder.append("Payload Type: " + this.getPayloadtype() + "\r\n");
		builder.append("SSRC: " + this.getSyncSource() + "\r\n");
		builder.append("Payload Size: " + payload.length + "\r\n");
		builder.append("NRI: " + ((this.getPayload()[0] & 0b01100000) >> 5) + "\r\n");
		builder.append("TYPE: " + this.getNalType() + "\r\n");
		if (this.getPayloadtype() == 28) {
			builder.append("S: " + ((this.getPayload()[1] & 0b10000000) >> 7) + "\r\n");
			builder.append("E: " + ((this.getPayload()[1] & 0b01000000) >> 6) + "\r\n");
			builder.append("TYPE: " + (this.getPayload()[1] & 0b00011111) + "\r\n");
		}
		//builder.append("Payload: " + new String(payload) + "\r\n");
		return builder.toString();
	}

	private int getExtensionBit() {
		return extensionBit;
	}

	public static RTPPacket fromDatagramPacket(DatagramPacket packet) throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(packet.getData());
		int remainingBytes = packet.getLength();
		DataInputStream stream2 = new DataInputStream(stream);
		RTPPacket ret = new RTPPacket();
		int word = stream2.readInt();
		remainingBytes -= 4;
		ret.sequenceNumber = (short) (word & 0x0000FFFF);
		ret.payloadType = (byte) ((word >> 16) & 0b01111111);
		ret.extensionBit = word & 0x10000000;
		ret.timestamp = stream2.readInt();
		remainingBytes -= 4;
		ret.payload = new byte[remainingBytes];
		stream2.read(ret.payload);
		return ret;
	}

	public byte[] toNetworkData() {
		ByteBuffer buffer = ByteBuffer.allocate(12 + this.getSize());
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.put(this.getPacketHeader());
		buffer.put(this.getPayload());
		byte[] buff = buffer.array();
		return buff;
	}

	public int getVersion() {
		return version;
	}

	public int getPadding() {
		return padding;
	}

	public int getPayloadtype() {
		return payloadType;
	}

	public static int getSyncSource() {
		return syncSource;
	}

	public short getSequenceNumber() {
		return (short) sequenceNumber;
	}

	public int getTimestamp() {
		return timestamp;
	}

	private byte[] intToBytes(int item) {
		byte[] ret = new byte[4];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = (byte) (item >> ((ret.length - 1 - i) * 8));
		}
		return ret;
	}

	private int getFirstWord() {
		int ret = version << 30;
		ret |= ((int) padding << 29);
		ret |= (0 << 28);//extension;
		ret |= 0 << 24; //cc
		ret |= (markerBit ? 1 : 0) << 23; //marker
		ret |= ((int) payloadType << 16);
		ret |= sequenceNumber & 0x0000FFFF;
		return ret;
	}

	public byte[] getPayload() {
		return payload;
	}

	public byte[] getPacketHeader() {
		ByteBuffer buffer = ByteBuffer.allocate(12);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.put(intToBytes(getFirstWord()));
		buffer.putInt(timestamp);
		buffer.putInt(syncSource);
		//buffer.putInt(0);
		return buffer.array();
	}

	public int getSize() {
		return this.getPayload().length;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(RTPPacket packet) {
		return new Builder(packet);
	}

	public static class Builder {
		private RTPPacket item;

		private Builder() {
			this.item = new RTPPacket();
		}

		private Builder(RTPPacket item) {
			this.item = item;
		}

		public RTPPacket build() {
			return item;
		}

		public Builder sequenceNumber(int number) {
			item.sequenceNumber = number;
			return this;
		}

		public Builder timestamp(int timestamp) {
			item.timestamp = timestamp;
			return this;
		}

		public Builder payloadType(PayloadType payloadType) {
			return this.payloadType(payloadType.getId());
		}

		public Builder payloadType(byte payloadType) {
			payloadType &= 0b01111111;
			item.payloadType = payloadType;
			return this;
		}

		public Builder content(byte[] content) {
			item.payload = content;
			return this;
		}

		public Builder marker(boolean marker) {
			item.markerBit = marker;
			return this;
		}

	}

}
