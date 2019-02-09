package illager.guardillagers.item;

import illager.guardillagers.GuardIllagers;
import illager.guardillagers.client.model.ModelGuardHat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGuardHelm extends ItemArmor {
    protected static final ResourceLocation HAT_TEX = new ResourceLocation(GuardIllagers.MODID, "textures/overlay/guard_perspective.png");

    public ItemGuardHelm(ArmorMaterial materialIn, int render, EntityEquipmentSlot slots) {
        super(materialIn, render, slots);
        this.setUnlocalizedName("guard_helm");
    }


    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        return new ModelGuardHat();
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return GuardIllagers.MODID + ":" + "textures/armor/guard_hat.png";
    }

    @Override
    public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, ScaledResolution res, float partialTicks) {

        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        Minecraft.getMinecraft().getTextureManager().bindTexture(HAT_TEX);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0D, (double) res.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos((double) res.getScaledWidth(), (double) res.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos((double) res.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}