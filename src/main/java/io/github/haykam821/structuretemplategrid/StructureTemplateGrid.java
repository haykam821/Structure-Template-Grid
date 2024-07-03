package io.github.haykam821.structuretemplategrid;

import io.github.haykam821.structuretemplategrid.world.gen.StructureTemplateGridChunkGenerators;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class StructureTemplateGrid implements ModInitializer {
	private static final String MOD_ID = "structuretemplategrid";

	@Override
	public void onInitialize() {
		StructureTemplateGridChunkGenerators.register();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
