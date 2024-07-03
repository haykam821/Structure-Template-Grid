package io.github.haykam821.structuretemplategrid.world.gen;

import io.github.haykam821.structuretemplategrid.StructureTemplateGrid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class StructureTemplateGridChunkGenerators {
	private static final Identifier STRUCTURE_TEMPLATE_GRID_ID = StructureTemplateGrid.id("structure_template_grid");

	private StructureTemplateGridChunkGenerators() {
		return;
	}

	public static void register() {
		Registry.register(Registries.CHUNK_GENERATOR, STRUCTURE_TEMPLATE_GRID_ID, StructureTemplateGridChunkGenerator.CODEC);
	}
}
