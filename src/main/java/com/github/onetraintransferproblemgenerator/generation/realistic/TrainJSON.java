package com.github.onetraintransferproblemgenerator.generation.realistic;

import lombok.Data;

import java.util.List;

@Data
class TrainJSON {
    private String id;
    private String trainType;
    private int carriageCount;
    private List<CarriageData> carriageData;

    @Data
    public static class CarriageData {
        private int carriageId;
        private int capacity;
    }
}
