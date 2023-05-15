package com.github.onetraintransferproblemgenerator.models;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Platform {
    @CsvBindByName
    private int id;
    @CsvBindByName
    private int positionCount;
}
