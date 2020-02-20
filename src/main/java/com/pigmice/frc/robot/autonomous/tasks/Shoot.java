package com.pigmice.frc.robot.autonomous.tasks;

import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj.Timer;

public class Shoot implements ITask {
    private final Shooter shooter;
    private final Feeder feeder;

    private final double shootingLength = 1.2;

    private double shootStartTime = 0.0;
    private boolean shootingStarted = false;

    private double shooterReadyTime = 0.0;
    private boolean shooterStable = false;

    public Shoot(Shooter shooter, Feeder feeder) {
        this.shooter = shooter;
        this.feeder = feeder;
    }

    @Override
    public void initialize() {
        shootStartTime = 0.0;
        shootingStarted = false;

        shooterReadyTime = 0.0;
        shooterStable = false;
    }

    @Override
    public boolean update() {
        shooter.run(true);

        if (!shooter.isReady() && !shooterStable) {
            shootingStarted = false;
            return false;
        }

        if (!shootingStarted && shooter.isReady()) {
            shootingStarted = true;
            shooterStable = false;
            shootStartTime = Timer.getFPGATimestamp();
        }

        if (!shooterStable && Timer.getFPGATimestamp() - shootStartTime > 0.5) {
            shooterStable = true;
            shooterReadyTime = Timer.getFPGATimestamp();
        }

        if (shooterStable && Timer.getFPGATimestamp() - shooterReadyTime > shootingLength) {
            shooter.run(false);
            feeder.runLift(false);
            feeder.runHopper(false);

            return true;
        }

        if (shooterStable) {
            feeder.runLift(true);
            feeder.runHopper(true);
        }

        return false;
    }

}
