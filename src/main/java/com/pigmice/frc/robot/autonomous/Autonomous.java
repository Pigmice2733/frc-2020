package com.pigmice.frc.robot.autonomous;

import java.util.ArrayList;
import java.util.List;

import com.pigmice.frc.robot.autonomous.tasks.ITask;

public abstract class Autonomous {
    protected List<ITask> subroutines = new ArrayList<>();
    private int state = -1;

    public void initialize() {
        state = -1;
    }

    public void update() {
        // Initialize first state
        if (state < 0) {
            state = 0;
            subroutines.get(state).initialize();
        }

        // Update state, advance to next state if done
        if (state < subroutines.size()) {
            boolean done = subroutines.get(state).update();
            if (done) {
                state++;
                if (state < subroutines.size()) {
                    subroutines.get(state).initialize();
                } else {
                    System.out.println("Auto done");
                }
            }
        }
    };
}
