package illager.guardillagers.init;

import com.google.common.collect.Lists;
import illager.guardillagers.GuardIllagers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = GuardIllagers.MODID)
public class IllagerSoundsRegister {
    private static List<SoundEvent> sounds = Lists.newArrayList();
    public static final SoundEvent GUARDILLAGER_AMBIENT = create("mob.guardillager.ambient");
    public static final SoundEvent GUARDILLAGER_ANGRY = create("mob.guardillager.angry");
    public static final SoundEvent GUARDILLAGER_HURT = create("mob.guardillager.hurt");
    public static final SoundEvent GUARDILLAGER_DIE = create("mob.guardillager.die");
    public static final SoundEvent GUARDILLAGER_STEP = create("mob.guardillager.step");


    private static SoundEvent create(String name) {

        ResourceLocation id = new ResourceLocation(GuardIllagers.MODID, name);
        SoundEvent sound = new SoundEvent(id);

        sound.setRegistryName(id);

        sounds.add(sound);
        return sound;
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> registry) {
        for (SoundEvent sound : sounds) {

            registry.getRegistry().register(sound);

        }
    }
}