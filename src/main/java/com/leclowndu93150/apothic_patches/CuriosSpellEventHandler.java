package com.leclowndu93150.apothic_patches;

import com.leclowndu93150.apothic_patches.mixin.GemBonusAccessor;
import com.leclowndu93150.apothic_patches.mixin.SpellEffectBonusAccessor;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemItem;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import net.kayn.fallen_gems_affixes.adventure.affix.SpellEffectAffix;
import net.kayn.fallen_gems_affixes.adventure.socket.gem.bonus.SpellEffectBonus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ApothicPatches.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CuriosSpellEventHandler {

    @SubscribeEvent
    public static void onSpellDamage(SpellDamageEvent event) {
        LivingEntity target = event.getEntity();
        LivingEntity caster = (LivingEntity) event.getSpellDamageSource().getEntity();
        
        if (!(caster instanceof LivingEntity)) return;

        processSpellEffectsFromCurios(caster, target, SpellEffectAffix.Target.SPELL_DAMAGE_SELF, SpellEffectAffix.Target.SPELL_DAMAGE_TARGET);
    }

    @SubscribeEvent
    public static void onSpellHeal(SpellHealEvent event) {
        LivingEntity caster = event.getEntity();
        LivingEntity target = event.getTargetEntity();
        
        if (caster == null) return;

        processSpellEffectsFromCurios(caster, target, SpellEffectAffix.Target.SPELL_HEAL_SELF, SpellEffectAffix.Target.SPELL_HEAL_TARGET);
    }

    private static void processSpellEffectsFromCurios(LivingEntity caster, LivingEntity target, SpellEffectAffix.Target selfTarget, SpellEffectAffix.Target otherTarget) {
        CuriosApi.getCuriosInventory(caster).ifPresent(curiosInventory -> {
            IItemHandlerModifiable equippedCurios = curiosInventory.getEquippedCurios();
            
            for (int i = 0; i < equippedCurios.getSlots(); i++) {
                ItemStack stack = equippedCurios.getStackInSlot(i);
                if (stack.isEmpty()) continue;

                Map<DynamicHolder<? extends Affix>, AffixInstance> affixes = AffixHelper.getAffixes(stack);
                for (Map.Entry<DynamicHolder<? extends Affix>, AffixInstance> entry : affixes.entrySet()) {
                    processSpellEffectAffix(entry.getKey().get(), entry.getValue(), stack, caster, target, selfTarget, otherTarget);
                }

                List<GemInstance> gems = SocketHelper.getGems(stack).gems();
                LootCategory category = LootCategory.forItem(stack);
                if (category != null) {
                    for (GemInstance gemInstance : gems) {
                        DynamicHolder<LootRarity> rarityHolder = gemInstance.rarity();
                        if (rarityHolder.isBound()) {
                            LootRarity rarity = rarityHolder.get();

                            Gem gemObj = gemInstance.gem().get();
                            gemObj.getBonus(category, rarity).ifPresent(bonus -> 
                                processSpellEffectGemBonus(bonus, gemInstance, caster, target, rarity, selfTarget, otherTarget)
                            );
                        }
                    }
                }
            }
        });
    }

    private static void processSpellEffectAffix(Affix affix, AffixInstance instance, ItemStack stack, LivingEntity caster, LivingEntity target, SpellEffectAffix.Target selfTarget, SpellEffectAffix.Target otherTarget) {
        if (affix instanceof SpellEffectAffix spellAffix) {
            SpellEffectAffix.Target affixTarget = spellAffix.target;
            

            LootRarity rarity = instance.rarity().get();
            float level = instance.level();
            
            if (rarity != null) {
                if (affixTarget == selfTarget) {
                    spellAffix.applyEffect(caster, rarity, level);
                } else if (affixTarget == otherTarget && target != null) {
                    spellAffix.applyEffect(target, rarity, level);
                }
            }
        }
    }

    private static void processSpellEffectGemBonus(GemBonus bonus, GemInstance gemInstance, LivingEntity caster, LivingEntity target, LootRarity rarity, SpellEffectAffix.Target selfTarget, SpellEffectAffix.Target otherTarget) {
        if (bonus instanceof SpellEffectBonus spellBonus) {
            SpellEffectAffix.Target bonusTarget = spellBonus.target;
            ItemStack gemStack = gemInstance.gemStack();

            ResourceLocation cooldownId = ((GemBonusAccessor) bonus).invokeGetCooldownId(gemStack);
            int cooldown = ((SpellEffectBonusAccessor) spellBonus).invokeGetCooldown(rarity);
            
            if (cooldown == 0 || !Affix.isOnCooldown(cooldownId, cooldown, caster)) {
                if (bonusTarget == selfTarget) {
                    applySpellEffectWithCap(spellBonus, gemStack, caster, rarity);
                    Affix.startCooldown(cooldownId, caster);
                } else if (bonusTarget == otherTarget && target != null) {
                    applySpellEffectWithCap(spellBonus, gemStack, target, rarity);
                    Affix.startCooldown(cooldownId, caster);
                }
            }
        }
    }
    
    private static void applySpellEffectWithCap(SpellEffectBonus spellBonus, ItemStack gemStack, LivingEntity target, LootRarity rarity) {
        SpellEffectBonusAccessor accessor = (SpellEffectBonusAccessor) spellBonus;
        MobEffect effect = accessor.getEffect();
        SpellEffectBonus.EffectData data = accessor.getValues().get(rarity);
        
        if (data == null) return;
        
        ResourceLocation gemId = GemItem.getGem(gemStack).getId();
        int maxAmp = MaxAmplifierManager.getMaxAmplifier(gemId);
        
        if (maxAmp == -1) {
            maxAmp = Config.defaultAmplifierCap;
        }
        
        boolean stackOnReapply = accessor.getStackOnReapply();
        int stackingLimit = accessor.getStackingLimit();
        
        MobEffectInstance currentEffect = target.getEffect(effect);
        
        if (stackOnReapply && currentEffect != null) {
            int amplifier = Math.min(stackingLimit, currentEffect.getAmplifier() + 1 + data.amplifier());
            
            if (maxAmp >= 0 && amplifier > maxAmp) {
                amplifier = maxAmp;
            }
            
            MobEffectInstance newInst = new MobEffectInstance(
                effect,
                Math.max(currentEffect.getDuration(), data.duration()),
                amplifier,
                false,
                true,
                true
            );
            target.addEffect(newInst);
        } else {
            int amplifier = data.amplifier();
            
            if (maxAmp >= 0 && amplifier > maxAmp) {
                amplifier = maxAmp;
            }
            
            MobEffectInstance newInst = new MobEffectInstance(
                effect,
                data.duration(),
                amplifier,
                false,
                true,
                true
            );
            target.addEffect(newInst);
        }
    }
}