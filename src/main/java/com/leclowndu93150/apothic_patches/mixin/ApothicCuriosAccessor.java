package com.leclowndu93150.apothic_patches.mixin;

import daripher.apothiccurios.ApothicCuriosMod;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(value = ApothicCuriosMod.class, remap = false)
public interface ApothicCuriosAccessor {
    
    @Invoker("getEquippedCurios")
    static List<ItemStack> invokeGetEquippedCurios(LivingEntity entity) {
        throw new AssertionError();
    }
}