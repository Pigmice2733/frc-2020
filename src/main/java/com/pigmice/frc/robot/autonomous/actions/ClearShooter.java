package com.pigmice.frc.robot.autonomous.actions;

import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Feeder.LiftAction;
import com.pigmice.frc.robot.subsystems.Shooter;

public class ClearShooter implements IAction {
    private final Shooter shooter;
    private final Feeder feeder;

    public ClearShooter(Shooter shooter, Feeder feeder) {
        this.shooter = shooter;
        this.feeder = feeder;
    }

    @Override
    public void initialize() {
        shooter.initialize();
    }

    @Override
    public void update() {
        shooter.clear();
        feeder.runLift(LiftAction.BACKFEED);
    }

    @Override
    public void end() {
        shooter.stop();
        feeder.runLift(LiftAction.HOLD);
    }
}
