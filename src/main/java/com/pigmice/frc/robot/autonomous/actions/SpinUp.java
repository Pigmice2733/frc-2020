package com.pigmice.frc.robot.autonomous.actions;

import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Feeder.LiftAction;
import com.pigmice.frc.robot.subsystems.Shooter;

public class SpinUp implements IAction {
    private final Shooter shooter;
    private final Feeder feeder;
    private final double targetRange;

    public SpinUp(Shooter shooter, Feeder feeder, double targetRange) {
        this.shooter = shooter;
        this.feeder = feeder;
        this.targetRange = targetRange;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void update() {
        shooter.setRange(targetRange);
        feeder.runLift(LiftAction.BACKFEED);
    }

    @Override
    public void end() {
        feeder.runLift(LiftAction.HOLD);
    }
}
