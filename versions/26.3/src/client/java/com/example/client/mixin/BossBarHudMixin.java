package com.example.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
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

@Mixin(BossHealthOverlay.class)
public class BossBarHudMixin {
	@Shadow @Final private Map<UUID, LerpingBossEvent> events;

	private static final Map<UUID, LerpingBossEvent> adfilter$hidden = new HashMap<>();

	@Inject(method = "extractRenderState", at = @At("HEAD"))
	private void adfilter$hideNonVanilla(GuiGraphicsExtractor graphics, CallbackInfo ci) {
		adfilter$hidden.clear();
		if (!adfilter$isTargetServer()) return;

		events.entrySet().removeIf(entry -> {
			if (!isVanillaBossBar(entry.getValue().getName())) {
				adfilter$hidden.put(entry.getKey(), entry.getValue());
				return true;
			}
			return false;
		});
	}

	@Inject(method = "extractRenderState", at = @At("RETURN"))
	private void adfilter$restoreHidden(GuiGraphicsExtractor graphics, CallbackInfo ci) {
		if (!adfilter$hidden.isEmpty()) {
			events.putAll(adfilter$hidden);
			adfilter$hidden.clear();
		}
	}

	private static boolean isVanillaBossBar(Component name) {
		if (name == null) return false;
		if (name.getContents() instanceof TranslatableContents translatable) {
			String key = translatable.getKey();
			return "entity.minecraft.ender_dragon".equals(key)
					|| "entity.minecraft.wither".equals(key);
		}
		return false;
	}

	private static boolean adfilter$isTargetServer() {
		ServerData server = Minecraft.getInstance().getCurrentServer();
		if (server == null || server.ip == null) return false;

		String host = server.ip.trim().toLowerCase(Locale.ROOT);
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
