package com.example.client.mixin;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerTabOverlay.class)
public class PlayerListHudMixin {

    @ModifyVariable(method = "setHeader", at = @At("HEAD"), argsOnly = true)
    private Component adfilter$setHeader(Component header) {
        return filter(header);
    }

    @ModifyVariable(method = "setFooter", at = @At("HEAD"), argsOnly = true)
    private Component adfilter$setFooter(Component footer) {
        return filter(footer);
    }

    private static Component filter(Component text) {
        if (text == null) return null;
        String s = text.getString();
        if (s.isEmpty()) return text;

        String[] lines = s.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            String low = line.toLowerCase();
            if (low.contains("hypemc.pro")
                    || low.contains("покупка доната")
                    || low.contains("скидки на донат")
                    || low.contains("оплатить можно")
                    || low.contains("/donate")
                    || low.contains("без комиссии")) {
                continue;
            }
            if (sb.length() > 0) sb.append("\n");
            sb.append(line);
        }
        String result = sb.toString();
        if (result.isBlank()) return Component.empty();
        return Component.literal(result);
    }
}