package com.example.client.mixin;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    @ModifyVariable(method = "setHeader", at = @At("HEAD"), argsOnly = true)
    private Text adfilter$setHeader(Text header) {
        return filter(header);
    }

    @ModifyVariable(method = "setFooter", at = @At("HEAD"), argsOnly = true)
    private Text adfilter$setFooter(Text footer) {
        return filter(footer);
    }

    private static Text filter(Text text) {
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
        if (result.isBlank()) return Text.empty();
        return Text.literal(result);
    }
}