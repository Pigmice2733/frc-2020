package com.pigmice.frc.robot.autonomous;

import java.util.Arrays;

import com.pigmice.frc.robot.autonomous.tasks.Shoot;
import com.pigmice.frc.robot.autonomous.tasks.SpinUp;
import com.pigmice.frc.robot.autonomous.tasks.VisionAlign;
import com.pigmice.frc.robot.subsystems.Drivetrain;
import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Shooter;
import com.pigmice.frc.robot.subsystems.Shooter.Action;

public class VisionTest extends Autonomous {
    public VisionTest(Drivetrain drivetrain, Shooter shooter, Feeder feeder) {
        this.subroutines = Arrays.asList(new VisionAlign(drivetrain), new SpinUp(shooter, 2.0, Action.LONG_SHOT),
                new Shoot(shooter, feeder, 1.0, Action.LONG_SHOT));
    }

    public void initialize() {
        super.initialize();
    }

    public String name() {
        return "Vision test";
    }
}
