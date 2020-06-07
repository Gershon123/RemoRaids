package ca.landonjw.remoraids.implementation.ui.creators;

import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.ICreatorUI;
import ca.landonjw.remoraids.implementation.rewards.contents.ItemContent;
import ca.landonjw.remoraids.internal.inventory.api.InventoryAPI;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The {@link ICreatorUI} used for creating a new {@link ItemContent}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class ItemContentCreator implements ICreatorUI<IRewardContent> {

    /** {@inheritDoc} */
    @Override
    public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull List<IRewardContent> toAddTo) {
        new Creator(source, player, toAddTo);
    }

    /** {@inheritDoc} */
    @Override
    public ItemStack getCreatorIcon() {
        return new ItemStack(Items.DIAMOND);
    }

    /** {@inheritDoc} */
    @Override
    public String getCreatorTitle() {
        return TextFormatting.AQUA + "" + TextFormatting.BOLD + "Item Content";
    }

    class Creator {

        /** The source that opened this creator. */
        private IBossUI source;
        /** The player that the user interface is intended for. */
        private EntityPlayerMP player;
        /** The list of reward contents to append creation to. */
        private List<IRewardContent> toAddTo;

        /**
         * Constructor for the creator user interface.
         *
         * @param source  source that opened this creator
         * @param player  player that user interface is intended for
         * @param toAddTo list of reward contents to append creation to
         */
        public Creator(IBossUI source, EntityPlayerMP player, List<IRewardContent> toAddTo){
            this.source = source;
            this.player = player;
            this.toAddTo = toAddTo;

            InventoryAPI.getInstance().closePlayerInventory(player);
            player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Enter an item and amount (ie. 'dirt 64') or 'cancel' to cancel!"));
            MinecraftForge.EVENT_BUS.register(this);
        }

        /**
         * Used to receive message from player to determine the item and amount for reward content.
         *
         * @param event called when a player sends message on server
         */
        @SubscribeEvent
        public void onMessage(ServerChatEvent event){
            if(event.getPlayer().equals(player)){
                event.setCanceled(true);
                String message = event.getMessage();
                if(!message.equalsIgnoreCase("cancel")){
                    String[] args = message.split(" ");
                    if(args.length == 1){
                        Item item = Item.getByNameOrId(args[0]);
                        if(item != null){
                            ItemContent content = new ItemContent(new ItemStack(item, 1));
                            toAddTo.add(content);
                            source.getSource().get().open();
                            MinecraftForge.EVENT_BUS.unregister(this);
                        }
                        else{
                            player.sendMessage(new TextComponentString(TextFormatting.RED + "Item not found. Try again."));
                        }
                    }
                    else if(args.length == 2){
                        Item item = Item.getByNameOrId(args[0]);
                        if(item != null){
                            try{
                                int amount = Integer.parseInt(args[1]);
                                if(amount >= 1 && amount <= 64){
                                    ItemContent content = new ItemContent(new ItemStack(item, amount));
                                    toAddTo.add(content);
                                    source.getSource().get().open();
                                    MinecraftForge.EVENT_BUS.unregister(this);
                                }
                                else{
                                    player.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid amount given. Try again."));
                                }
                            }
                            catch (NumberFormatException e){
                                player.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid amount given. Try again."));
                            }
                        }
                        else{
                            player.sendMessage(new TextComponentString(TextFormatting.RED + "Item not found. Try again."));
                        }
                    }
                    else{
                        player.sendMessage(new TextComponentString(TextFormatting.RED + "Incorrect amount of arguments. Try again."));
                    }
                }
                else{
                    source.getSource().get().open();
                    MinecraftForge.EVENT_BUS.unregister(this);
                }
            }
        }

    }

}