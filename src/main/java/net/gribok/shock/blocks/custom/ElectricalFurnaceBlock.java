package net.gribok.shock.blocks.custom;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.gribok.shock.blocks.entity.ModBlockEntities;
import net.gribok.shock.blocks.entity.custom.ElectricalFurnaceBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ElectricalFurnaceBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final BooleanProperty LIT = Properties.LIT;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final MapCodec<ElectricalFurnaceBlock> CODEC = ElectricalFurnaceBlock.createCodec(ElectricalFurnaceBlock::new);

    public ElectricalFurnaceBlock(Settings settings) {
        super(settings.luminance(state -> state.get(LIT) ? 13 : 0));
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false).with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // Ставим блок так, чтобы он смотрел "против" игрока (обычно так делают)
        Direction playerFacing = ctx.getHorizontalPlayerFacing().getOpposite();
        return this.getDefaultState().with(FACING, playerFacing).with(LIT, false);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricalFurnaceBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, ModBlockEntities.ELECTRICAL_FURNACE_BE, ElectricalFurnaceBlockEntity::tick);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(LIT)) return;

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.0;
        double z = pos.getZ() + 0.5;

        Direction facing = state.get(FACING);

        for (int i = 0; i < 2; i++) {
            double offsetX = x + (random.nextDouble() - 0.5) * 0.4;
            double offsetY = pos.getY() + 0.3 + random.nextDouble() * 0.2;
            double offsetZ = z + (random.nextDouble() - 0.5) * 0.4;

            offsetX += facing.getOffsetX() * 0.52;
            offsetZ += facing.getOffsetZ() * 0.52;

            world.addParticle(ParticleTypes.FLAME, offsetX, offsetY, offsetZ, 0.0, 0.01, 0.0);
        }

        BlockPos above = pos.up(2);
        if (world.getBlockState(above).isOpaque()) {
            return;
        }

        for (int i = 0; i < 30; i++) {
            double dx = x + random.nextGaussian() * 0.3;
            double dy = pos.getY() + 1.5 + random.nextDouble() * 0.5;
            double dz = z + random.nextGaussian() * 0.3;

            world.addParticle(ParticleTypes.SMOKE, dx, dy, dz, 0.0, 0.02, 0.0);

            world.addParticle(ParticleTypes.LARGE_SMOKE, dx, dy, dz, 0.0, 0.05, 0.0);
        }

        if (random.nextFloat() < 0.05f) {
            world.addParticle(ParticleTypes.LARGE_SMOKE, x, y + 1.8, z, 0.0, 0.15, 0.0);
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (stack.getItem() == Items.COAL || stack.getItem() == Items.CHARCOAL || stack.getItem() == Items.COAL_BLOCK) {
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof ElectricalFurnaceBlockEntity furnace) {
                    if (!furnace.isBurning()) {
                        Integer burnTime = FuelRegistry.INSTANCE.get(stack.getItem());
                        if (burnTime != null && burnTime > 0) {
                            furnace.startBurning(burnTime);
                            stack.decrement(1);
                            return ItemActionResult.success(world.isClient);
                        }
                    }
                }
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }
}
