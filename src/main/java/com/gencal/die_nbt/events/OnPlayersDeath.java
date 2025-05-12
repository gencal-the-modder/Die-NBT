package com.gencal.die_nbt.events;

import com.gencal.die_nbt.Config;
import com.gencal.die_nbt.DieNbt;
import static com.gencal.die_nbt.DieNbt.LOGGER;
import static com.gencal.die_nbt.DieNbt.shouldItemsNbtDie;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraft.server.level.ServerPlayer;

@Mod.EventBusSubscriber(modid = DieNbt.MODID)
public class OnPlayersDeath {

    @SubscribeEvent
    public static void clearNbtOnPlayersDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getInventory().items.forEach( itemStack -> {
                if (itemStack.hasTag() && shouldItemsNbtDie(itemStack)) {
                    if (Config.enableLogging) {
                        LOGGER.info("[Die NBT!] item {} was cleared from tags {}", itemStack, itemStack.getTag());
                    }
                    itemStack.setTag(new CompoundTag());
                }
            });
        }
    }

}
