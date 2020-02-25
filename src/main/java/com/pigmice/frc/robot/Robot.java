package com.pigmice.frc.robot;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.pigmice.frc.robot.autonomous.Autonomous;
import com.pigmice.frc.robot.autonomous.LeaveLine;
import com.pigmice.frc.robot.autonomous.TrenchFiveBall;
import com.pigmice.frc.robot.autonomous.TrenchSixBall;
import com.pigmice.frc.robot.subsystems.Climber;
import com.pigmice.frc.robot.subsystems.Drivetrain;
import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.Feeder.LiftAction;
import com.pigmice.frc.robot.subsystems.ISubsystem;
import com.pigmice.frc.robot.subsystems.Intake;
import com.pigmice.frc.robot.subsystems.Shooter;
import com.pigmice.frc.robot.subsystems.Shooter.Action;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
    private Drivetrain drivetrain;
    private Shooter shooter;
    private Intake intake;
    private Feeder feeder;
    private Climber climber;

    private final List<ISubsystem> subsystems = new ArrayList<>();

    private final Controls controls = new Controls();

    private List<Autonomous> autoRoutines = new ArrayList<>();
    private Autonomous autonomous;

    @Override
    public void robotInit() {
        displayDeployTimestamp();

        drivetrain = setupDrivetrain();
        shooter = setupShooter();
        feeder = setupFeeder();
        intake = setupIntake();
        climber = setupClimber();

        subsystems.add(drivetrain);
        subsystems.add(shooter);
        subsystems.add(feeder);
        subsystems.add(intake);
        subsystems.add(climber);

        subsystems.forEach((ISubsystem subsystem) -> subsystem.initialize());

        autoRoutines.add(new TrenchSixBall(drivetrain, shooter, feeder, intake));
        autoRoutines.add(new TrenchFiveBall(drivetrain, shooter, feeder, intake));
        autoRoutines.add(new LeaveLine(drivetrain));
        Autonomous.setOptions(autoRoutines);
    }

    @Override
    public void autonomousInit() {
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
        subsystems.forEach((ISubsystem subsystem) -> subsystem.initialize());
    }

    @Override
    public void teleopPeriodic() {
        controls.update();

        subsystems.forEach((ISubsystem subsystem) -> subsystem.updateInputs());

        drivetrain.arcadeDrive(controls.driveSpeed(), controls.turnSpeed());

        feeder.runHopper(controls.feed() || controls.intake());
        feeder.runLift(
                controls.feed() ? LiftAction.FEED : (controls.backFeed() ? LiftAction.BACKFEED : LiftAction.HOLD));

        intake.run(controls.intake());
        intake.setPosition(controls.intake() ? Intake.Position.DOWN : Intake.Position.UP);

        shooter.run(controls.shoot() ? Action.LONG_SHOT : Action.HOLD);
        shooter.setHood(controls.extendHood());

        if(controls.climbUp()) {
            climber.driveUp();
        } else if(controls.climbDown()) {
            climber.driveDown();
        } else {
            climber.stop();
        }

        subsystems.forEach((ISubsystem subsystem) -> subsystem.updateOutputs());
        subsystems.forEach((ISubsystem subsystem) -> subsystem.updateDashboard());
    }

    @Override
    public void testInit() {
        subsystems.forEach((ISubsystem subsystem) -> subsystem.test());
    }

    @Override
    public void testPeriodic() {
    }

    @Override
    public void disabledPeriodic() {
        subsystems.forEach((ISubsystem subsystem) -> subsystem.updateInputs());
        subsystems.forEach((ISubsystem subsystem) -> subsystem.updateDashboard());
    }

    private void displayDeployTimestamp() {
        FileInputStream file;
        Properties properties = new Properties();

        try {
            Path filePath = Filesystem.getDeployDirectory().toPath().resolve("deployTimestamp.properties");
            file = new FileInputStream(filePath.toFile());
            properties.load(file);
        } catch (Exception e) {
            SmartDashboard.putString("Deploy Timestamp", "none");
            return;
        }

        SmartDashboard.putString("Deploy Timestamp", properties.getProperty("DEPLOY_TIMESTAMP"));
    }

    private Drivetrain setupDrivetrain() {
        CANSparkMax frontRight = new CANSparkMax(1, MotorType.kBrushless);
        CANSparkMax backRight = new CANSparkMax(2, MotorType.kBrushless);
        CANSparkMax frontLeft = new CANSparkMax(3, MotorType.kBrushless);
        CANSparkMax backLeft = new CANSparkMax(4, MotorType.kBrushless);

        frontRight.setInverted(true);
        backLeft.follow(frontLeft);
        backRight.follow(frontRight);

        AHRS navx = new AHRS(SPI.Port.kMXP);

        return new Drivetrain(frontLeft, frontRight, navx);
    }

    private Shooter setupShooter() {
        CANSparkMax motor = new CANSparkMax(5, MotorType.kBrushless);
        CANSparkMax follower = new CANSparkMax(6, MotorType.kBrushless);

        motor.setInverted(false);

        follower.follow(motor, true);

        return new Shooter(motor, new DoubleSolenoid(3, 2));
    }

    private Intake setupIntake() {
        TalonSRX motor = new TalonSRX(7);

        motor.setInverted(true);

        return new Intake(motor, new DoubleSolenoid(1, 0));
    }

    private Feeder setupFeeder() {
        TalonSRX liftLeader = new TalonSRX(6);
        TalonSRX liftFollower = new TalonSRX(3);
        liftFollower.follow(liftLeader);
        liftFollower.setInverted(true);

        TalonSRX hopperLeader = new TalonSRX(2);
        TalonSRX hopperFollower = new TalonSRX(5);
        hopperFollower.follow(hopperLeader);
        hopperFollower.setInverted(true);

        return new Feeder(hopperLeader, liftLeader);
    }

    private Climber setupClimber() {
        TalonSRX climberLeader = new TalonSRX(1);
        TalonSRX climberFollower = new TalonSRX(4);

        climberFollower.follow(climberLeader);
        climberFollower.setInverted(true);

        return new Climber(climberLeader, new DoubleSolenoid(5, 4));
    }
}
