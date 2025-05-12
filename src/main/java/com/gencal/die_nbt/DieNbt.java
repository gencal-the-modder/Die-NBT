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
            LOGGER.info("[Die, NBT!] The items that should be cleared from NBT data on death are:");
            Config.itemsToUntag.forEach((item) -> LOGGER.info(item.toString()));
            LOGGER.info("[Die, NBT!] The mods from which items that should be cleared from NBT data on death are:");
            Config.modsToUntag.forEach((mod) -> LOGGER.info(mod));
        }
    }

    public static boolean shouldItemsNbtDie(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return (
                Config.itemsToUntag.contains(item) ||
                        (!Config.modsToUntag.isEmpty() &&
                                Config.modsToUntag.contains(
                                        BuiltInRegistries.ITEM.getKey(item).getNamespace()
                                )
                        )
        );
    }
}
