package viyper.sharkmod.testshark;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;
import viyper.sharkmod.RandomSwimmingGoal;
import viyper.sharkmod.SharkMod;

import java.util.function.Supplier;

public class TestSharkEntity extends AbstractFish implements GeoEntity {
    public static final String ID = "test_shark";
    public static final String BUCKET_ID = ID + "_bucket";
    public static final String SPAWN_EGG_ID = ID + "_spawn_egg";

    protected static final RawAnimation SWIM_ANIM = RawAnimation.begin().thenLoop("test_shark.swim");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static Supplier<EntityType<TestSharkEntity>> entityType;
    private static Supplier<MobBucketItem> bucketItem;
    private static Supplier<SpawnEggItem> spawnEggItem;

    public TestSharkEntity(EntityType<? extends TestSharkEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public @NotNull AttributeMap getAttributes() {
        return new AttributeMap(AbstractFish.createMobAttributes().build());
    }

    @Override
    public boolean isPushedByFluid() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Swimming", 5, this::swimAnimController));
    }

    protected <E extends TestSharkEntity> PlayState swimAnimController(final AnimationState<E> event) {
        if (event.isMoving()) {
            return event.setAndContinue(SWIM_ANIM);
        }

        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    protected @NotNull SoundEvent getFlopSound() {
        return SoundEvents.GUARDIAN_FLOP;
    }

    @Override
    public @NotNull ItemStack getBucketItemStack() {
        return new ItemStack(bucketItem.get());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RandomSwimmingGoal(this, 2.5D, 80, 16, 2));
    }

    @SubscribeEvent
    public void onRegisterSpawnPlacementsEvent(RegisterSpawnPlacementsEvent event) {
        event.register(entityType.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WaterAnimal::checkSurfaceWaterAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    public static class ContentLoader extends viyper.sharkmod.ContentLoader {
        @Override
        public void loadStatic() {
            entityType = SharkMod.ENTITY_TYPES.register(
                    ID,
                    () -> EntityType.Builder.of(TestSharkEntity::new, MobCategory.WATER_CREATURE)
                            .sized(1.0F, 0.4F)
                            .build(ResourceLocation.fromNamespaceAndPath(SharkMod.MOD_ID, ID).toString())
            );

            bucketItem = SharkMod.ITEMS.register(BUCKET_ID,
                    () -> new MobBucketItem(entityType.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH,
                            new Item.Properties().stacksTo(1)));

            //noinspection deprecation
            spawnEggItem = SharkMod.ITEMS.register(SPAWN_EGG_ID,
                    () -> new SpawnEggItem(
                            entityType.get(),
                            12633042, // Test Shark Base Color RGB HEX -> DEC
                            0,
                            new Item.Properties()
                    ));
        }

        @Override
        public void loadMod(IEventBus eventBus) {
            eventBus.register(this);
        }

        @SubscribeEvent
        public void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                event.accept(bucketItem.get());
            } else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                event.accept(spawnEggItem.get());
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void registerSpawnEggColors(RegisterColorHandlersEvent.Item event) {
            SpawnEggItem spawnEgg = spawnEggItem.get();
            event.register((stack, layer) -> FastColor.ARGB32.opaque(spawnEgg.getColor(layer)), spawnEgg);
        }

        @EventBusSubscriber(modid = SharkMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
        public static class ClientContentLoader {
            @SubscribeEvent
            public static void onClientSetup(FMLClientSetupEvent event) {
                EntityRenderers.register(entityType.get(), TestSharkRenderer::new);
            }
        }
    }
}
