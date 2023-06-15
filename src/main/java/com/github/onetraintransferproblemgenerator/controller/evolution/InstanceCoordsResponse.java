package com.github.onetraintransferproblemgenerator.controller.evolution;

import com.github.onetraintransferproblemgenerator.controller.evolution.InstanceCoordsPair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceCoordsResponse {
    private List<List<Double>> coordinates;

}


