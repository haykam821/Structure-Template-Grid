package io.github.haykam821.structuretemplategrid.layout;

import java.util.List;

import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.MathHelper;

public class StructureTemplateLayout {
	private final List<StructureTemplateInfo> templates;

	private final int lengthX;
	private final int lengthZ;

	private StructureTemplateLayout(List<StructureTemplateInfo> templates) {
		this.templates = templates;

		this.lengthX = MathHelper.ceil(MathHelper.sqrt(templates.size()));
		this.lengthZ = MathHelper.ceil(templates.size() / (float) lengthX);
	}

	public StructureTemplateInfo getTemplate(int x, int z) {
		if (x < lengthX && z < lengthZ) {
			int index = MathHelper.abs(x * this.lengthX + z);

			if (index < templates.size()) {
				return this.templates.get(index);
			}
		}

		return null;
	}

	public int getLengthX() {
		return this.lengthX;
	}

	public int getLengthZ() {
		return this.lengthZ;
	}

	@Override
	public String toString() {
		return "StructureTemplateLayout[" + this.lengthX + "x" + this.lengthZ + "]";
	}

	public static StructureTemplateLayout load(StructureTemplateManager manager) {
		List<StructureTemplateInfo> templates = manager.streamTemplates()
			.sorted()
			.map(id -> StructureTemplateInfo.load(manager, id))
			.toList();

		return new StructureTemplateLayout(templates);
	}
}
