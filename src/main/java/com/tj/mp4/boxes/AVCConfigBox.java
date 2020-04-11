package com.tj.mp4.boxes;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo.MediaInfoBuilder;
import com.tj.mp4.MediaType;
import com.tj.mp4.boxes.Box.DecoderConfigurationBox;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Base64;

public class AVCConfigBox extends DecoderConfigurationBox {

    private byte configurationVersion;
    private byte avcProfileIndication;
    private byte profileCompatability;
    private byte avcLevelIndication;
    private int lengthSizeMinusOne;
    private int numberOfSequenceParameterSets;
    private byte[][] sequenceParameterSets;
    private int numOfPictureParameterSets;
    private byte[][] pictureParameterSets;

    public AVCConfigBox(MP4Reader reader, long position, long size) throws IOException {
        super(reader, position, size, Type.AVCC);
    }

    @Override
    protected void readBox(RandomAccessFile stream) throws IOException {
        this.configurationVersion = stream.readByte();
        this.avcProfileIndication = stream.readByte();
        this.profileCompatability = stream.readByte();
        this.avcLevelIndication = stream.readByte();
        this.lengthSizeMinusOne = stream.readByte() & 0b00000011;
        this.numberOfSequenceParameterSets = stream.readByte() & 0b00000111;
        this.sequenceParameterSets = new byte[numberOfSequenceParameterSets][];
        for (int i = 0; i < this.numberOfSequenceParameterSets; i++) {
            int sequenceParameterSetLength = stream.readShort();
            sequenceParameterSets[i] = new byte[sequenceParameterSetLength];
            for (int j = 0; j < sequenceParameterSetLength; j++) {
                sequenceParameterSets[i][j] = stream.readByte();
            }
        }
        this.numOfPictureParameterSets = stream.readByte() & 0b00000111;
        this.pictureParameterSets = new byte[numOfPictureParameterSets][];
        for (int i = 0; i < this.numOfPictureParameterSets; i++) {
            int pictureParameterSetLength = stream.readShort();
            pictureParameterSets[i] = new byte[pictureParameterSetLength];
            for (int j = 0; j < pictureParameterSetLength; j++) {
                pictureParameterSets[i][j] = stream.readByte();
            }
        }

    }

    @Override
    public void updateContext(MediaInfoBuilder context) {
        context.currentTrack().trackMediaType(MediaType.AVC);
        context.currentTrack().decoderConfiguration(this);
        context.currentTrack().sampleRate(90000);
    }

    public String getParameterSets() {
        String[] sets = new String[this.numberOfSequenceParameterSets + this.numOfPictureParameterSets];
        for (int i = 0; i < numberOfSequenceParameterSets; i++) {
            sets[i] = Base64.getEncoder().encodeToString(this.sequenceParameterSets[i]);
        }
        for (int i = 0; i < numOfPictureParameterSets; i++) {
            sets[numberOfSequenceParameterSets + i] = Base64.getEncoder().encodeToString(this.pictureParameterSets[i]);
        }
        return String.join(",", sets);
    }

    public String getProfileLevelId() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%02x", getProfileIDC()));
        builder.append(String.format("%02x", getProfileIOC()));
        builder.append(String.format("%02x", getLevelIDC()));
        return builder.toString();
    }

    //1st byte is nal byte
    public byte getProfileIDC() {
        return sequenceParameterSets[0][1];
    }

    public byte getProfileIOC() {
        return (byte) (sequenceParameterSets[0][2] & 0b11100000);
    }

    public byte getLevelIDC() {
        return sequenceParameterSets[0][3];
    }

    public byte getConfigurationVersion() {
        return configurationVersion;
    }

    public byte getAvcProfileIndication() {
        return avcProfileIndication;
    }

    public byte getProfileCompatability() {
        return profileCompatability;
    }

    public byte getAvcLevelIndication() {
        return avcLevelIndication;
    }

    public int getLengthSizeMinusOne() {
        return lengthSizeMinusOne;
    }

    public int getNumberOfSequenceParameterSets() {
        return numberOfSequenceParameterSets;
    }

    public byte[][] getSequenceParameterSets() {
        return sequenceParameterSets;
    }

    public int getNumOfPictureParameterSets() {
        return numOfPictureParameterSets;
    }

    public byte[][] getPictureParameterSets() {
        return pictureParameterSets;
    }

}
