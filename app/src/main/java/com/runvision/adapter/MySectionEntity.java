package com.runvision.adapter;

import com.chad.library.adapter.base.entity.SectionEntity;

public class MySectionEntity extends SectionEntity<PictureTypeEntity> {

    public MySectionEntity(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public MySectionEntity(PictureTypeEntity pictureType) {
        super(pictureType);
    }
}