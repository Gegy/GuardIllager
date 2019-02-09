package illager.guardillagers.event;

import com.google.common.collect.Lists;
import illager.guardillagers.entity.EntityGuardIllager;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.WoodlandMansion;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class EntityEventHandler {
    private static final Field mansionField = ReflectionHelper.findField(ChunkGeneratorOverworld.class,
            "woodlandMansionGenerator", "field_191060_C");
    private static final Method initializeMansionMethod = ReflectionHelper.findMethod(MapGenStructure.class,
            "initializeStructureData", "func_143027_a", World.class);
    private static final Field structMapField = ReflectionHelper.findField(MapGenStructure.class, "structureMap",
            "field_75053_d");

    private static final List<Biome.SpawnListEntry> SPAWN_ENEMIES = Lists.newArrayList();

    private final List<Biome.SpawnListEntry> spawnList = Lists.newArrayList();

    public EntityEventHandler() {
        this.spawnList.add(new Biome.SpawnListEntry(EntityGuardIllager.class, 10, 1, 2));
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityVillager) {
            EntityVillager villager = (EntityVillager) event.getEntity();

            villager.tasks.addTask(1, new EntityAIAvoidEntity<>(villager, EntityGuardIllager.class, 12.0F, 0.8D, 0.8D));
        }
    }

    @SubscribeEvent
    public void getPotentialSpawns(WorldEvent.PotentialSpawns event) {
        BlockPos pos = event.getPos();

        World world = event.getWorld();


        IChunkProvider prov = world.getChunkProvider();

        if (event.getType() == EnumCreatureType.MONSTER && prov instanceof ChunkProviderServer) {

            ChunkProviderServer serverProv = (ChunkProviderServer) prov;


            if (isInMansion(serverProv, world, pos)) {
                serverProv.getPossibleCreatures(EnumCreatureType.MONSTER, pos).addAll(SPAWN_ENEMIES);
            }

        }
    }

    static {
        SPAWN_ENEMIES.add(new Biome.SpawnListEntry(EntityGuardIllager.class, 1, 2, 4));
    }

    private boolean isInMansion(ChunkProviderServer prov, World world, BlockPos pos) {

        IChunkGenerator chunkGen = prov.chunkGenerator;
        if (chunkGen instanceof ChunkGeneratorOverworld) {

            ChunkGeneratorOverworld overworldGen = (ChunkGeneratorOverworld) chunkGen;


            try {

                WoodlandMansion mansionGen = (WoodlandMansion) mansionField.get(overworldGen);

                initializeMansionMethod.invoke(mansionGen, world);

                return getMansionAtIgnoreFlag(mansionGen, pos) != null;


            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {

                e.printStackTrace();

            }


        }
        return prov.chunkGenerator.isInsideStructure(world, "Mansion", pos); // Fallback

    }

    private StructureStart getMansionAtIgnoreFlag(MapGenStructure gen, BlockPos pos) {

        try {
            Long2ObjectMap<StructureStart> map = (Long2ObjectMap<StructureStart>) structMapField.get(gen);

            ObjectIterator objectiterator = map.values().iterator();
            label31:
            while (objectiterator.hasNext()) {

                StructureStart structurestart = (StructureStart) objectiterator.next();

                // Removed sizeableStructure check from the below condition.
                if (structurestart.getBoundingBox().isVecInside(pos)) {

                    Iterator<StructureComponent> iterator = structurestart.getComponents().iterator();
                    while (true) {

                        if (!iterator.hasNext()) {

                            continue label31;

                        }

                        StructureComponent structurecomponent = iterator.next();

                        if (structurecomponent.getBoundingBox().isVecInside(pos)) {
                            break;
                        }

                    }

                    return structurestart;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
