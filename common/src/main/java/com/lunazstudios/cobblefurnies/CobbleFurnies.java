package com.lunazstudios.cobblefurnies;

import com.lunazstudios.cobblefurnies.registry.CFBlockEntityTypes;
import com.lunazstudios.cobblefurnies.registry.CFBlockTags;
import com.lunazstudios.cobblefurnies.registry.CFBlocks;
import net.minecraft.resources.ResourceLocation;

public final class CobbleFurnies {
    public static final String MOD_ID = "cobblefurnies";

    public static void init() {
        CFBlocks.init();
        CFBlockTags.init();
        CFBlockEntityTypes.init();
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
