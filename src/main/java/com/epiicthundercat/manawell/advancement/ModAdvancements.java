package com.epiicthundercat.manawell.advancement;

import net.minecraft.advancements.CriteriaTriggers;

public class ModAdvancements {

    // Forge 52: CriteriaTriggers.register() takes a plain namespaced String, not a ResourceLocation.
    public static final WitchStoleManaProximityTrigger WITCH_STOLE_MANA_PROXIMITY =
        CriteriaTriggers.register("manawell:witch_stole_mana_proximity",
            new WitchStoleManaProximityTrigger());

    public static final PlayerSteppedOnManaWellTrigger PLAYER_STEPPED_ON_WELL =
        CriteriaTriggers.register("manawell:player_stepped_on_well",
            new PlayerSteppedOnManaWellTrigger());

    // Called from the @Mod constructor to force static field initialization,
    // ensuring all triggers are registered before advancements are loaded.
    public static void init() {}
}
