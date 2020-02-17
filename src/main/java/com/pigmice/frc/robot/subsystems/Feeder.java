package com.pigmice.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Feeder implements ISubsystem {
    private final TalonSRX hopperMotor;
    private final TalonSRX liftMotor;

    private final double hopperSpeed = 0.15;
    private final double liftSpeed = 0.35;

    private boolean runHopper = false, runLift = false;

    public Feeder(TalonSRX hopperMotor, TalonSRX liftMotor) {
        this.hopperMotor = hopperMotor;
        this.liftMotor = liftMotor;
    }

    @Override
    public void initialize() {
        runHopper = false;
        runLift = false;
    }

    public void feed() {
        runHopper = true;
        runLift = true;
    }

    public void stop() {
        runHopper = false;
        runLift = false;
    }

    @Override
    public void updateDashboard() {
    }

    @Override
    public void updateInputs() {
    }

    @Override
    public void updateOutputs() {
        hopperMotor.set(ControlMode.PercentOutput, runHopper ? hopperSpeed : 0.0);
        liftMotor.set(ControlMode.PercentOutput, runLift ? liftSpeed : 0.0);
    }

    @Override
    public void test() {
    }
}
