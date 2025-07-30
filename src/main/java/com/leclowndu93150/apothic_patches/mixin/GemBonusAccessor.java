package com.leclowndu93150.apothic_patches.mixin;

import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = GemBonus.class, remap = false)
public interface GemBonusAccessor {

    @Invoker("getCooldownId")
    ResourceLocation invokeGetCooldownId(ItemStack stack);
}