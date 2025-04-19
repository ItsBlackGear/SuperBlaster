package com.blackgear.superblaster.common.level.block;

import com.blackgear.superblaster.common.level.inventory.ScrapWorkbenchMenu;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ScrapWorkbenchBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<WorkbenchPart> PART = EnumProperty.create("part", WorkbenchPart.class);

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(ScrapWorkbenchBlock::new);
    }

    public ScrapWorkbenchBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(PART, WorkbenchPart.PRIMARY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockPos pos = context.getClickedPos();
        // Get the position to the right of the direction
        BlockPos secondaryPos = pos.relative(direction.getClockWise());
        Level level = context.getLevel();

        if (pos.getY() < level.getMaxBuildHeight() && level.getBlockState(secondaryPos).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(FACING, direction);
        } else {
            return null;
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            Direction direction = state.getValue(FACING);
            // Place secondary block to the right
            BlockPos secondaryPos = pos.relative(direction.getClockWise());
            level.setBlock(secondaryPos, state.setValue(PART, WorkbenchPart.SECONDARY), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            WorkbenchPart part = state.getValue(PART);
            BlockPos otherPartPos;
            Direction direction = state.getValue(FACING);

            if (part == WorkbenchPart.SECONDARY) {
                // Secondary is to the right, so primary is to the left
                otherPartPos = pos.relative(direction.getCounterClockWise());
            } else {
                // Primary is on the left, so secondary is to the right
                otherPartPos = pos.relative(direction.getClockWise());
            }

            BlockState otherPart = level.getBlockState(otherPartPos);
            if (otherPart.getBlock() == this && otherPart.getValue(PART) != part) {
                level.removeBlock(otherPartPos, false);
                level.levelEvent(player, 2001, otherPartPos, Block.getId(otherPart));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((containerId, inventory, player) -> new ScrapWorkbenchMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)), Component.empty());
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    public enum WorkbenchPart implements StringRepresentable {
        PRIMARY("primary"),
        SECONDARY("secondary");

        private final String name;

        WorkbenchPart(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}