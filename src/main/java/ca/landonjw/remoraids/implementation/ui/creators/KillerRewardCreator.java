package ca.landonjw.remoraids.implementation.ui.creators;

import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.ICreatorUI;
import ca.landonjw.remoraids.implementation.rewards.KillerReward;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * The {@link ICreatorUI} used for creating a new {@link KillerReward}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class KillerRewardCreator implements ICreatorUI<IReward> {

    /** {@inheritDoc} */
    @Override
    public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull Collection<IReward> toAddTo) {
        toAddTo.add(new KillerReward());
        source.getSource().get().open();
    }

    /** {@inheritDoc} */
    @Override
    public ItemStack getCreatorIcon() {
        return new ItemStack(Items.DIAMOND);
    }

    /** {@inheritDoc} */
    @Override
    public String getCreatorTitle() {
        return TextFormatting.AQUA + "" + TextFormatting.BOLD + "Killer Reward";
    }

}
