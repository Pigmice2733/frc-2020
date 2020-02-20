package com.pigmice.frc.robot.autonomous.actions;

import com.pigmice.frc.robot.subsystems.Shooter;

public class SpinUp implements IAction {
    private final Shooter shooter;

    public SpinUp(Shooter shooter) {
        this.shooter = shooter;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void update() {
        shooter.run(true);
    }

    @Override
    public void end() {
    }
}
