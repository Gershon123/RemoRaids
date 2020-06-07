package ca.landonjw.remoraids.implementation.ui.editors;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.IEditorUI;
import ca.landonjw.remoraids.implementation.rewards.KillerReward;
import ca.landonjw.remoraids.implementation.ui.pages.BaseBossUI;
import ca.landonjw.remoraids.implementation.ui.pages.RewardContentsUI;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import com.pixelmonmod.pixelmon.config.PixelmonItemsValuables;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KillerRewardEditor implements IEditorUI<KillerReward> {

    @Override
    public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull KillerReward killerReward) {
        new Editor(source, player, source.getBossEntity(), killerReward).open();
    }

    class Editor extends BaseBossUI {

        private KillerReward reward;

        /**
         * Default constructor.
         *
         * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
         * @param player     the player using the user interface
         * @param bossEntity the boss entity being edited
         */
        public Editor(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity, @Nonnull KillerReward reward) {
            super(source, player, bossEntity);
            this.reward = reward;
        }

        @Override
        public void open() {
            Button back = Button.builder()
                    .item(new ItemStack(Blocks.BARRIER))
                    .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back")
                    .onClick(() -> {
                        source.open();
                    })
                    .build();

            Button editContents = Button.builder()
                    .item(new ItemStack(PixelmonItemsValuables.nugget))
                    .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit Reward Contents")
                    .onClick(() -> {
                        RewardContentsUI contentsUI = new RewardContentsUI(this, player, bossEntity, reward);
                        contentsUI.open();
                    })
                    .build();

            Template template = Template.builder(5)
                    .line(LineType.Horizontal, 1, 0, 9, getWhiteFiller())
                    .line(LineType.Horizontal, 3, 0, 9, getWhiteFiller())
                    .border(0,0, 5,9, getBlueFiller())
                    .set(0, 4, getBossButton())
                    .set(2, 4, editContents)
                    .set(3, 4, back)
                    .build();

            Page page = Page.builder()
                    .template(template)
                    .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Reward Settings")
                    .build();

            page.forceOpenPage(player);
        }

    }

}
