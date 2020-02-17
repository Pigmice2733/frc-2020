package com.pigmice.frc.robot;

import java.util.ArrayList;
import java.util.List;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.pigmice.frc.robot.autonomous.Autonomous;
import com.pigmice.frc.robot.autonomous.Test;
import com.pigmice.frc.robot.subsystems.Drivetrain;
import com.pigmice.frc.robot.subsystems.Feeder;
import com.pigmice.frc.robot.subsystems.ISubsystem;
import com.pigmice.frc.robot.subsystems.Intake;
import com.pigmice.frc.robot.subsystems.Shooter;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {
    private Drivetrain drivetrain;
    private Shooter shooter;
    private Intake intake;
    private Feeder feeder;

    private final List<ISubsystem> subsystems = new ArrayList<>();

    private final Controls controls = new Controls();

    private Autonomous autonomous;

    @Override
    public void robotInit() {
        drivetrain = setupDrivetrain();
        shooter = setupShooter();
        feeder = setupFeeder();
        intake = setupIntake();

        subsystems.add(drivetrain);
        subsystems.add(shooter);
        subsystems.add(feeder);
        subsystems.add(intake);

        subsystems.forEach((ISubsystem subsystem) -> subsystem.initialize());

        autonomous = new Test(drivetrain);
    }

    @Override
    public void autonomousInit() {
        subsystems.forEach((ISubsystem subsystem) -> subsystem.initialize());

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
    public void teleopPeriodic() {
        subsystems.forEach((ISubsystem subsystem) -> subsystem.updateInputs());

        if(controls.demoMode()) {
            drivetrain.arcadeDrive(0.2 * controls.driveSpeed(), 0.2 * controls.turnSpeed());
        } else {
            drivetrain.arcadeDrive(controls.driveSpeed(), controls.turnSpeed());
        }

        if(controls.feed()) {
            feeder.go(0.4);
        } else {
            feeder.go(0.0);
        }

        if (controls.intake()) {
            intake.go(0.6);
        } else {
            intake.go(0.0);
        }

        if(controls.shoot()) {
            shooter.go();
        } else {
            shooter.stop();
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

    public Drivetrain setupDrivetrain() {
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

        follower.follow(motor, true);

        return new Shooter(motor);
    }

    private Intake setupIntake() {
        TalonSRX motor = new TalonSRX(3);

        return new Intake(motor);
    }

    private Feeder setupFeeder() {
        TalonSRX motor = new TalonSRX(1);
        TalonSRX follower = new TalonSRX(2);

        motor.setInverted(true);

        follower.follow(motor);

        return new Feeder(motor);
    }
}
