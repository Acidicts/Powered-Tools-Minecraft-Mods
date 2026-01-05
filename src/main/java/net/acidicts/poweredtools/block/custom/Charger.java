package net.acidicts.poweredtools.block.custom;

import com.mojang.serialization.MapCodec;
import net.acidicts.poweredtools.block.entity.custom.ChargerBlockEntity;
import net.acidicts.poweredtools.particle.ModParticles;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class Charger extends BlockWithEntity {
    public static final MapCodec<Charger> CODEC = createCodec(Charger::new);
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public Charger(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChargerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof ChargerBlockEntity charger) {
                charger.tick();
            }
        };
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                  WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // Trigger block entity update when neighbors change
        if (!world.isClient()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ChargerBlockEntity chargerEntity) {
                // Mark the block entity as needing a sync to update the animation
                chargerEntity.markDirty();
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        createParticles(world, pos);
    }

    private void createParticles(World world, BlockPos pos) {
        for (int i = 0; i < 2; i++) {
            double xPos = pos.getX() + 0.5;
            double yPos = pos.getY() + 0.5;
            double zPos = pos.getZ() + 0.5;
            double offset = Math.random() * 0.6 - 0.3;
            double velocity_x = Math.random() * 0.1 + 0.02;
            double velocity_y = Math.abs(Math.random() * 0.1 + 0.02);
            double velocity_z = Math.random() * 0.1 + 0.02;

            if (randomBoolean()) velocity_x = -velocity_x;
            if (randomBoolean()) velocity_y = -velocity_y;
            if (randomBoolean()) velocity_z = -velocity_z;

            world.addParticle(ModParticles.ELECTRIC_SPARK, xPos + offset, yPos, zPos + offset, velocity_x, velocity_y, velocity_z);
        }
    }

    private boolean randomBoolean() {
        return Math.random() < 0.5;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ChargerBlockEntity chargerBlockEntity) {
            if (!chargerBlockEntity.hasSpaceToOpen() && !chargerBlockEntity.hasOpened()) {
                player.sendMessage(Text.literal("Place the Charger Without Blocks around it to open it!").formatted(Formatting.RED), true);
                return ActionResult.FAIL;
            }
            if (chargerBlockEntity.hasOpened()) {
                createParticles(world, pos);
                if (!world.isClient) {
                    NamedScreenHandlerFactory screenHandlerFactory = ((ChargerBlockEntity) world.getBlockEntity(pos));

                    if (screenHandlerFactory != null) {
                        player.openHandledScreen(screenHandlerFactory);
                    }
                }
                return ActionResult.SUCCESS;
            }
        }


        return ActionResult.PASS;
    }
}
