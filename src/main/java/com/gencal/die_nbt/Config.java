package com.gencal.die_nbt;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = DieNbt.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue ENABLE_LOGGING = BUILDER
            .comment("Whether 'Die NBT!' should log its actions")
            .define("enableLogging", false);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEMS_TO_UNTAG_STRINGS = BUILDER
            .comment("A list of items that should lose NBT tags on players death.")
            .defineListAllowEmpty("itemsToUntag", List.of(), Config::validateItemName);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> MODS_TO_UNTAG_STRINGS = BUILDER
            .comment("A list of mods from which items should lose NBT tags on players death.")
            .defineListAllowEmpty("mods_to_untag", List.of(), Config::doesNamespaceExist);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean enableLogging;
    public static Set<Item> itemsToUntag;
    public static Set<String> modsToUntag;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    private static boolean doesNamespaceExist(final Object obj) {
        return obj instanceof final String namespace && ModList.get().isLoaded(namespace);
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        enableLogging = ENABLE_LOGGING.get();

        // convert the list of strings into a set of items
        itemsToUntag = ITEMS_TO_UNTAG_STRINGS.get().stream()
                .map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
                .collect(Collectors.toSet());

        modsToUntag = new HashSet<>(MODS_TO_UNTAG_STRINGS.get());

    }
}
