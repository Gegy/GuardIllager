package illager.guardillagers.init;

import illager.guardillagers.GuardIllagers;
import illager.guardillagers.entity.EntityGuardIllager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class IllagerEntityRegistry {

    public static void registerEntity(RegistryEvent.Register<EntityEntry> event) {
        EntityRegistry.registerModEntity(new ResourceLocation(GuardIllagers.MODID, "guard_illager"), EntityGuardIllager.class, "GuardIllager", 1, GuardIllagers.instance, 80, 3, false, 9804699, 0x879C9B);
    }

}