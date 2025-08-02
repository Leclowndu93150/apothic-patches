package com.leclowndu93150.apothic_patches.mixin;

import com.leclowndu93150.apothic_patches.CuriosSpellEventHandler;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import net.kayn.fallen_gems_affixes.event.SpellEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SpellEventHandler.class, remap = false)
public class SpellEventHandlerMixin {

    @Inject(method = "onSpellDamage", at = @At("TAIL"), remap = false)
    private static void onSpellDamageWithCurios(SpellDamageEvent event, CallbackInfo ci) {
        CuriosSpellEventHandler.onSpellDamage(event);
    }

    @Inject(method = "onSpellHeal", at = @At("TAIL"), remap = false)
    private static void onSpellHealWithCurios(SpellHealEvent event, CallbackInfo ci) {
        CuriosSpellEventHandler.onSpellHeal(event);
    }
}