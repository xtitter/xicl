package ru.icl.dicewars.core;

import java.util.LinkedList;
import java.util.Queue;

import ru.icl.dicewars.core.activity.DiceWarsActivity;
import ru.icl.dicewars.core.activity.FlagDistributedActivity;
import ru.icl.dicewars.core.activity.LandUpdatedActivity;
import ru.icl.dicewars.core.activity.PlayerAttackActivity;
import ru.icl.dicewars.core.activity.WorldCreatedActivity;

public class ActivityQueueImpl implements ActivityQueue{
     private Queue<DiceWarsActivity> queue = new LinkedList<DiceWarsActivity>();      
     
     boolean isGameOver = false;
     boolean isWorldCreated = false;
     boolean isFlagDistributed = false;
     
     
     @Override
     public synchronized DiceWarsActivity poll() {
          return queue.poll();
     }
     
     @Override
     public synchronized void add(DiceWarsActivity e) {
          if (e instanceof WorldCreatedActivity && !isFlagDistributed){
               throw new IllegalStateException();
          }
          
          if (e instanceof LandUpdatedActivity && (!isFlagDistributed || !isWorldCreated)){
               throw new IllegalStateException();
          }

          if (e instanceof PlayerAttackActivity && (!isFlagDistributed || !isWorldCreated)){
               throw new IllegalStateException();
          }
          
          queue.add(e);
          if (e instanceof FlagDistributedActivity){
               isFlagDistributed = true;
          }

          if (e instanceof WorldCreatedActivity){
               isWorldCreated = true;
          }
     }
     
     @Override
     public synchronized void clear(){
          queue.clear();
          isWorldCreated = false;
          isGameOver = false;
          isFlagDistributed = false;
     }
     
    @Override
    public synchronized int size() {
    	return queue.size();
    }
}
