package com.pigmice.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.pigmice.frc.lib.controllers.TakeBackHalf;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter implements ISubsystem {
    private final TalonSRX motor;
    private final Encoder encoder;

    private double shooterRPM = 0.0;
    private double shooterVoltage = 0.0;

    private TakeBackHalf controller = new TakeBackHalf(1.0e-5, 0.68);

    public Shooter(Encoder encoder, TalonSRX motor) {
        this.encoder = encoder;
        this.motor = motor;
    }

    @Override
    public void initialize() {
        encoder.setDistancePerPulse(1.0/2048.0);
        encoder.reset();

        shooterRPM = 0.0;
        shooterVoltage = 0.0;

        updateDashboard();
    }

    @Override
    public void updateDashboard() {
        SmartDashboard.putNumber("Shooter RPM", shooterRPM);
        SmartDashboard.putNumber("Shooter Voltage", shooterVoltage);

        shooterVoltage = 0.0;
    }

    @Override
    public void updateInputs() {
        shooterRPM = encoder.getRate() * 60.0;
    }

    @Override
    public void updateOutputs() {
        double output = controller.calculateOutput(shooterRPM, 4000);
        shooterVoltage = output * 100.0;
        motor.set(ControlMode.PercentOutput, output);
    }

    @Override
    public void test() {
    }
}
