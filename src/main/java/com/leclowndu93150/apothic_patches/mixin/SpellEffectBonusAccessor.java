package com.leclowndu93150.apothic_patches.mixin;

import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import net.kayn.fallen_gems_affixes.adventure.socket.gem.bonus.SpellEffectBonus;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(value = SpellEffectBonus.class, remap = false)
public interface SpellEffectBonusAccessor {

    @Invoker("getCooldown")
    int invokeGetCooldown(LootRarity rarity);
    
    @Accessor("effect")
    MobEffect getEffect();
    
    @Accessor("values")
    Map<LootRarity, SpellEffectBonus.EffectData> getValues();
    
    @Accessor("stackOnReapply")
    boolean getStackOnReapply();
    
    @Accessor("stackingLimit")
    int getStackingLimit();
}