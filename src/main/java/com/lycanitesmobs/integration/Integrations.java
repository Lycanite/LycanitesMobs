package com.lycanitesmobs.integration;
import com.chaosbuffalo.targeting_api.Targeting;
import com.lycanitesmobs.elementalmobs.entity.EntityNymph;

public class Integrations {

    private static void setupTargetingApi() {
        Targeting.registerFriendlyEntity(EntityNymph.class.getName());
    }

    public static void setup() {
        setupTargetingApi();
    }
}
