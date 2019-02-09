package illager.guardillagers.init;

import illager.guardillagers.GuardIllagers;
import illager.guardillagers.item.ItemGuardHelm;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collections;
import java.util.List;

public class IllagerItems {
    private static final NonNullList<Item> ITEMS = NonNullList.create();

    public static final ItemArmor.ArmorMaterial MATERIAL_GUARD_HELM = EnumHelper.addArmorMaterial("guard_helm", GuardIllagers.MODID + ":" + "textures/models/armor/kabanhat_layer_1.png", 10, new int[]{2, 2, 3, 2}, 15, net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0).setRepairItem(new ItemStack(Items.LEATHER));


    public static final Item GUARD_HELMET = new ItemGuardHelm(MATERIAL_GUARD_HELM, 0, EntityEquipmentSlot.HEAD);

    public static List<Item> getItems() {
        return Collections.unmodifiableList(ITEMS);
    }


    public static void register(IForgeRegistry<Item> registry, Item item) {
        ITEMS.add(item);

        if (item instanceof ItemBlock && item.getRegistryName() == null) {
            item.setRegistryName(((ItemBlock) item).getBlock().getRegistryName());
        }

        registry.register(item);
    }


    public static void registerItems(IForgeRegistry<Item> registry) {
        register(registry, GUARD_HELMET.setRegistryName("guard_helm"));
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        registerModel(GUARD_HELMET);
    }

    @SideOnly(Side.CLIENT)
    public static void registerModel(Item item, String modelName) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(GuardIllagers.MODID + ":" + modelName, "inventory"));
    }

    @SideOnly(Side.CLIENT)
    public static void registerModel(Item item) {
        registerModel(item, item.getRegistryName().getResourcePath());
    }

}
