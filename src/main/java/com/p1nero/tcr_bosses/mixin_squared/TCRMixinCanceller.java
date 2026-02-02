package com.p1nero.tcr_bosses.mixin_squared;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class TCRMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(List<String> list, String s) {
        return s.contains("com.legacy.lost_aether.mixin.MusicManagerMixins");
    }
}
