package illager.guardillagers.event;

import com.google.common.collect.ImmutableMultimap;
import illager.guardillagers.GuardIllagers;
import illager.guardillagers.entity.EntityGuardIllager;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.WoodlandMansionPieces;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = GuardIllagers.MODID)
public class GuardSpawnHandler {
    private static final Logger LOGGER = LogManager.getLogger(GuardSpawnHandler.class);

    private static final ImmutableMultimap<String, SpawnEntry> SPAWN_ENTRIES = ImmutableMultimap.of(
            "entrance", new SpawnEntry(EntityGuardIllager::new, 2, 3),
            "1x2_c_stairs", new SpawnEntry(EntityGuardIllager::new, 2, 2),
            "1x2_d_stairs", new SpawnEntry(EntityGuardIllager::new, 2, 2)
    );

    private static final float SPAWN_SEARCH_ATTEMPT_FACTOR = 0.1F;

    private static final Field MANSION_FIELD = ReflectionHelper.findField(ChunkGeneratorOverworld.class, "woodlandMansionGenerator", "field_191060_C");
    private static final Field STRUCT_MAP_FIELD = ReflectionHelper.findField(MapGenStructure.class, "structureMap", "field_75053_d");
    private static final Field TEMPLATE_NAME_FIELD = ReflectionHelper.findField(WoodlandMansionPieces.MansionTemplate.class, "templateName", "field_191082_d");

    @Nullable
    @SuppressWarnings("unchecked")
    private static Long2ObjectMap<StructureStart> getStructureMap(MapGenStructure generator) {
        try {
            return (Long2ObjectMap<StructureStart>) STRUCT_MAP_FIELD.get(generator);
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Failed to get structure map on {}", generator, e);
            return null;
        }
    }

    @Nullable
    private static MapGenStructure getMansionGenerator(WorldServer world) {
        ChunkProviderServer chunkProvider = world.getChunkProvider();
        if (chunkProvider.chunkGenerator instanceof ChunkGeneratorOverworld) {
            try {
                return (MapGenStructure) MANSION_FIELD.get(chunkProvider.chunkGenerator);
            } catch (ReflectiveOperationException e) {
                LOGGER.error("Failed to get mansion generator on {}", chunkProvider.chunkGenerator, e);
            }
        }
        return null;
    }

    @Nullable
    private static String getTemplateName(StructureComponent component) {
        if (component instanceof WoodlandMansionPieces.MansionTemplate) {
            try {
                return (String) TEMPLATE_NAME_FIELD.get(component);
            } catch (ReflectiveOperationException e) {
                LOGGER.error("Failed to get template name for {}", component, e);
            }
        }
        return null;
    }

    @SubscribeEvent
    public static void onPostPopulate(PopulateChunkEvent.Post event) {
        if (!(event.getWorld() instanceof WorldServer)) {
            return;
        }

        WorldServer world = (WorldServer) event.getWorld();

        MapGenStructure mansionGenerator = getMansionGenerator(world);
        if (mansionGenerator == null) {
            return;
        }

        Long2ObjectMap<StructureStart> structureMap = getStructureMap(mansionGenerator);
        if (structureMap == null) {
            return;
        }

        ChunkPos chunkPos = new ChunkPos(event.getChunkX(), event.getChunkZ());
        StructureBoundingBox chunkBounds = new StructureBoundingBox(chunkPos.getXStart(), chunkPos.getZStart(), chunkPos.getXEnd(), chunkPos.getZEnd());

        Stream<StructureStart> intersectingStructures = structureMap.values().stream()
                .filter(structure -> structure.getBoundingBox().intersectsWith(chunkBounds));

        intersectingStructures.forEach(structure -> {
            Stream<StructureComponent> intersectingComponents = structure.getComponents().stream()
                    .filter(component -> component.getBoundingBox().intersectsWith(chunkBounds))
                    .filter(component -> isComponentFullyGenerated(world, component));

            StructureBoundingBox structureBounds = structure.getBoundingBox();
            intersectingComponents.forEach(component -> populateComponent(world, structureBounds, component));
        });
    }

