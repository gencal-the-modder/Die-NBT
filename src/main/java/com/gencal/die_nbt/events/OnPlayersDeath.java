package com.gencal.die_nbt.events;

import com.gencal.die_nbt.Config;
import com.gencal.die_nbt.DieNbt;
import static com.gencal.die_nbt.DieNbt.LOGGER;
import static com.gencal.die_nbt.DieNbt.shouldItemNbtDie;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraft.server.level.ServerPlayer;
import top.theillusivec4.curios.common.CuriosHelper;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.HashSet;
import java.util.Map;

@Mod.EventBusSubscriber(modid = DieNbt.MODID)
public class OnPlayersDeath {

    private static void clearItemsFromNbt(ItemStack itemStack) {
        if (itemStack.hasTag() && shouldItemNbtDie(itemStack)) {
            HashSet<String> fieldsToClear = DieNbt.getFieldsToClear(itemStack);
            fieldsToClear.retainAll(itemStack.getTag().getAllKeys());

            if (Config.enableLogging) {
                if (fieldsToClear.isEmpty()) {
                    LOGGER.info("[Die NBT!] item {} was cleared from all tags", itemStack);
                } else {
                    LOGGER.info("[Die NBT!] item {} was cleared from tags {}", itemStack, fieldsToClear);
                }
            }

            if (fieldsToClear.isEmpty()) {
                itemStack.setTag(new CompoundTag());
            } else {
                fieldsToClear.forEach(itemStack::removeTagKey);
            }
        }
    }

    @SubscribeEvent
    public static void clearNbtOnPlayersDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {

            if (Config.enableInventory) {
                player.getInventory().items.forEach(OnPlayersDeath::clearItemsFromNbt);
                clearItemsFromNbt(player.getOffhandItem());
            }

            if (Config.enableArmorSlots) {
                player.getAllSlots().forEach(itemStack -> {
                    if (itemStack != player.getOffhandItem()) {
                        clearItemsFromNbt(itemStack);
                    }
                });
            }

            if (Config.enableCurios && ModList.get().isLoaded("curios")) {
                Map<String, ICurioStacksHandler> curiosSlots = new CuriosHelper()
                        .getCuriosHandler(player)
                        .map(ICuriosItemHandler::getCurios)
                        .orElse(null);

                if (curiosSlots != null) {
                    curiosSlots.forEach((slotName, stacksHandler) -> {
                        IDynamicStackHandler stacks = stacksHandler.getStacks();
                        for (int i = 0; i < stacks.getSlots(); i++) {
                            if (stacks.getStackInSlot(i).hasTag()) {
                                clearItemsFromNbt(stacks.getStackInSlot(i));
                            }
                        }
                    });
                }
            }
        }
    }

}
