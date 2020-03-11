package ca.landonjw.remoraids.internal.tasks;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TaskTickListener {

    /** List of active tasks to be ticked by the listener. */
    private static List<Task> tasks = new ArrayList<>();
    /** List of tasks that have expired and are to be removed from {@link #tasks}. */
    private static List<Task> expiredTasks = new ArrayList<>();

    /**
     * Invokes {@link Task#tick()} on each active task and checks if any should be removed.
     *
     * @param event the event called when the server ticks
     */
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            for(Task task : new ArrayList<>(tasks)){
                task.tick();
                if(task.isExpired()){
                    expiredTasks.add(task);
                }
            }
            removeExpiredTasks();
        }
    }

    /**
     * Removes any expired tasks from the active task list.
     */
    private void removeExpiredTasks(){
        tasks.removeAll(expiredTasks);
        expiredTasks.clear();
    }

    /**
     * Adds a task to be ticked by the listener.
     *
     * @param task the task to add
     */
    protected static void addTask(@Nonnull Task task){
        tasks.add(task);
    }

}