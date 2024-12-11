package viyper.sharkmod;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RandomSwimmingGoal extends RandomStrollGoal {
    protected final int radius;
    protected final int verticalDistance;

    public RandomSwimmingGoal(PathfinderMob mob, double speedModifier, int interval, int radius, int verticalDistance) {
        super(mob, speedModifier, interval);
        this.radius = radius;
        this.verticalDistance = verticalDistance;
    }

    @Override
    protected @Nullable Vec3 getPosition() {
        return BehaviorUtils.getRandomSwimmablePos(this.mob, this.radius, this.verticalDistance);
    }
}
