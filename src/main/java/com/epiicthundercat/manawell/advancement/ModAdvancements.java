package com.epiicthundercat.manawell.advancement;

import com.epiicthundercat.manawell.setup.Registration;

public class ModAdvancements {

    // MC 26.1.2: triggers are now registered via DeferredRegister<CriterionTrigger<?>> in Registration
    // (Registries.TRIGGER_TYPE). Direct CriteriaTriggers.register() calls from enqueueWork fail because
    // BuiltInRegistries are frozen by that point.
    //
    // Callers use .get() at runtime after registration completes — never at class-load time.

    public static WitchStoleManaProximityTrigger getWitchStoleMana() {
        return Registration.WITCH_STOLE_MANA_PROXIMITY.get();
    }

    public static PlayerSteppedOnManaWellTrigger getPlayerSteppedOnWell() {
        return Registration.PLAYER_STEPPED_ON_WELL.get();
    }

    // Kept for compatibility — no longer needed since registration is handled by DeferredRegister.
    public static void init() {}
}
