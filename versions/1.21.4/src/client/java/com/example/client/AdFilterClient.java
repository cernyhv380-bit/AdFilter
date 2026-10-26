package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;

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

	private static boolean isAd(Text message) {
		if (message == null) return false;
		String text = message.getString();
		String trimmed = text.trim();
		long now = System.currentTimeMillis();

		if (isAdLine(trimmed)) {
			lastAdTime = now;
			return true;
		}
		if (trimmed.isEmpty()) {
			if (now - lastAdTime < AD_TIMEOUT_MS) {
				lastAdTime = now;
				return true;
			}
			return false;
		}
		return false;
	}

	private static boolean isAdLine(String text) {
		if (text.isEmpty()) return false;
		if (text.contains("❏")) return true;
		if (text.contains("▬▬▬▬▬")) return true;
		if (text.contains("hypemc.pro")) return true;
		if (text.contains("Добро пожаловать на сервер HypeMC")) return true;
		if (text.contains("Донат навсегда и не пропадает")) return true;
		if (text.contains("Возможности и описание доната")) return true;
		if (text.contains("Покупка доната")) return true;
		if (text.contains("Напиши команду /donate")) return true;
		return false;
	}

}
