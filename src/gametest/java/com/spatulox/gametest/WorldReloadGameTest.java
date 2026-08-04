package com.spatulox.gametest;

import com.spatulox.ExtendedTimePotion;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.world.TestWorldSave;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Lance le jeu, cree une partie, s'y connecte, la quitte, la rouvre et verifie
 * que les potions du mod sont toujours la.
 */
public class WorldReloadGameTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		final TestWorldSave save;
		final int potionsALaCreation;

		// 1. nouvelle partie + connexion
		try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
			save = singleplayer.getWorldSave();
			singleplayer.getClientLevel().waitForChunksRender();

			potionsALaCreation = countModPotions(singleplayer);
			if (potionsALaCreation == 0) {
				throw new AssertionError("aucune potion du mod enregistree a la creation");
			}
			assertPotionPresent(singleplayer, "long_long_night_vision");
			context.takeScreenshot("01-monde-cree");
		}
		// 2. sortie du try-with-resources = partie quittee, retour au menu

		// 3. rouvrir la meme sauvegarde
		try (TestSingleplayerContext singleplayer = save.open()) {
			singleplayer.getClientLevel().waitForChunksRender();

			int apresRechargement = countModPotions(singleplayer);
			if (apresRechargement != potionsALaCreation) {
				throw new AssertionError("nombre de potions change apres rechargement : "
						+ potionsALaCreation + " -> " + apresRechargement);
			}
			assertPotionPresent(singleplayer, "long_long_night_vision");
			context.takeScreenshot("02-monde-recharge");
		}
	}

	private static int countModPotions(TestSingleplayerContext singleplayer) {
		return singleplayer.getServer().computeOnServer(server ->
				(int) BuiltInRegistries.POTION.keySet().stream()
						.filter(id -> ExtendedTimePotion.MOD_ID.equals(id.getNamespace()))
						.count());
	}

	private static void assertPotionPresent(TestSingleplayerContext singleplayer, String path) {
		boolean present = singleplayer.getServer().computeOnServer(server ->
				BuiltInRegistries.POTION.containsKey(ExtendedTimePotion.id(path)));
		if (!present) {
			throw new AssertionError("potion absente du registre : " + path);
		}
	}
}
