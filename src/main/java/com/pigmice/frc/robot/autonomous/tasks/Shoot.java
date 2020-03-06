package com.pigmice.frc.robot.autonomous.tasks;

import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Feeder.LiftAction;
import com.pigmice.frc.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj.Timer;

public class Shoot implements ITask {
    private final Shooter shooter;
    private final Feeder feeder;

    private final double shootLength;
    private final double targetRange;

    private double startTime = 0.0;
    private boolean shootingStarted = false;

    public Shoot(Shooter shooter, Feeder feeder, double shootLength, double targetRange) {
        this.shooter = shooter;
        this.feeder = feeder;
        this.shootLength = shootLength;
        this.targetRange = targetRange;
    }

    @Override
    public void initialize() {
        startTime = 0.0;
        shootingStarted = false;
    }

    @Override
    public boolean update() {
        shooter.setRange(targetRange);

        if (shooter.isReady() && !shootingStarted) {
            shootingStarted = true;
            startTime = Timer.getFPGATimestamp();
        }

        if(shootingStarted) {
            feeder.runLift(LiftAction.FEED);
            feeder.runHopper(true);
        }

        if (shootingStarted && Timer.getFPGATimestamp() - startTime > shootLength) {
            shooter.stop();
            feeder.runLift(LiftAction.HOLD);
            feeder.runHopper(false);

            return true;
        }

        return false;
    }
}
