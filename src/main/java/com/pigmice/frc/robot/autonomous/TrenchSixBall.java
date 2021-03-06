package com.pigmice.frc.robot.autonomous;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pigmice.frc.lib.purepursuit.Path;
import com.pigmice.frc.lib.utils.Point;
import com.pigmice.frc.robot.autonomous.actions.Acquire;
import com.pigmice.frc.robot.autonomous.actions.ClearShooter;
import com.pigmice.frc.robot.autonomous.actions.IAction;
import com.pigmice.frc.robot.autonomous.actions.SpinUp;
import com.pigmice.frc.robot.autonomous.tasks.PathFollower;
import com.pigmice.frc.robot.autonomous.tasks.Shoot;
import com.pigmice.frc.robot.subsystems.Drivetrain;
import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Intake;
import com.pigmice.frc.robot.subsystems.Shooter;

public class TrenchSixBall extends Autonomous {
    public TrenchSixBall(Drivetrain drivetrain, Shooter shooter, Feeder feeder, Intake intake) {
        PathFollower acquisition = constructAcquisition(drivetrain, intake, feeder);
        PathFollower returnAndSpinUp = constructReturnPath(drivetrain, shooter, intake, feeder);

        this.subroutines = Arrays.asList(new Shoot(shooter, feeder, intake, 1.2, 140), acquisition, returnAndSpinUp,
                new Shoot(shooter, feeder, intake, 3, 140));
    }

    public void initialize() {
        super.initialize();
    }

    public String name() {
        return "Trench Six Ball";
    }

    public static PathFollower constructAcquisition(Drivetrain drivetrain, Intake intake, Feeder feeder) {
        List<Point> positions = new ArrayList<>();
        positions.add(new Point(0.0, 0.0));
        positions.add(new Point(0.4, -0.7));
        positions.add(new Point(1.2, -0.9));
        positions.add(new Point(1.2, -1.2));
        positions.add(new Point(1.25, -1.5));
        positions.add(new Point(1.25, -2.0));
        positions.add(new Point(1.25, -5.0));
        positions.add(new Point(1.25, -5.6));

        List<Double> velocities = new ArrayList<>();
        velocities.add(3.0);
        velocities.add(3.0);
        velocities.add(3.0);
        velocities.add(2.5);
        velocities.add(1.25);
        velocities.add(1.65);
        velocities.add(1.65);
        velocities.add(1.25);

        Path path = new Path(positions, velocities);

        IAction powerCellAcquisition = new Acquire(intake, feeder, true);

        PathFollower follower = new PathFollower(drivetrain, path, true, 1.5);
        follower.addAction(0, 6, powerCellAcquisition);
        return follower;
    }

    public static PathFollower constructReturnPath(Drivetrain drivetrain, Shooter shooter, Intake intake,
            Feeder feeder) {
        List<Point> positions = new ArrayList<>();
        positions.add(new Point(1.2, -5.75));
        positions.add(new Point(1.0, -4.5));
        positions.add(new Point(0.8, -3.5));
        positions.add(new Point(0.7, -3.0));
        positions.add(new Point(0.6, -2.5));
        positions.add(new Point(0.5, -2.0));
        positions.add(new Point(0.4, -1.5));

        List<Double> velocities = new ArrayList<>();
        velocities.add(0.5);
        velocities.add(2.0);
        velocities.add(3.0);
        velocities.add(3.0);
        velocities.add(2.0);
        velocities.add(1.0);
        velocities.add(0.75);

        Path path = new Path(positions, velocities);

        IAction spinUp = new SpinUp(shooter, feeder, 140);
        IAction powerCellAcquisition = new Acquire(intake, feeder, false);
        IAction clearShooter = new ClearShooter(shooter, feeder);

        PathFollower follower = new PathFollower(drivetrain, path, false, 1.0);
        follower.addAction(0, 2, powerCellAcquisition);
        follower.addAction(3, 3, clearShooter);
        follower.addAction(4, 6, spinUp);
        return follower;
    }
}
