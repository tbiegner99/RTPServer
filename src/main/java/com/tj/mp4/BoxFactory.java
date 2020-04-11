package com.tj.mp4;

import com.tj.mp4.boxes.*;
import com.tj.mp4.boxes.Box.Type;

import java.io.IOException;

public class BoxFactory {

    public static Box createBox(MP4Reader reader, long position, int size, String type) throws IOException {
        Box.Type boxType;
        try {
            boxType = Box.Type.valueOf(type.replace(" ", "_").toUpperCase());
        } catch (Exception e) {
            return new UnknownBox(reader, position, size, type);
            //throw new IllegalArgumentException("Unknown Box Type: " + type);
        }
        switch (boxType) {
            case FTYP:
                return new FTYPBox(reader, position, size);
            case FREE:
                return new FreeBox(reader, position, size);
            case MDAT:
                return new MDatBox(reader, position, size);
            case MOOV:
                return new MoovBox(reader, position, size);
            case MVHD:
                return new MvhdBox(reader, position, size);
            case TRAK:
                return new TrackBox(reader, position, size);
            case TKHD:
                return new TrackHeaderBox(reader, position, size);
            case EDTS:
                return new BoxContainer(reader, position, size, Type.EDTS);
            case ELST:
                return new EditListBox(reader, position, size);
            case TREF:
                return new TrackReferenceBox(reader, position, size);
            case MDIA:
                return new BoxContainer(reader, position, size, Type.MDIA);
            case MDHD:
                return new MediaHeaderBox(reader, position, size);
            case HDLR:
                return new HandlerBox(reader, position, size);
            case IODS:
                return new UnknownBox(reader, position, size, boxType);
            case MINF:
                return new BoxContainer(reader, position, size, Type.MINF);
            case VMHD:
                return new VideoMediaHeaderBox(reader, position, size);
            case SMHD:
                return new SoundMediaHeaderBox(reader, position, size);
            case HMHD:
                return new HintMediaHeaderBox(reader, position, size);
            case DINF:
                return new BoxContainer(reader, position, size, Type.DINF);
            case DREF:
                return new DataReferenceBox(reader, position, size);
            case URL_:
                return new DataEntryBox(reader, position, size, Type.URL_);
            case URN_:
                return new DataEntryBox(reader, position, size, Type.URN_);
            case STBL:
                return new BoxContainer(reader, position, size, Type.STBL);
            case STSD:
                return new SampleDescriptionBox(reader, position, size);
            case STTS:
                return new TimeToSampleBox(reader, position, size);
            case STSS:
                return new SyncSampleBox(reader, position, size);
            case CTTS:
                return new CompositionTimeToSampleBox(reader, position, size);
            case STSC:
                return new SampleToChunkBox(reader, position, size);
            case STSZ:
            case STZ2:
                return new SampleSizeBox(reader, position, size);
            case STCO:
                return new ChunkOffsetBox(reader, position, size, false);
            case CO64:
                return new ChunkOffsetBox(reader, position, size, true);
            case UDTA:
                return new FreeBox(reader, position, size);
            case AVCC:
                return new AVCConfigBox(reader, position, size);
            case MP4A:
                return new MP4aBox(reader, position, size);
            case ESDS:
                return new ESDSBox(reader, position, size);
            case MFRA:
            case MOOF:

            case PDIN:
            case SKIP:

            default:
                throw new UnsupportedOperationException("box Type not supported: " + type);
        }
    }
}
