package com.pigmice.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Intake implements ISubsystem {
    private final TalonSRX motor;

    private double speed = 0.0;

    public Intake(TalonSRX motor) {
        this.motor = motor;
    }

    @Override
    public void initialize() {
        speed = 0.0;
    }

    public void go(double speed) {
        this.speed = speed;
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
    }

    @Override
    public void test() {
        // TODO Auto-generated method stub

    }


}
