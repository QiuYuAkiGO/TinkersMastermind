package net.qiuyu.tinkersmastermind.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.qiuyu.tinkersmastermind.TinkersMastermind;
import net.qiuyu.tinkersmastermind.register.ModBlockEntities;

@Mod.EventBusSubscriber(modid = TinkersMastermind.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TinkersMastermindClient {
    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.FORGING_TABLE.get(), ForgingTableRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MODULAR_DISPLAY_FRAME.get(), ModularDisplayFrameRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(NetherFortressCompassItemProperties::register);
    }
}
