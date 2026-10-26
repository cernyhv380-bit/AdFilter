package com.example.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
    @Shadow @Final private Map<UUID, ClientBossBar> bossBars;

    private static final Map<UUID, ClientBossBar> adfilter$hidden = new HashMap<>();

    @Inject(method = "render", at = @At("HEAD"))
    private void adfilter$hideNonVanilla(DrawContext context, CallbackInfo ci) {
        adfilter$hidden.clear();
        if (!adfilter$isTargetServer()) return;

        bossBars.entrySet().removeIf(e -> {
            if (!isVanillaBossBar(e.getValue().getName())) {
                adfilter$hidden.put(e.getKey(), e.getValue());
                return true;
            }
            return false;
        });
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void adfilter$restoreHidden(DrawContext context, CallbackInfo ci) {
        if (!adfilter$hidden.isEmpty()) {
            bossBars.putAll(adfilter$hidden);
            adfilter$hidden.clear();
        }
    }

    private static boolean isVanillaBossBar(Text name) {
        if (name == null) return false;
        if (name.getContent() instanceof TranslatableTextContent t) {
            String key = t.getKey();
            return "entity.minecraft.ender_dragon".equals(key)
                    || "entity.minecraft.wither".equals(key);
        }
        return false;
    }

    private static boolean adfilter$isTargetServer() {
        ServerInfo server = MinecraftClient.getInstance().getCurrentServerEntry();
        if (server == null || server.address == null) return false;

        String host = server.address.trim().toLowerCase(Locale.ROOT);
        int lastColon = host.lastIndexOf(':');
        if (lastColon >= 0 && host.indexOf(':') == lastColon) {
            host = host.substring(0, lastColon);
        }
        if (host.endsWith(".")) host = host.substring(0, host.length() - 1);

        return adfilter$matchesDomain(host, "hypemc.pro")
                || adfilter$matchesDomain(host, "mineblaze.net")
                || adfilter$matchesDomain(host, "mineblaze.ru")
                || adfilter$matchesDomain(host, "dexland.org");
    }

    private static boolean adfilter$matchesDomain(String host, String domain) {
        return host.equals(domain) || host.endsWith("." + domain);
    }
}
