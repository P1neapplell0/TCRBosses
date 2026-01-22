package net.shelmarow.tcr_bosses.entity.cataclysm.remnant;

import com.asanginxst.epicfightx.gameassets.AnimationsX;
import com.asanginxst.epicfightx.gameassets.ExtraAnimations;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class AncientRemnantAI {
    public static final CECombatBehaviors.Builder<MobPatch<?>> SPEAR;
    public static final CECombatBehaviors.Builder<MobPatch<?>> BOW;

    static {
        SPEAR = CECombatBehaviors.builder()

                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(20)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinAngle(0,60)
                                .withinDistance(0,5)
                                .animationBehavior(AnimationsX.SPEAR_TWOHAND_AUTO1, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0,5.5)
                                        .animationBehavior(AnimationsX.SPEAR_TWOHAND_AUTO2, 0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0,6)
                                                .animationBehavior(ExtraAnimations.SPEAR_TWOHAND_AUTO3, 0F)
                                        )
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinAngle(0,60)
                                .withinDistance(0,5)
                                .animationBehavior(AnimationsX.SPEAR_TWOHAND_AUTO1, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0,5.5)
                                        .animationBehavior(AnimationsX.SPEAR_TWOHAND_AUTO2, 0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0,6)
                                                .animationBehavior(ExtraAnimations.SPEAR_TWOHAND_AUTO3, 0F)
                                        )
                                )
                        )
                )
        ;

        BOW = CECombatBehaviors.builder();
    }
}
