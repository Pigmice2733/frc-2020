package com.pigmice.frc.robot.autonomous.actions;

import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Feeder.LiftAction;
import com.pigmice.frc.robot.subsystems.Intake;

public class Acquire implements IAction {
    private final Intake intake;
    private final Feeder feeder;

    private final boolean keepIntakeDown;

    public Acquire(Intake intake, Feeder feeder, boolean keepIntakeDown) {
        this.intake = intake;
        this.feeder = feeder;
        this.keepIntakeDown = keepIntakeDown;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void update() {
        intake.setPosition(Intake.Position.DOWN);
        intake.run(true);
        feeder.runHopper(true);
        feeder.runLift(LiftAction.GRAB);
    }

    @Override
    public void end() {
        if(!keepIntakeDown) {
            intake.setPosition(Intake.Position.UP);
            intake.run(false);
            feeder.runHopper(false);
        }

        feeder.runLift(LiftAction.HOLD);
    }
}
