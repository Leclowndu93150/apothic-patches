package com.leclowndu93150.apothic_patches.events;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = "apothic_patches", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HudEffectAmplifierRenderer {
    private static final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.POTION_ICONS.type()) return;
        if (mc.player == null || mc.options.hideGui || mc.player.getActiveEffects().isEmpty()) return;
        if(FMLLoader.isProduction()) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        Collection<MobEffectInstance> effects = mc.player.getActiveEffects();

        int beneficialX = screenWidth - 25;
        int beneficialY = 1;
        int harmfulX = screenWidth - 25;
        int harmfulY = 1;

        for (MobEffectInstance effect : effects) {
            //if (!effect.shouldRenderInvText()) continue;

            int amplifier = effect.getAmplifier();

            String amplifierText = Integer.toString(amplifier + 1);

            if (effect.getEffect().isBeneficial()) {
                guiGraphics.drawString(mc.font, amplifierText, beneficialX - 20, beneficialY + 6, 0xFFFFFF, true);
                beneficialY += 25;
            } else {
                guiGraphics.drawString(mc.font, amplifierText, harmfulX - 20, harmfulY + 6, 0xFFFFFF, true);
                harmfulY += 25;
            }
        }
    }
}