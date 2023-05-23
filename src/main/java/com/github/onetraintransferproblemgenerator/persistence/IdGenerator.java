package com.github.onetraintransferproblemgenerator.persistence;

import java.time.LocalDate;
import java.util.UUID;

public class IdGenerator {

    public static String generateExperimentId() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString().substring(0, 5);
        LocalDate date = LocalDate.now();
        return date + uuidString;
    }
}
