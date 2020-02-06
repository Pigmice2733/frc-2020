package com.pigmice.frc.robot.autonomous;

import java.util.Arrays;

import com.pigmice.frc.robot.autonomous.subroutines.Drive;
import com.pigmice.frc.robot.autonomous.subroutines.Turn;
import com.pigmice.frc.robot.subsystems.Drivetrain;

public class Test extends Autonomous {
    public Test(Drivetrain drivetrain) {
        this.subroutines = Arrays.asList(
            new Turn(drivetrain, Math.PI),
            new Drive(drivetrain, 1.0)
        );
    }

    public void initialize() {
        super.initialize();
    }
}
