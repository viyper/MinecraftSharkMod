package viyper.sharkmod;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import viyper.sharkmod.testshark.TestSharkEntity;

import java.util.ArrayList;
import java.util.List;

@Mod(SharkMod.MOD_ID)
public class SharkMod {
    public static final String MOD_ID = "sharkmod";
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);

    public static final List<ContentLoader> LOADERS = new ArrayList<>();

    static {
        LOADERS.add(new TestSharkEntity.ContentLoader());

        LOADERS.forEach(ContentLoader::loadStatic);
    }

    public SharkMod(IEventBus modEventBus, @SuppressWarnings("unused") ModContainer modContainer) {
        //NeoForge.EVENT_BUS.register(this);

        LOADERS.forEach(l -> l.loadMod(modEventBus));

        ENTITY_TYPES.register(modEventBus);
        ITEMS.register(modEventBus);
    }
}
