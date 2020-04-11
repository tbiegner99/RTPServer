package com.tj.mp4;

import java.util.List;

public class TrackReference {
    public static enum TrackReferenceType {
        TMCD,
        CHAP,
        SYNC,
        SCPT,
        SSRC,
        FALL
    }

    private TrackReferenceType refType;
    private List<Integer> trackIds;

    public TrackReference(TrackReferenceType refType2, List<Integer> trackIds) {
        super();
        this.refType = refType2;
        this.trackIds = trackIds;
    }

    public TrackReferenceType getRefType() {
        return refType;
    }

    public List<Integer> getTrackIds() {
        return trackIds;
    }

}
