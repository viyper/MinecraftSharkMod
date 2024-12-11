package viyper.sharkmod.testshark;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import viyper.sharkmod.SharkMod;

public class TestSharkModel extends DefaultedEntityGeoModel<TestSharkEntity> {
    public TestSharkModel() {
        super(ResourceLocation.fromNamespaceAndPath(SharkMod.MOD_ID, TestSharkEntity.ID));
    }
}
