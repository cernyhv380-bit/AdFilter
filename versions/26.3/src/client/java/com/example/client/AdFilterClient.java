package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.network.chat.Component;

public class AdFilterClient implements ClientModInitializer {
	private static long lastAdTime = 0L;
	private static final long AD_TIMEOUT_MS = 1500L;

	@Override
	public void onInitializeClient() {
		ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
			if (overlay) return true;
			return !isAd(message);
		});

		ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signedMessage, sender, type, timestamp) -> {
			if (sender == null && isAd(message)) return false;
			return true;
		});
	}

	private static boolean isAd(Component message) {
		if (message == null) return false;
		String trimmed = message.getString().trim();
		long now = System.currentTimeMillis();

		if (isAdLine(trimmed)) {
			lastAdTime = now;
			return true;
		}
		if (trimmed.isEmpty() && now - lastAdTime < AD_TIMEOUT_MS) {
			lastAdTime = now;
			return true;
		}
		return false;
	}

	private static boolean isAdLine(String text) {
		return !text.isEmpty() && (text.contains("❏")
				|| text.contains("▬▬▬▬▬")
				|| text.contains("hypemc.pro")
				|| text.contains("Добро пожаловать на сервер HypeMC")
				|| text.contains("Донат навсегда и не пропадает")
				|| text.contains("Возможности и описание доната")
				|| text.contains("Покупка доната")
				|| text.contains("Напиши команду /donate"));
	}
}
