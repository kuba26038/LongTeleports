package dev.itsmeow.quickteleports.forge;

import dev.itsmeow.quickteleports.QuickTeleportsMod;
import dev.itsmeow.quickteleports.util.Teleport;
import net.minecraft.ChatFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;

@Mod(QuickTeleportsMod.MOD_ID)
@Mod.EventBusSubscriber(modid = QuickTeleportsMod.MOD_ID)
public class QuickTeleportsModForge {

    public static ServerConfig SERVER_CONFIG = null;
    private static ForgeConfigSpec SERVER_CONFIG_SPEC = null;
    static int TimerSeconds;


    public QuickTeleportsModForge() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (s, b) -> true));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_CONFIG_SPEC = specPair.getRight();
        SERVER_CONFIG = specPair.getLeft();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG_SPEC);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        QuickTeleportsMod.serverTick(ServerLifecycleHooks.getCurrentServer());


        //here i try to check if the countdown has started, not sure if it works; gotta check
            //the message appears every frame
            if(QuickTeleportsMod.CountdownTeleport == true) {
            QuickTeleportsMod.timer++;
            if(QuickTeleportsMod.timer % 20 == 0) {
                QuickTeleportsMod.sendMessage(QuickTeleportsMod.playerRequesting.createCommandSourceStack(), true, QuickTeleportsMod.ftc(ChatFormatting.GREEN, "Teleporting in " + Math.floor(QuickTeleportsMod.timer / 20) + " seconds..."));
               }
            }
        if(QuickTeleportsMod.timer == 1200){
        double posX = QuickTeleportsMod.playerRequesting.getX();
        double posY = QuickTeleportsMod.playerRequesting.getY();
        double posZ = QuickTeleportsMod.playerRequesting.getZ();
        //the change: check if the PlayerMoving is in the same dimension as playerRequesting
        if(QuickTeleportsMod.playerMoving.level.dimension() == QuickTeleportsMod.playerRequesting.level.dimension())
            QuickTeleportsMod.playerMoving.teleportTo(QuickTeleportsMod.playerRequesting.getLevel(), posX, posY, posZ, QuickTeleportsMod.playerRequesting.getYRot(), 0F);

        else { //gets executed when players are in the same dimension, easily fixable (note: fixed)
            QuickTeleportsMod.sendMessage(QuickTeleportsMod.playerRequesting.createCommandSourceStack(), true, QuickTeleportsMod.ftc(ChatFormatting.GREEN, "The Player isn't in the same dimension as you, you can't teleport to them!"));
            QuickTeleportsMod.sendMessage(QuickTeleportsMod.playerMoving.createCommandSourceStack(), true, QuickTeleportsMod.ftc(ChatFormatting.GREEN, "The Player isn't in the same dimension as you, you can't teleport to them!"));
        }
        QuickTeleportsMod.CountdownTeleport = false;
        QuickTeleportsMod.timer = 0;
        }

        }



    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        QuickTeleportsMod.registerCommands(event.getDispatcher());
    }

    public static class ServerConfig {
        public ForgeConfigSpec.Builder builder;
        public final ForgeConfigSpec.IntValue teleportRequestTimeout;

        ServerConfig(ForgeConfigSpec.Builder builder) {
            this.builder = builder;
            this.teleportRequestTimeout = builder.comment(QuickTeleportsMod.CONFIG_FIELD_COMMENT + " Place a copy of this config in the defaultconfigs/ folder in the main server/.minecraft directory (or make the folder if it's not there) to copy this to new worlds.").defineInRange(QuickTeleportsMod.CONFIG_FIELD_NAME, QuickTeleportsMod.CONFIG_FIELD_VALUE, QuickTeleportsMod.CONFIG_FIELD_MIN, QuickTeleportsMod.CONFIG_FIELD_MAX);
            builder.build();
        }
    }

}
