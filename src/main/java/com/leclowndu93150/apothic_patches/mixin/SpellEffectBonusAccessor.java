package com.leclowndu93150.apothic_patches.mixin;

import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import net.kayn.fallen_gems_affixes.adventure.socket.gem.bonus.SpellEffectBonus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = SpellEffectBonus.class, remap = false)
public interface SpellEffectBonusAccessor {

    @Invoker("getCooldown")
    int invokeGetCooldown(LootRarity rarity);
}