package com.lunazstudios.cobblefurnies.client;

import com.lunazstudios.cobblefurnies.client.renderer.block.StatueBlockRenderer;
import com.lunazstudios.cobblefurnies.registry.CFBlockEntityTypes;
import com.lunazstudios.cobblefurnies.registry.CFRegistry;

public class CobbleFurniesClient {

    public static void init() {
        CFRegistry.registerBlockEntityRenderer(CFBlockEntityTypes.STATUE, StatueBlockRenderer::new);
    }
}
