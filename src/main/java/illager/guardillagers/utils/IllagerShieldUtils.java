package illager.guardillagers.utils;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.BannerPattern;

public class IllagerShieldUtils {
    private static ItemStack getIllagerBanner() {
        ItemStack banner = new ItemStack(Items.BANNER);

        addPattern(banner, BannerPattern.BASE, EnumDyeColor.WHITE);
        addPattern(banner, BannerPattern.RHOMBUS_MIDDLE, EnumDyeColor.CYAN);
        addPattern(banner, BannerPattern.STRIPE_BOTTOM, EnumDyeColor.SILVER);
        addPattern(banner, BannerPattern.STRIPE_CENTER, EnumDyeColor.GRAY);
        addPattern(banner, BannerPattern.STRIPE_MIDDLE, EnumDyeColor.BLACK);
        addPattern(banner, BannerPattern.HALF_HORIZONTAL, EnumDyeColor.SILVER);
        addPattern(banner, BannerPattern.CIRCLE_MIDDLE, EnumDyeColor.SILVER);
        return banner;
    }

    private static ItemStack addPattern(ItemStack banner, BannerPattern pattern, EnumDyeColor color) {

        NBTTagCompound nbt = banner.getTagCompound();
        if (nbt == null) {
            banner.setTagCompound(new NBTTagCompound());
            nbt = banner.getTagCompound();
        }

        NBTTagCompound tag;

        if (nbt.hasKey("BlockEntityTag")) {
            tag = nbt.getCompoundTag("BlockEntityTag");
        } else {
            tag = new NBTTagCompound();
            nbt.setTag("BlockEntityTag", tag);
        }

        NBTTagList patterns;

        if (tag.hasKey("Patterns")) {
            patterns = tag.getTagList("Patterns", 10);
        } else {
            patterns = new NBTTagList();
            tag.setTag("Patterns", patterns);
        }

        NBTTagCompound toAdd = new NBTTagCompound();
        toAdd.setInteger("Color", color.getDyeDamage());
        toAdd.setString("Pattern", pattern.getHashname());
        patterns.appendTag(toAdd);

        return banner;
    }

    public static ItemStack getIllagerShield() {


        ItemStack banner = getIllagerBanner();


        ItemStack shield = new ItemStack(Items.SHIELD, 1, 0);


        applyBanner(banner, shield);


        return shield;

    }


    private static void applyBanner(ItemStack banner, ItemStack shield) {


        NBTTagCompound bannerNBT = banner.getSubCompound("BlockEntityTag");

        NBTTagCompound shieldNBT = bannerNBT == null ? new NBTTagCompound() : bannerNBT.copy();

        shieldNBT.setInteger("Base", banner.getMetadata() & 15);

        shield.setTagInfo("BlockEntityTag", shieldNBT);

    }
}
