package com.pigmice.frc.robot;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.pigmice.frc.robot.autonomous.Autonomous;
import com.pigmice.frc.robot.autonomous.LeaveLine;
import com.pigmice.frc.robot.autonomous.TrenchFiveBall;
import com.pigmice.frc.robot.autonomous.TrenchFiveBallAngled;
import com.pigmice.frc.robot.autonomous.TrenchSixBall;
import com.pigmice.frc.robot.autonomous.VisionTest;
import com.pigmice.frc.robot.subsystems.Climber;
import com.pigmice.frc.robot.subsystems.Drivetrain;
import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Feeder.LiftAction;
import com.pigmice.frc.robot.subsystems.ISubsystem;
import com.pigmice.frc.robot.subsystems.Intake;
import com.pigmice.frc.robot.subsystems.LEDs;
import com.pigmice.frc.robot.subsystems.Shooter;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

public class Robot extends TimedRobot {
    private Drivetrain drivetrain;
    private Shooter shooter;
    private Intake intake;
    private Feeder feeder;
    private Climber climber;
    private LEDs leds;

    private final List<ISubsystem> subsystems = new ArrayList<>();

    private final Controls controls = new Controls();

    private List<Autonomous> autoRoutines = new ArrayList<>();
    private Autonomous autonomous;

    private double testStartTime;

    @Override
    public void robotInit() {
        displayDeployTimestamp();

        drivetrain = Drivetrain.getInstance();
        shooter = Shooter.getInstance();
        feeder = Feeder.getInstance();
        intake = Intake.getInstance();
        climber = Climber.getInstance();
        leds = LEDs.getInstance();

        subsystems.add(drivetrain);
        subsystems.add(shooter);
        subsystems.add(feeder);
        subsystems.add(intake);
        subsystems.add(climber);

        subsystems.forEach((ISubsystem subsystem) -> subsystem.initialize());

        autoRoutines.add(new TrenchSixBall(drivetrain, shooter, feeder, intake));
        autoRoutines.add(new TrenchFiveBall(drivetrain, shooter, feeder, intake));
        autoRoutines.add(new TrenchFiveBallAngled(drivetrain, shooter, feeder, intake));
        autoRoutines.add(new LeaveLine(drivetrain));
        autoRoutines.add(new VisionTest(drivetrain, shooter, feeder, intake));
        Autonomous.setOptions(autoRoutines);
    }

    @Override
    public void autonomousInit() {
        drivetrain.setCoastMode(false);
        subsystems.forEach((ISubsystem subsystem) -> subsystem.initialize());

        autonomous = Autonomous.getSelected();
        autonomous.initialize();
    }

    @Override
    public void autonomousPeriodic() {
        subsystems.forEach((ISubsystem subsystem) -> subsystem.updateInputs());

        autonomous.update();

        subsystems.forEach((ISubsystem subsystem) -> subsystem.updateOutputs());
        subsystems.forEach((ISubsystem subsystem) -> subsystem.updateDashboard());
    }

    @Override
    public void teleopInit() {
        drivetrain.setCoastMode(false);
        subsystems.forEach((ISubsystem subsystem) -> subsystem.initialize());
    }

    @Override
    public void teleopPeriodic() {
        controls.update();

        if(controls.visionAlign() || controls.shoot()) {
            Vision.update();
        } else {
            Vision.stop();
        }

        subsystems.forEach((ISubsystem subsystem) -> subsystem.updateInputs());

        if (controls.visionAlign()) {
            double output = Vision.pidOutput();
            drivetrain.arcadeDrive(controls.driveSpeed(), output);
        } else {
            drivetrain.arcadeDrive(controls.driveSpeed(), controls.turnSpeed());
        }

        feeder.runHopper(controls.feed() || controls.intake());
        feeder.runLift(
                controls.feed() ? LiftAction.FEED : (controls.backFeed() ? LiftAction.BACKFEED : LiftAction.HOLD));

        intake.run(controls.intake());
        intake.setPosition(controls.intake() ? Intake.Position.DOWN : Intake.Position.UP);

        if(controls.shoot()) {
            if(Vision.targetIsVisible()) {
                shooter.setRange(Vision.targetDistance());
            } else {
                shooter.setRange(20 * 12);
            }
        } else if(controls.backFeed()) {
            shooter.clear();
        } else {
            shooter.stop();
        }

        if (controls.climbUp()) {
            climber.driveUp();
        } else if (controls.climbDown()) {
            climber.driveDown();
        } else {
            climber.stop();
        }

        if(controls.speedDown()) {
            shooter.downPower();
        } else if(controls.speedUp()) {
            shooter.upPower();
        }

        subsystems.forEach((ISubsystem subsystem) -> subsystem.updateOutputs());
        subsystems.forEach((ISubsystem subsystem) -> subsystem.updateDashboard());
    }

    @Override
    public void testInit() {
        testStartTime = Timer.getFPGATimestamp();
    }

    @Override
    public void testPeriodic() {
        subsystems.forEach((ISubsystem subsystem) -> subsystem.test(Timer.getFPGATimestamp() - testStartTime));
    }

    @Override
    public void disabledInit() {
        drivetrain.setCoastMode(true);
    }

    @Override
    public void disabledPeriodic() {
        subsystems.forEach((ISubsystem subsystem) -> subsystem.updateInputs());
        subsystems.forEach((ISubsystem subsystem) -> subsystem.updateDashboard());
    }

    @Override
    public void robotPeriodic() {
        leds.updateOutputs();
    }

    private void displayDeployTimestamp() {
        FileInputStream file;
        Properties properties = new Properties();

        NetworkTableEntry timestampDisplay = Shuffleboard.getTab(Dashboard.developmentTabName)
                .add("Deploy Timestamp", "none").withSize(2, 1).withPosition(Dashboard.deployTimestampPosition, 0)
                .getEntry();

        try {
            Path filePath = Filesystem.getDeployDirectory().toPath().resolve("deployTimestamp.properties");
            file = new FileInputStream(filePath.toFile());
            properties.load(file);
        } catch (Exception e) {
            return;
        }

        timestampDisplay.forceSetString(properties.getProperty("DEPLOY_TIMESTAMP"));
    }
}
