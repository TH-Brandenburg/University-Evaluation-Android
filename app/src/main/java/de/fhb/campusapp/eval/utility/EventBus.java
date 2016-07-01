package de.fhb.campusapp.eval.utility;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Admin on 06.12.2015.
 */
public class EventBus {

    public static final Bus eventBus = new Bus(ThreadEnforcer.MAIN);

    public static Bus getEventBus(){
        return eventBus;
    }
}
