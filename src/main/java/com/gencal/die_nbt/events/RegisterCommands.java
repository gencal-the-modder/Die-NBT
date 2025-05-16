package com.gencal.die_nbt.events;

import com.gencal.die_nbt.DieNbt;
import com.gencal.die_nbt.commands.DieNbtCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DieNbt.MODID)
public class RegisterCommands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        DieNbtCommand.register(event.getDispatcher());
    }

}
