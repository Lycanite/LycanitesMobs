package com.lycanitesmobs.api;

import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;

import java.util.Set;
import java.util.function.BiFunction;

/**
 * Created by Jacob on 4/18/2018.
 */
public class Targeting {

    private static Set<BiFunction<Entity, Entity, Boolean>> callbacks = Sets.newHashSet();

    public static void registerCallback(BiFunction<Entity, Entity, Boolean> callback) {
        callbacks.add(callback);
    }

    public static boolean isValidTarget(Entity caster, Entity target){
        for (BiFunction<Entity, Entity, Boolean> callback : callbacks){
            if (callback.apply(caster, target)){
                return true;
            }
        }
        return false;
    }
}
