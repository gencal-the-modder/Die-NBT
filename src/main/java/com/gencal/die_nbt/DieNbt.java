package com.gencal.die_nbt;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.HashSet;

@Mod(DieNbt.MODID)
public class DieNbt
{
    public static final String MODID = "die_nbt";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DieNbt()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        if (Config.enableLogging) {
            LOGGER.info("[Die, NBT!] Logging was enabled!");
            LOGGER.info("[Die, NBT!] Mods from config: " + (Config.itemsToCheck.isEmpty() ? "" : Config.itemsToCheck.toString()));
            LOGGER.info("[Die, NBT!] Items from config: " + (Config.modsToCheck.isEmpty() ? "" : Config.modsToCheck.toString()));
            LOGGER.info("[Die, NBT!] Fields to clear by items: " + (Config.itemsToFields.isEmpty() ? "" : Config.itemsToFields.toString()));
            LOGGER.info("[Die, NBT!] Fields to clear by mods: " + (Config.modsToFields.isEmpty() ? "" : Config.modsToFields.toString()));
            LOGGER.info("[Die, NBT!] If you don't find some of the things you defined in config, that means that your config was incorrect");
        }
    }

    public static boolean shouldItemNbtDie(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return (
                Config.itemsToCheck.contains(item) ||
                Config.modsToCheck.contains(
                    BuiltInRegistries.ITEM.getKey(item).getNamespace()
                )
        );
    }

    public static HashSet<String> getFieldsToClear(ItemStack itemStack) {
        Item item = itemStack.getItem();
        Object fieldsByItem = Config.itemsToFields.get(item);
        Object fieldsByNamespace = Config.modsToFields.get(BuiltInRegistries.ITEM.getKey(item).getNamespace());
        if (fieldsByItem == null) {
            return (HashSet<String>) fieldsByNamespace;
        } else {
            return (HashSet<String>) fieldsByItem;
        }
    }
}
