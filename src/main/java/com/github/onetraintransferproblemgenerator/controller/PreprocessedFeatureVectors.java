package com.github.onetraintransferproblemgenerator.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
class PreprocessedFeatureVectors {
    private List<List<Double>> featureVectors;
}
