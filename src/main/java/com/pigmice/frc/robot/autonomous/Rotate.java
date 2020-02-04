package com.pigmice.frc.robot.autonomous;

import java.util.Arrays;

import com.pigmice.frc.robot.autonomous.subroutines.Turn;
import com.pigmice.frc.robot.subsystems.Drivetrain;

public class Rotate extends Autonomous {
    public Rotate(Drivetrain drive) {
        this.subroutines = Arrays.asList(new Turn(drive, -2*Math.PI));
    }

    public void initialize() {
        super.initialize();
    }
}
