package com.lunazstudios.cobblefurnies.registry;

import com.lunazstudios.cobblefurnies.block.entity.StatueBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class CFBlockEntityTypes {
    public static final Supplier<BlockEntityType<StatueBlockEntity>> STATUE = CFRegistry.registerBlockEntityType("statue",
            () -> CFRegistry.createBlockEntityType(StatueBlockEntity::new,
                    CFBlocks.STATUE_ANCIENT.get(),
                    CFBlocks.STATUE_SQUIRTLE.get(),
                    CFBlocks.STATUE_BULBASAUR.get(),
                    CFBlocks.STATUE_CHARMANDER.get(),
                    CFBlocks.STATUE_PIKACHU.get()
            ));

    public static void init() {}
}
