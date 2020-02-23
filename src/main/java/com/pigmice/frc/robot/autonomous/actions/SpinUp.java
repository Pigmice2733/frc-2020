package com.pigmice.frc.robot.autonomous.actions;

import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Feeder.LiftAction;
import com.pigmice.frc.robot.subsystems.Shooter;
import com.pigmice.frc.robot.subsystems.Shooter.Action;

public class SpinUp implements IAction {
    private final Shooter shooter;
    private final Feeder feeder;

    public SpinUp(Shooter shooter, Feeder feeder) {
        this.shooter = shooter;
        this.feeder = feeder;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void update() {
        shooter.run(Action.SHOOT);
        feeder.runLift(LiftAction.BACKFEED);
    }

    @Override
    public void end() {
        feeder.runLift(LiftAction.HOLD);
    }
}
