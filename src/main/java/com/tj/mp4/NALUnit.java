package com.tj.mp4;

public class NALUnit {

    private int size;
    private int ref_idc;
    private int type;
    private byte[] content;
    private int number;
    private int timestampOffset;

    private NALUnit() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public byte getHeaderByte() {
        return content[0];
    }

    public int getSize() {
        return size;
    }

    public int getRef_idc() {
        return ref_idc;
    }

    public int getType() {
        return type;
    }

    public byte[] getContent() {
        return content;
    }

    public int getNumber() {
        return number;
    }

    public int getTimestampOffset() {
        return timestampOffset;
    }

    public static class Builder {
        private NALUnit item;

        public Builder() {
            this.item = new NALUnit();
        }

        public NALUnit build() {
            return item;
        }

        public Builder size(int nalSize) {
            item.size = nalSize;
            return this;
        }

        public Builder content(byte[] content) {
            item.content = content;
            item.type = content[0] & 0b00011111;
            item.ref_idc = (content[0] & 0b01100000) >> 5;
            return this;
        }

        public Builder number(int num) {
            item.number = num;
            return this;
        }

        public Builder timestampOffset(int timestampOffset) {
            item.timestampOffset = timestampOffset;
            return this;
        }
    }
}
