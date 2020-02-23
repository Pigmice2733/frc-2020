package com.pigmice.frc.robot.autonomous.actions;

import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Feeder.LiftAction;

public class BackFeed implements IAction {
    private final Feeder feeder;

    public BackFeed(Feeder feeder) {
        this.feeder = feeder;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void update() {
        feeder.runLift(LiftAction.BACKFEED);
    }

    @Override
    public void end() {
        feeder.runLift(LiftAction.HOLD);
    }
}
