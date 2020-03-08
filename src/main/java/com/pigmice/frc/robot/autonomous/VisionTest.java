package com.pigmice.frc.robot.autonomous;

import java.util.Arrays;

import com.pigmice.frc.robot.autonomous.tasks.Shoot;
import com.pigmice.frc.robot.autonomous.tasks.SpinUp;
import com.pigmice.frc.robot.autonomous.tasks.VisionAlign;
import com.pigmice.frc.robot.subsystems.Drivetrain;
import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Intake;
import com.pigmice.frc.robot.subsystems.Shooter;

public class VisionTest extends Autonomous {
    public VisionTest(Drivetrain drivetrain, Shooter shooter, Feeder feeder, Intake intake) {
        this.subroutines = Arrays.asList(new VisionAlign(drivetrain, 20.0), new SpinUp(shooter, 2.0, 360),
                new Shoot(shooter, feeder, intake, 1.0, 360));
    }

    public void initialize() {
        super.initialize();
    }

    public String name() {
        return "Vision test";
    }
}
