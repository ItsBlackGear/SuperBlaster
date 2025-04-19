package com.blackgear.superblaster.common.registries;

import com.blackgear.superblaster.common.level.block.ScrapWorkbenchBlock;
import com.blackgear.superblaster.core.SuperBlaster;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SuperBlaster.MODID);

    public static final DeferredBlock<ScrapWorkbenchBlock> SCRAP_WORKBENCH = BLOCKS.registerBlock(
        "scrap_workbench",
        ScrapWorkbenchBlock::new,
        BlockBehaviour.Properties.of()
            .noOcclusion()
    );
}