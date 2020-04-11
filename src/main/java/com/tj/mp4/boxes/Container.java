package com.tj.mp4.boxes;

import java.util.List;

public interface Container {
    List<Box> getChildren();

    int getChildrenSize();
}
