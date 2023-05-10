package com.github.onetraintransferproblemgenerator.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Station {
    private int id;
    private List<Platform> platforms = new ArrayList<>();
}
