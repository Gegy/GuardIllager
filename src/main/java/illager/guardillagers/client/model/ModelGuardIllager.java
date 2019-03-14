package illager.guardillagers.client.model;

import illager.guardillagers.entity.EntityGuardIllager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelGuardIllager extends ModelBase {
    public ModelRenderer Head;
    public ModelRenderer Nose;
    public ModelRenderer HatLayer;
    public ModelRenderer HatFlap;
    public ModelRenderer Body;
    public ModelRenderer ChestPlate;
    public ModelRenderer RightLeg;
    public ModelRenderer LeftLeg;
    public ModelRenderer RightOpenArm;
    public ModelRenderer LeftOpenArm;
    public ModelRenderer MiddleClosedArm;
    public ModelRenderer Cape;
    public ModelRenderer RightClosedArm;
    public ModelRenderer LeftClosedArm;

    public ModelGuardIllager() {
        this.textureWidth = 64;
        this.textureHeight = 128;
        this.MiddleClosedArm = new ModelRenderer(this, 0, 56);
        this.MiddleClosedArm.setRotationPoint(0.0F, 3.0F, -1.0F);
        this.MiddleClosedArm.addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, 0.0F);
        this.setRotateAngle(MiddleClosedArm, -0.7853981633974483F, 0.0F, 0.0F);
        this.LeftLeg = new ModelRenderer(this, 0, 18);
        this.LeftLeg.mirror = true;
        this.LeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.LeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.RightClosedArm = new ModelRenderer(this, 48, 36);
        this.RightClosedArm.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.RightClosedArm.addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
        this.Nose = new ModelRenderer(this, 24, 0);
        this.Nose.setRotationPoint(0.0F, -3.0F, -4.0F);
        this.Nose.addBox(-1.0F, 0.0F, -2.0F, 2, 4, 2, 0.0F);
        this.LeftOpenArm = new ModelRenderer(this, 44, 18);
        this.LeftOpenArm.mirror = true;
        this.LeftOpenArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.LeftOpenArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        this.RightLeg = new ModelRenderer(this, 0, 18);
        this.RightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.RightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.ChestPlate = new ModelRenderer(this, 0, 36);
        this.ChestPlate.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ChestPlate.addBox(-4.0F, 0.0F, -3.0F, 8, 14, 6, 0.5F);
        this.Body = new ModelRenderer(this, 16, 18);
        this.Body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Body.addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, 0.0F);
        this.LeftClosedArm = new ModelRenderer(this, 48, 36);
        this.LeftClosedArm.mirror = true;
        this.LeftClosedArm.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.LeftClosedArm.addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F);
        this.Head = new ModelRenderer(this, 0, 0);
        this.Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Head.addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, 0.0F);
        this.HatFlap = new ModelRenderer(this, 16, 48);
        this.HatFlap.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.HatFlap.addBox(-8.0F, -4.9F, -8.0F, 16, 0, 16, 0.0F);
        this.RightOpenArm = new ModelRenderer(this, 44, 18);
        this.RightOpenArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.RightOpenArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        this.Cape = new ModelRenderer(this, 0, 64);
        this.Cape.setRotationPoint(0.0F, 0.0F, 3.6F);
        this.Cape.addBox(-4.0F, 0.0F, -1.0F, 8, 14, 1, 0.0F);
        this.setRotateAngle(Cape, 0.17453292519943295F, 0.0F, 0.0F);
        this.HatLayer = new ModelRenderer(this, 32, 0);
        this.HatLayer.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.HatLayer.addBox(-4.0F, -9.9F, -4.0F, 8, 10, 8, 0.5F);
        this.MiddleClosedArm.addChild(this.RightClosedArm);
        this.MiddleClosedArm.addChild(this.LeftClosedArm);
        this.Head.addChild(this.HatLayer);
        this.Head.addChild(this.HatFlap);
        this.Head.addChild(this.Nose);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float HeadPitch, float scale) {
        this.Body.render(scale);
        this.Head.render(scale);
        this.LeftLeg.render(scale);
        this.ChestPlate.render(scale);
        this.RightLeg.render(scale);
        this.Cape.render(scale);

        AbstractIllager abstractillager = (AbstractIllager) entity;
        if (abstractillager.getArmPose() == AbstractIllager.IllagerArmPose.CROSSED) {
            this.MiddleClosedArm.render(scale);
        } else {
            this.RightOpenArm.render(scale);
            this.LeftOpenArm.render(scale);
        }

        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, HeadPitch,scale, entity);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.Head.rotateAngleY = (float) Math.toRadians(netHeadYaw);
        this.Head.rotateAngleX = (float) Math.toRadians(headPitch);
        this.MiddleClosedArm.rotationPointY = 3.0F;
        this.MiddleClosedArm.rotationPointZ = -1.0F;
        this.MiddleClosedArm.rotateAngleX = -0.75F;
        this.RightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.LeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.RightLeg.rotateAngleY = 0.0F;
        this.LeftLeg.rotateAngleY = 0.0F;
        EntityGuardIllager guard = (EntityGuardIllager) entityIn;
        AbstractIllager.IllagerArmPose armPose = guard.getArmPose();
        if (armPose == AbstractIllager.IllagerArmPose.ATTACKING) {
            float f = MathHelper.sin(this.swingProgress * (float) Math.PI);
            float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
            this.RightOpenArm.rotateAngleZ = 0.0F;
            this.LeftOpenArm.rotateAngleZ = 0.0F;
            this.RightOpenArm.rotateAngleY = 0.15707964F;
            this.LeftOpenArm.rotateAngleY = -0.15707964F;
            if (guard.getPrimaryHand() == EnumHandSide.RIGHT) {
                if (guard.isDrinkingPotion()) {
                    this.RightOpenArm.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
                    this.LeftOpenArm.rotateAngleX = -1.0F;
                    this.LeftOpenArm.rotateAngleZ = -0.6F;
                    this.RightOpenArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
                    this.LeftOpenArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
                } else {
                    this.RightOpenArm.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
                    this.LeftOpenArm.rotateAngleX = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
                    this.RightOpenArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
                    this.LeftOpenArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
                }
            } else {
                if (guard.isDrinkingPotion()) {
                    this.RightOpenArm.rotateAngleZ = 0.6F;
                    this.RightOpenArm.rotateAngleX = 1.0F;
                    this.LeftOpenArm.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
                    this.RightOpenArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
                    this.LeftOpenArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
                }
                this.RightOpenArm.rotateAngleX = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
                this.LeftOpenArm.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
                this.RightOpenArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
                this.LeftOpenArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
            }

            this.RightOpenArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.LeftOpenArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            this.RightOpenArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
            this.LeftOpenArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        } else if (armPose == AbstractIllager.IllagerArmPose.BOW_AND_ARROW) {
            this.RightOpenArm.rotateAngleY = -0.1F + this.Head.rotateAngleY;
            this.RightOpenArm.rotateAngleX = (-(float) Math.PI / 2F) + this.Head.rotateAngleX;
            this.LeftOpenArm.rotateAngleX = -0.9424779F + this.Head.rotateAngleX;
            this.LeftOpenArm.rotateAngleY = this.Head.rotateAngleY - 0.4F;
            this.LeftOpenArm.rotateAngleZ = ((float) Math.PI / 2F);
        }

        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        double capeX = guard.prevCapeX + (guard.capeX - guard.prevCapeX) * partialTicks;
        double capeY = guard.prevCapeY + (guard.capeY - guard.prevCapeY) * partialTicks;
        double capeZ = guard.prevCapeZ + (guard.capeZ - guard.prevCapeZ) * partialTicks;
        double guardX = guard.prevPosX + (guard.posX - guard.prevPosX) * partialTicks;
        double guardY = guard.prevPosY + (guard.posY - guard.prevPosY) * partialTicks;
        double guardZ = guard.prevPosZ + (guard.posZ - guard.prevPosZ) * partialTicks;

        double deltaX = capeX - guardX;
        double deltaY = guardY - capeY;
        double deltaZ = capeZ - guardZ;

        double deltaXZ = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        this.Cape.rotateAngleX = (float) (Math.PI / 2 - MathHelper.atan2(deltaY, deltaXZ));
    }

    public ModelRenderer getArm(EnumHandSide handSide) {
        return handSide == EnumHandSide.LEFT ? this.LeftOpenArm : this.RightOpenArm;
    }

    public ModelRenderer crossHand() {
        return this.MiddleClosedArm;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
