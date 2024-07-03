package io.github.haykam821.structuretemplategrid.layout;

import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;

public record StructureTemplateInfo(Identifier id, StructureTemplate template) {
	public static StructureTemplateInfo load(StructureTemplateManager manager, Identifier id) {
		return new StructureTemplateInfo(id, manager.getTemplateOrBlank(id));
	}
}
