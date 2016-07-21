package com.russmiles.antifragile.api;

/**
 * Created by gtarrant-fisher on 06/07/2016.
 */
public interface SimpleCommandAggregate extends EventProcessor,CommandProcessor {


    /*
     * initialise method should populate the aggregate from an event store
     * that contains all the events that represent a history of the aggregate
     *  for each event found the handle event method will be called
     */

    public void initialise ();

    /*
     * the implementation of this should consume commands from the aggregates command source
     * for each event found the handle command method will be called
     */
    public void run (int loopCount);

    public void handleCommand (Command command);

    public void handleEvent (Event event);

}
