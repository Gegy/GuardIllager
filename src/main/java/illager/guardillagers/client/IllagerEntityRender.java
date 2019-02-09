package illager.guardillagers.client;

import illager.guardillagers.client.render.RenderGuardIllager;
import illager.guardillagers.entity.EntityGuardIllager;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IllagerEntityRender {
    public static void entityRender() {
        RenderingRegistry.registerEntityRenderingHandler(EntityGuardIllager.class,RenderGuardIllager::new);
    }
}
