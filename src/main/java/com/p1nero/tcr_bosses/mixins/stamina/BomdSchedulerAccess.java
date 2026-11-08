package com.p1nero.tcr_bosses.mixins.stamina;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Set;

@Pseudo
@Mixin(targets = "com.cerbon.cerbons_api.api.general.event.EventScheduler", remap = false)
public interface BomdSchedulerAccess {
    @Accessor("eventQueue")
    List<?> tcr_bosses$getEvents();

    @Accessor("eventsToAdd")
    Set<?> tcr_bosses$getPendingEvents();
}
