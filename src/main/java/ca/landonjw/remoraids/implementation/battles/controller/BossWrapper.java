package ca.landonjw.remoraids.implementation.battles.controller;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.implementation.battles.controller.participants.BossParticipant;
import ca.landonjw.remoraids.internal.pokemon.PokemonUtils;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.status.StatusBase;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import java.util.List;

public class BossWrapper extends PixelmonWrapper {

    private BossParticipant owner;

    /**
     * Constructor for the boss wrapper.
     *
     * @param owner       participant that wrapper is being controlled from
     * @param pixelmon    the pixelmon to make into wrapper
     */
    public BossWrapper(@Nonnull BossParticipant owner,
                       @Nonnull EntityPixelmon pixelmon){
        super(owner, PokemonUtils.clonePokemon(owner.getBossEntity().getBoss().getPokemon()), 0);
        this.owner = owner;
        super.setHealth(owner.getBossBattle().getHealth());
        this.entity = pixelmon;
        removeDisabledMoves();
    }

    /**
     * Removes all disabled boss moves from the wrapper.
     *
     * If the boss has no remaining moves, it will use Tackle.
     */
    private void removeDisabledMoves(){
        List<String> disabledMoves = RemoRaids.getRestraintsConfig().getDisabledBossMoves();
        for(String move : disabledMoves){
            getMoveset().removeAttack(move);
        }

        if(getMoveset().isEmpty()){
            getMoveset().add(new Attack(AttackBase.getAttackBase("tackle").get()));
        }
    }

    /**
     * Redirects the health setting to {@link IBossBattle#setBossHealth(int, EntityPlayerMP)}.
     *
     * @param newHP the new health of the pokemon
     */
    @Override
    public void setHealth(int newHP) {
        owner.getBossBattle().setBossHealth(newHP, owner.bc.getPlayers().get(0).player);
    }

    /**
     * Checks if the boss cannot have a particular status
     *
     * @param status   status to check
     * @param opponent opponent applying the status
     * @return true if the boss cannot have the status, false if it can
     */
    @Override
    public boolean cannotHaveStatus(StatusBase status, PixelmonWrapper opponent){
        return cannotHaveStatus(status, opponent, false);
    }

    /**
     * Checks if the boss cannot have a particular status.
     *
     * @param status               status to check
     * @param opponent             opponent applying the status
     * @param ignorePrimaryOverlap has no effect
     * @return true if the boss cannot have the status, false if it can
     */
    @Override
    public boolean cannotHaveStatus(StatusBase status, PixelmonWrapper opponent, boolean ignorePrimaryOverlap){
        List<StatusType> disabledStatus = RemoRaids.getRestraintsConfig().getDisabledStatus();
        StatusType[] arrDisabledStatus = new StatusType[disabledStatus.size()];
        disabledStatus.toArray(arrDisabledStatus);

        if(status.type.isStatus(arrDisabledStatus)){
            return true;
        }
        return super.cannotHaveStatus(status, opponent, ignorePrimaryOverlap);
    }

}