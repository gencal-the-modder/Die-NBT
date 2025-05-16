package com.gencal.die_nbt;

import com.gencal.die_nbt.util.ConfigUtils;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.*;


@Mod.EventBusSubscriber(modid = DieNbt.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue ENABLE_LOGGING = BUILDER
            .comment("Whether 'Die NBT!' should log its actions")
            .define("enableLogging", false);

    private static final ForgeConfigSpec.BooleanValue ENABLE_INVENTORY = BUILDER
            .comment("Whether 'Die NBT!' should work on your inventory and offhand")
            .define("enableInventory", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_ARMOR_SLOTS = BUILDER
            .comment("Whether 'Die NBT!' should work on your armor slots")
            .define("enableArmorSlots", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_CURIOS = BUILDER
            .comment("Whether 'Die NBT!' should work on curios slots")
            .define("enableCurios", true);

    private static final ForgeConfigSpec.ConfigValue<List<? extends ArrayList<Object>>> ITEMS_AND_FIELDS_STRINGS = BUILDER
            .comment(
                    """
                    A list of keys - item names, and their values - NBT data fields, also known as tags, that should be erased on player's death.\
                    
                    Leave the value as an empty list if you want to completely erase NBT data from specific item\
                    
                    Please note that item-specific rules override mod-wide rules for that item\
                    
                    Example of the list: [ ["minecraft:diamond_sword", ["Damage", "Enchantments"]] ]
                    """
            )
            .defineListAllowEmpty("itemsToFields", List.of(), ConfigUtils::validateItemFieldPair);

    private static final ForgeConfigSpec.ConfigValue<List<? extends ArrayList<Object>>> MODS_AND_FIELDS_STRINGS = BUILDER
            .comment(
                    """
                    A list of keys - mod names, and their values - NBT data fields, also known as tags, that should be erased on player's death.\
                    
                    Leave the value as an empty list if you want to completely erase NBT data from specific mod items\
                    
                    Example of the list: [ ["minecraft", ["Damage", "Enchantments"]] ]
                    """
            )
            .defineListAllowEmpty("modsToFields", List.of(), ConfigUtils::validateNamespaceFieldPair);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean enableLogging;
    public static boolean enableInventory;
    public static boolean enableArmorSlots;
    public static boolean enableCurios;
    public static Set<Item> itemsToCheck;
    public static Set<String> modsToCheck;
    public static HashMap<Item, Set<String>> itemsToFields;
    public static HashMap<String, Set<String>> modsToFields;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        enableLogging = ENABLE_LOGGING.get();

        enableInventory = ENABLE_INVENTORY.get();

        enableArmorSlots = ENABLE_ARMOR_SLOTS.get();

        enableCurios = ENABLE_CURIOS.get();

        itemsToCheck = ConfigUtils.collectItemsFromPairs(ITEMS_AND_FIELDS_STRINGS.get());

        modsToCheck = ConfigUtils.collectNamespacesFromPairs(MODS_AND_FIELDS_STRINGS.get());

        itemsToFields = ConfigUtils.mergeItemToFieldsMaps(ITEMS_AND_FIELDS_STRINGS.get());

        modsToFields = ConfigUtils.mergeStringToFieldsMaps(MODS_AND_FIELDS_STRINGS.get());
    }
}
