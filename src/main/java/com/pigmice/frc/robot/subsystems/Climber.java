package com.pigmice.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.pigmice.frc.robot.subsystems.SystemConfig.ClimberConfiguration;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber implements ISubsystem {
    private final TalonSRX motor;
    private final TalonSRX follower;

    private final double upSpeed = 0.5;
    private double downSpeed = -0.4;

    private double speed = 0.0;

    private static Climber instance = null;

    public static Climber getInstance() {
        if (instance == null) {
            instance = new Climber();
        }

        return instance;
    }

    public Climber() {
        motor = new TalonSRX(ClimberConfiguration.leaderMotorPort);
        motor.setInverted(ClimberConfiguration.leaderInverted);

        follower = new TalonSRX(ClimberConfiguration.followerMotorPort);
        follower.follow(motor);
        follower.setInverted(ClimberConfiguration.followerInverted);

        SmartDashboard.putNumber("Climber power", 0.0);
    }

    @Override
    public void initialize() {
        speed = 0.0;
    }

    public void driveUp() {
        speed = upSpeed;
    }

    public void driveDown() {
        speed = downSpeed;
    }

    public void stop() {
        speed = 0.0;
    }

    @Override
    public void updateDashboard() {
    }

    @Override
    public void updateInputs() {
        downSpeed = SmartDashboard.getNumber("Climber power", 0.0);
    }

    @Override
    public void updateOutputs() {
        motor.set(ControlMode.PercentOutput, speed);
    }

    @Override
    public void test(double currentTestTime) {
    }
}
