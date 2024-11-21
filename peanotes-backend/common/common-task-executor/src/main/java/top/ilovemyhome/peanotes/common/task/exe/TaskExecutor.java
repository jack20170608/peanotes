package top.ilovemyhome.peanotes.common.task.exe;

import top.ilovemyhome.peanotes.common.task.exe.domain.*;

public interface TaskExecutor {

    ReturnT<String> beat();

    ReturnT<String> idleBeat(IdleBeatParam idleBeatParam);

    ReturnT<String> run(TriggerParam triggerParam);

    ReturnT<String> kill(KillParam killParam);

    ReturnT<LogResult> log(LogParam logParam);
}
