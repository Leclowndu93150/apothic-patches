package com.leclowndu93150.apothic_patches.mixin;

import net.kayn.fallen_gems_affixes.event.PermanentEffectHandler;
import net.kayn.fallen_gems_affixes.util.ProtectedMobEffectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(value = PermanentEffectHandler.class, remap = false)
public interface PermanentEffectHandlerAccessor {

    @Accessor("tickEventProtectedMapWrapper")
    static Map<UUID, ProtectedMobEffectMap<?>> getTickEventProtectedMapWrapper() {
        throw new AssertionError();
    }
}