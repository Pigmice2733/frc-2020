package com.pigmice.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber implements ISubsystem {
    private final TalonSRX motor;
    private final DoubleSolenoid pistons;

    private final double upSpeed = 0.5;
    private double downSpeed = -0.4;

    private double speed = 0.0;
    private boolean throwPistons = false;

    public Climber(TalonSRX motor, DoubleSolenoid pistons) {
        this.motor = motor;
        this.pistons = pistons;

        SmartDashboard.putNumber("Climber power", 0.0);
    }

    @Override
    public void initialize() {
        speed = 0.0;
    }

    public void driveUp() {
        speed = upSpeed;
        throwPistons = true;
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

        if(pistons.get() == Value.kOff) {
            pistons.set(Value.kForward);
        }

        if(pistons.get() == Value.kForward && throwPistons) {
            pistons.set(Value.kReverse);
        }
    }

    @Override
    public void test() {
    }
}
