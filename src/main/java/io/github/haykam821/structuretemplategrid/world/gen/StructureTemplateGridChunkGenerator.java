package io.github.haykam821.structuretemplategrid.world.gen;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.haykam821.structuretemplategrid.layout.StructureTemplateInfo;
import io.github.haykam821.structuretemplategrid.layout.StructureTemplateLayout;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.YLevels;
import net.minecraft.world.gen.GenerationStep.Carver;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

public class StructureTemplateGridChunkGenerator extends ChunkGenerator {
	public static final Codec<StructureTemplateGridChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			RegistryOps.getEntryCodec(BiomeKeys.THE_VOID)
		).apply(instance, instance.stable(StructureTemplateGridChunkGenerator::new));
	});

	private static final int SPACING = Math.max(StructureBlockBlockEntity.field_31364, StructureBlockBlockEntity.field_31365) + SharedConstants.CHUNK_WIDTH;
	private static final int SPACING_CHUNKS = MathHelper.ceilDiv(SPACING, SharedConstants.CHUNK_WIDTH);

	private static final int Y_POSITION = 64;

	private static final BlockState STRUCTURE_BLOCK = Blocks.STRUCTURE_BLOCK.getDefaultState()
		.with(StructureBlock.MODE, StructureBlockMode.SAVE);

	private static final BlockState BARRIER = Blocks.BARRIER.getDefaultState();
	private static final BlockState STRUCTURE_VOID = Blocks.STRUCTURE_VOID.getDefaultState();

	private StructureTemplateLayout layout;

	public StructureTemplateGridChunkGenerator(RegistryEntry.Reference<Biome> biomeEntry) {
		super(new FixedBiomeSource(biomeEntry));
	}

	@Override
	public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
		ChunkPos pos = chunk.getPos();

		if (pos.x < 0) return;
		if (pos.x % SPACING_CHUNKS != 0) return;

		if (pos.z < 0) return;
		if (pos.z % SPACING_CHUNKS != 0) return;

		int x = pos.x / SPACING_CHUNKS;
		int z = pos.z / SPACING_CHUNKS;

		StructureTemplateManager manager = world.getServer().getStructureTemplateManager();

		if (this.layout == null) {
			this.layout = StructureTemplateLayout.load(manager);
		}

		StructureTemplateInfo info = this.layout.getTemplate(x, z);

		if (info != null) {
			StructureTemplate template = info.template();
			Vec3i size = template.getSize();

			int offsetX = (SharedConstants.CHUNK_WIDTH - size.getX()) / 2;
			int offsetZ = (SharedConstants.CHUNK_WIDTH - size.getZ()) / 2;

			BlockPos origin = pos.getBlockPos(offsetX, Y_POSITION, offsetZ);
			BlockPos.Mutable blockPos = new BlockPos.Mutable(origin.getX(), origin.getY() - 1, origin.getZ());

			for (int dy = -1; dy < size.getY(); dy += 1) {
				blockPos.setY(origin.getY() + dy);

				for (int dx = 0; dx < size.getX(); dx += 1) {
					blockPos.setX(origin.getX() + dx);

					for (int dz = 0; dz < size.getZ(); dz += 1) {
						blockPos.setZ(origin.getZ() + dz);

						if (dy == -1) {
							if (dx == 0 && dz == 0) {
								world.setBlockState(blockPos, STRUCTURE_BLOCK, Block.NOTIFY_LISTENERS);

								world.getBlockEntity(blockPos, BlockEntityType.STRUCTURE_BLOCK).ifPresent(structureBlock -> {
									structureBlock.setTemplateName(info.id());
									structureBlock.setSize(size);
									structureBlock.setIgnoreEntities(false);
								});
							} else {
								world.setBlockState(blockPos, BARRIER, Block.NOTIFY_LISTENERS);
							}
						} else {
							world.setBlockState(blockPos, STRUCTURE_VOID, Block.NOTIFY_LISTENERS);
						}
					}
				}
			}

			template.place(world, origin, BlockPos.ORIGIN, new StructurePlacementData(), world.getRandom(), 0);
		}
	}

	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
		return;
	}

	@Override
	public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, Carver carverStep) {
		return;
	}

	@Override
	protected Codec<? extends StructureTemplateGridChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
		return new VerticalBlockSample(0, new BlockState[0]);
	}

	@Override
	public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
		return;
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
		return 0;
	}

	@Override
	public int getMinimumY() {
		return 0;
	}

	@Override
	public int getSeaLevel() {
		return 63;
	}

	@Override
	public int getWorldHeight() {
		return YLevels.OVERWORLD_GENERATION_HEIGHT;
	}

	@Override
	public void populateEntities(ChunkRegion region) {
		return;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
		return CompletableFuture.completedFuture(chunk);
	}
}
