package com.pigmice.frc.robot.autonomous.actions;

import com.pigmice.frc.robot.subsystems.Intake;

public class Acquire implements IAction {
    private final Intake intake;

    public Acquire(Intake intake) {
        this.intake = intake;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void update() {
        intake.setPosition(Intake.Position.DOWN);
        intake.run(true);
    }

    @Override
    public void end() {
        intake.setPosition(Intake.Position.UP);
        intake.run(false);
    }
}
