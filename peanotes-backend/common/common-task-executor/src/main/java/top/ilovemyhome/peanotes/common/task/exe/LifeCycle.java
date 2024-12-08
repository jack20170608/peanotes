package top.ilovemyhome.peanotes.common.task.exe;

import java.time.Duration;

public interface LifeCycle {

    enum State {
        INITIALIZING,
        INITIALIZED,
        STARTING,
        STARTED,
        STOPPING,
        STOPPED
    }

    State getState();

    default void initialize(){}

    default boolean isStarted(){
        return getState() == State.STARTED;
    }

    default boolean isStopped(){
        return getState() == State.STOPPED;
    }

    void start();

    default void stop(){
        stop(Duration.ofHours(1));
    }

    void stop(Duration timeoutDuration);
}
