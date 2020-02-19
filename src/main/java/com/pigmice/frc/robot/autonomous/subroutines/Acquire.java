package com.pigmice.frc.robot.autonomous.subroutines;

import com.pigmice.frc.robot.subsystems.Intake;

public class Acquire implements ISubroutine {
    private final Intake intake;
    private final PathFollower path;

    public Acquire(Intake intake, PathFollower path) {
        this.intake = intake;
        this.path = path;
    }

    @Override
    public void initialize() {
        path.initialize();
    }

    @Override
    public boolean update() {
        intake.setPosition(Intake.Position.DOWN);

        boolean done = path.update();

        intake.run(!done);

        return done;
    }
}
