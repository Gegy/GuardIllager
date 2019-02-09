package illager.guardillagers.client.render;

import illager.guardillagers.GuardIllagers;
import illager.guardillagers.client.model.ModelGuardIllager;
import illager.guardillagers.client.render.layer.LayerHeldItemGuard;
import illager.guardillagers.entity.EntityGuardIllager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGuardIllager extends RenderLiving<EntityMob> {
    private static final ResourceLocation ILLAGER_TEXTURE = new ResourceLocation(GuardIllagers.MODID, "textures/entity/illager/guardillager.png");

    public RenderGuardIllager(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelGuardIllager(), 0.5F);
        this.addLayer(new LayerHeldItem(this) {
            public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
                if (((EntityGuardIllager)entitylivingbaseIn).isAggressive()) {
                    super.doRenderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
                }

            }

            protected void translateToHand(EnumHandSide p_191361_1_) {
                ((ModelGuardIllager)this.livingEntityRenderer.getMainModel()).getArm(p_191361_1_).postRender(0.0625F);
            }
        });
        this.addLayer(new LayerHeldItemGuard(this));
    }

    public ModelGuardIllager getMainModel() {
        return (ModelGuardIllager) super.getMainModel();
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityMob entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityMob entity) {
        return ILLAGER_TEXTURE;
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(EntityMob entitylivingbaseIn, float partialTickTime) {
        float f = 0.9375F;
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }
}