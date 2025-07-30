package com.leclowndu93150.apothic_patches.mixin;

import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.PotionBonus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = PotionBonus.class, remap = false)
public interface PotionBonusAccessor {

    @Invoker("getCooldown")
    int invokeGetCooldown(LootRarity rarity);
}