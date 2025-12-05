package net.shelmarow.tcr_bosses.plugin;

import com.brass_amber.ba_bt.init.BTEntityType;
import net.shelmarow.tcr_bosses.TCRBosses;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class TCRBossesMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String s) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String s, String s1) {
        try {
            Class.forName(BTEntityType.class.getName(), false, getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            TCRBosses.LOGGER.error("{} not found", BTEntityType.class.getName());
            return false;
        }
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
