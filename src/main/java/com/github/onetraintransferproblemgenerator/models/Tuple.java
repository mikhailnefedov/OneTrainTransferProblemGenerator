package com.github.onetraintransferproblemgenerator.models;

import lombok.Data;

@Data
public class Tuple<T1,T2> {
    private T1 left;
    private T2 right;
}
