package com.pigmice.frc.robot.autonomous.subroutines;

import com.pigmice.frc.lib.controllers.PID;
import com.pigmice.frc.lib.controllers.PIDGains;
import com.pigmice.frc.lib.motion.Setpoint;
import com.pigmice.frc.lib.motion.StaticProfile;
import com.pigmice.frc.lib.motion.execution.ProfileExecutor;
import com.pigmice.frc.lib.utils.Range;
import com.pigmice.frc.robot.subsystems.Drivetrain;

public class Turn implements ISubroutine {
    private ProfileExecutor executor;
    private PID turningPID;

    private final Drivetrain drivetrain;

    private final double targetRotation;
    private double targetAngle = 0.0;
    private double initialAngle = 0.0;
    private final boolean absolute;

    public Turn(Drivetrain drivetrain, double radians) {
        this(drivetrain, radians, false);
    }

    public Turn(Drivetrain drivetrain, double radians, boolean absolute) {
        this.drivetrain = drivetrain;
        this.targetRotation = radians;
        this.absolute = absolute;

        PIDGains gains = new PIDGains(0.6, 0.05, 0.0);
        Range turningBounds = new Range(-0.8, 0.8);
        turningPID = new PID(gains, turningBounds, 0.02);
    }

    public void initialize() {
        initialAngle = drivetrain.getHeading();

        if (absolute) {
            targetAngle = targetRotation;
        } else {
            targetAngle = initialAngle + targetRotation;
        }

        StaticProfile profile = new StaticProfile(0.0, initialAngle, targetAngle, 2*Math.PI, 2*Math.PI, Math.PI);
        executor = new ProfileExecutor(profile, this::driveOutput, this::getAngle, 0.0075*2*Math.PI);

        turningPID.initialize(0.0, 0.0);
        executor.initialize();
    }

    public boolean update() {
        return executor.update();
    }

    private double getAngle() {
        return drivetrain.getHeading();
    }

    private void driveOutput(Setpoint sp) {
        double output = turningPID.calculateOutput(getAngle(), sp.getPosition());

        drivetrain.tankDrive(-output, output);
    }
}
