package illager.guardillagers.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;

/**
 * ModelGuardHat - Undefined
 * Created using Tabula 7.0.0
 */
public class ModelGuardHat extends ModelBiped {
    public ModelRenderer Hat1;
    public ModelRenderer Hat2;
    public ModelRenderer Hat3;

    public ModelGuardHat() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.Hat1 = new ModelRenderer(this, 0, 0);
        this.Hat1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Hat1.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.Hat3 = new ModelRenderer(this, -16, 16);
        this.Hat3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Hat3.addBox(-8.0F, -3.0F, -8.0F, 16, 0, 16, 0.0F);
        this.Hat2 = new ModelRenderer(this, 32, 0);
        this.Hat2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Hat2.addBox(-4.0F, -8.14F, -4.0F, 8, 8, 8, 0.5F);
        this.Hat1.addChild(Hat2);
        this.Hat1.addChild(Hat3);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        GlStateManager.pushMatrix();
        if (entity.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }
        GlStateManager.scale(1.05F, 1.05F, 1.05F);
        this.Hat1.render(scale);
        GlStateManager.popMatrix();
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        setAngle(Hat1, this.bipedHead);
        if (entityIn instanceof EntityArmorStand) {
            EntityArmorStand entityarmorstand = (EntityArmorStand) entityIn;
            this.Hat1.rotateAngleX = ((float) Math.PI / 180F) * entityarmorstand.getHeadRotation().getX();
            this.Hat1.rotateAngleY = ((float) Math.PI / 180F) * entityarmorstand.getHeadRotation().getY();
            this.Hat1.rotateAngleZ = ((float) Math.PI / 180F) * entityarmorstand.getHeadRotation().getZ();
        }
    }

    protected void setAngle(ModelRenderer modelRenderer, ModelRenderer modelRenderer2) {
        modelRenderer.rotationPointX = modelRenderer2.rotationPointX;
        modelRenderer.rotationPointY = modelRenderer2.rotationPointY;
        modelRenderer.rotationPointZ = modelRenderer2.rotationPointZ;

        modelRenderer.rotateAngleX = modelRenderer2.rotateAngleX;
        modelRenderer.rotateAngleY = modelRenderer2.rotateAngleY;
        modelRenderer.rotateAngleZ = modelRenderer2.rotateAngleZ;
    }

    @Override
    public void setModelAttributes(ModelBase model) {
        super.setModelAttributes(model);

        if (model instanceof ModelBiped) {
            ModelBiped modelbiped = (ModelBiped) model;

            this.leftArmPose = modelbiped.leftArmPose;
            this.rightArmPose = modelbiped.rightArmPose;
            this.isSneak = modelbiped.isSneak;
            this.isChild = modelbiped.isChild;
            this.isRiding = modelbiped.isRiding;
            this.swingProgress = modelbiped.swingProgress;
        }
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
