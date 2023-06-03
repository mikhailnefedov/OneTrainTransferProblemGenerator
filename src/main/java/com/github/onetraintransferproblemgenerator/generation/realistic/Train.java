package com.github.onetraintransferproblemgenerator.generation.realistic;

import lombok.Data;

import java.util.List;

@Data
class Train {
    private String id;
    private String trainType;
    private int carriageCount;
    private List<CarriageData> carriageData;

    @Data
    private static class CarriageData {
        private String carriageType;
        private int carriageId;
        private int capacity;
    }
}