    private static boolean isComponentFullyGenerated(World world, StructureComponent component) {
        StructureBoundingBox boundingBox = component.getBoundingBox();
        ChunkPos minChunkPos = chunkFromBlock(boundingBox.minX, boundingBox.minZ);
        ChunkPos maxChunkPos = chunkFromBlock(boundingBox.maxX, boundingBox.maxZ);
        for (int chunkZ = minChunkPos.z; chunkZ <= maxChunkPos.z; chunkZ++) {
            for (int chunkX = minChunkPos.x; chunkX <= maxChunkPos.x; chunkX++) {
                if (!isChunkPopulated(world, chunkX, chunkZ)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isChunkPopulated(World world, int chunkX, int chunkZ) {
        BlockPos pos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
        if (!world.isBlockLoaded(pos)) {
            return false;
        }

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        return chunk.isTerrainPopulated();
    }

    private static void populateComponent(World world, StructureBoundingBox structureBounds, StructureComponent component) {
        String templateName = getTemplateName(component);
        if (templateName == null) {
            return;
        }

        long seed = world.getSeed() ^ hash(structureBounds);
        Random random = new Random(seed);

        Collection<SpawnEntry> spawnEntries = SPAWN_ENTRIES.get(templateName);
        for (SpawnEntry spawnEntry : spawnEntries) {
            spawnGroup(world, random, structureBounds, component, spawnEntry);
        }
    }

    private static void spawnGroup(World world, Random random, StructureBoundingBox structureBounds, StructureComponent component, SpawnEntry spawnEntry) {
        BlockPos structureCenter = new BlockPos(
                (structureBounds.maxX + structureBounds.minX) / 2,
                (structureBounds.maxY + structureBounds.minY) / 2,
                (structureBounds.maxZ + structureBounds.minZ) / 2
        );
        int structureRadius = Math.max(
                Math.max(structureBounds.getXSize(), structureBounds.getYSize()),
                structureBounds.getZSize()
        ) / 2;

        int groupSize = random.nextInt(spawnEntry.groupMax - spawnEntry.groupMin + 1) + spawnEntry.groupMin;

        for (int i = 0; i < groupSize; i++) {
            Entity entity = spawnEntry.create(world);
            BlockPos spawnLocation = tryFindSpawnLocationIn(world, random, entity, component.getBoundingBox());
            if (spawnLocation == null) {
                return;
            }

            float yaw = random.nextFloat() * 360.0F;
            entity.setPositionAndRotation(spawnLocation.getX() + 0.5, spawnLocation.getY(), spawnLocation.getZ() + 0.5, yaw, 0.0F);
            world.spawnEntity(entity);

            if (entity instanceof EntityCreature) {
                ((EntityCreature) entity).setHomePosAndDistance(structureCenter, structureRadius);
            }
        }
    }

    @Nullable
    private static BlockPos tryFindSpawnLocationIn(World world, Random random, Entity entity, StructureBoundingBox bounds) {
        int floorArea = bounds.getXSize() * bounds.getZSize();
        int attempts = MathHelper.ceil(floorArea * SPAWN_SEARCH_ATTEMPT_FACTOR);

        for (int i = 0; i < attempts; i++) {
            BlockPos pos = randomPositionIn(bounds, random);

            if (world.isAirBlock(pos)) {
                BlockPos floor = findFloor(world, pos, bounds.minY);
                if (floor == null) {
                    continue;
                }

                BlockPos spawnPos = floor.up();

                AxisAlignedBB boundsInPlace = getEntityBoundsAt(entity, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                if (world.collidesWithAnyBlock(boundsInPlace)) {
                    continue;
                }

                return spawnPos;
            }
        }

        return null;
    }

    private static AxisAlignedBB getEntityBoundsAt(Entity entity, double x, double y, double z) {
        float width = entity.width / 2.0F;
        float height = entity.height;

        return new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
    }

    @Nullable
    private static BlockPos findFloor(World world, BlockPos pos, int minY) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(pos);
        while (mutablePos.getY() > minY) {
            mutablePos.move(EnumFacing.DOWN);
            if (world.isSideSolid(mutablePos, EnumFacing.UP)) {
                return mutablePos.toImmutable();
            }
        }

        return null;
    }

    private static BlockPos randomPositionIn(StructureBoundingBox bounds, Random random) {
        return new BlockPos(
                bounds.minX + random.nextInt(bounds.getXSize()),
                bounds.minY + random.nextInt(bounds.getYSize()),
                bounds.minZ + random.nextInt(bounds.getZSize())
        );
    }

    private static ChunkPos chunkFromBlock(int x, int z) {
        return new ChunkPos(x >> 4, z >> 4);
    }

    private static long hash(StructureBoundingBox bounds) {
        long min = (bounds.minY + bounds.minZ * 31) * 31 + bounds.minX;
        long max = (bounds.maxY + bounds.maxZ * 31) * 31 + bounds.maxX;
        return min * 31 + max;
    }

    private static class SpawnEntry {
        private final Function<World, Entity> constructor;

        private final int groupMin;
        private final int groupMax;

        private SpawnEntry(Function<World, Entity> constructor, int groupMin, int groupMax) {
            this.constructor = constructor;
            this.groupMin = groupMin;
            this.groupMax = groupMax;
        }

        public Entity create(World world) {
            return this.constructor.apply(world);
        }
    }
}
