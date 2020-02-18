package com.pigmice.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Intake implements ISubsystem {
    private final TalonSRX motor;
    private final DoubleSolenoid solenoid;

    private double speed = 0.0;
    private Value targetPistonState = Value.kOff;
    private Value previousPistonState = Value.kOff;

    public Intake(TalonSRX motor, DoubleSolenoid solenoid) {
        this.motor = motor;
        this.solenoid = solenoid;
    }

    @Override
    public void initialize() {
        speed = 0.0;
        targetPistonState = solenoid.get();
        previousPistonState = targetPistonState;
    }

    public void go(double speed) {
        this.speed = speed;
    }

    public void setPosition(boolean down) {
        targetPistonState = down ? Value.kReverse : Value.kForward;
    }

    @Override
    public void updateDashboard() {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateInputs() {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateOutputs() {
        motor.set(ControlMode.PercentOutput, -speed);

        if (targetPistonState != previousPistonState) {
            solenoid.set(targetPistonState);
            previousPistonState = targetPistonState;
        }
    }

    @Override
    public void test() {
        // TODO Auto-generated method stub

    }

}
