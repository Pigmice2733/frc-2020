package com.pigmice.frc.robot;

import java.util.ArrayList;
import java.util.List;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.pigmice.frc.robot.autonomous.Autonomous;
import com.pigmice.frc.robot.autonomous.Rotate;
import com.pigmice.frc.robot.subsystems.Drivetrain;
import com.pigmice.frc.robot.subsystems.ISubsystem;
import com.pigmice.frc.robot.subsystems.Intake;
import com.pigmice.frc.robot.subsystems.Shooter;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {
    private Drivetrain drivetrain;
    private final Intake intake = setupIntake();

    private final List<ISubsystem> subsystems = new ArrayList<>();

    private final Controls controls = new Controls();

    private Autonomous autonomous;

    @Override
    public void robotInit() {
        drivetrain = setupDrivetrain();
        subsystems.add(drivetrain);
        subsystems.add(intake);

        subsystems.forEach((ISubsystem subsystem) -> subsystem.initialize());

        autonomous = new Rotate(drivetrain);

        //CameraServer.getInstance().startAutomaticCapture("Driver Cam", 0);

        //Vision.startProcessing();
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
            drivetrain.arcadeDrive(0.4 * controls.driveSpeed(), 0.4 * controls.turnSpeed());
        } else {
            drivetrain.arcadeDrive(controls.driveSpeed(), controls.turnSpeed());
        }

        if(controls.intake()) {
            double speed = controls.demoMode() ? 0.35 : 0.6;
            intake.go(speed);
        } else {
            intake.go(0.0);
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

    @Override
    public void robotPeriodic() {
        //SmartDashboard.putNumber("Target angle", Vision.getAngle());
        //SmartDashboard.putNumber("Target distance", Vision.getDistance());
    }

    public Drivetrain setupDrivetrain() {
        CANSparkMax frontLeft = new CANSparkMax(2, MotorType.kBrushless);
        CANSparkMax frontRight = new CANSparkMax(3, MotorType.kBrushless);
        CANSparkMax backLeft = new CANSparkMax(1, MotorType.kBrushless);
        CANSparkMax backRight = new CANSparkMax(4, MotorType.kBrushless);

        frontRight.setInverted(true);
        backLeft.follow(frontLeft);
        backRight.follow(frontRight);

        AHRS navx = new AHRS(SPI.Port.kMXP);

        return new Drivetrain(frontLeft, frontRight, navx);
    }

    private Shooter setupShooter() {
        TalonSRX motor = new TalonSRX(1);
        TalonSRX follower = new TalonSRX(2);

        motor.setInverted(true);

        follower.follow(motor);
        follower.setInverted(true);

        Encoder encoder = new Encoder(0,1);

        return new Shooter(encoder, motor);
    }

    private Intake setupIntake() {
        TalonSRX motor = new TalonSRX(3);

        return new Intake(motor);
    }
}
