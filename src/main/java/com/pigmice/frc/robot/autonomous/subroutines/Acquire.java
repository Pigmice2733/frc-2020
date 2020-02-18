package com.pigmice.frc.robot.autonomous.subroutines;

import com.pigmice.frc.robot.subsystems.Drivetrain;
import com.pigmice.frc.robot.subsystems.Intake;

public class Acquire implements ISubroutine {
    private final Intake intake;
    private final Drive drive;

    public Acquire(Intake intake, Drivetrain drivetrain) {
        this.intake = intake;

        drive = new Drive(drivetrain, -3.5);
    }

    @Override
    public void initialize() {
        drive.initialize();
    }

    @Override
    public boolean update() {
        intake.go(0.4);
        intake.setPosition(true);

        boolean done = drive.update();

        if(done) {
            intake.go(0.0);
        }

        return done;
    }

}
