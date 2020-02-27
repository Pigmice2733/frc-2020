package com.pigmice.frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;

public class MotorTester {
    public enum TestStatus {
        UNKNOWN, PASS, FAIL
    }

    public static class Config {
        private final double percentOutput;
        private final double minimumCurrent;
        private final double duration;

        public Config(double percentOutput, double minimumCurrent, double duration) {
            this.percentOutput = percentOutput;
            this.minimumCurrent = minimumCurrent;
            this.duration = duration;
        }
    }

    public static TestStatus Test(TalonSRX motor, Config config, double currentTestTime) {
        motor.set(ControlMode.PercentOutput, config.percentOutput);

        if (currentTestTime > config.duration) {
            return (motor.getSupplyCurrent() >= config.minimumCurrent) ? TestStatus.PASS : TestStatus.FAIL;
        }

        return TestStatus.UNKNOWN;
    }

    public static TestStatus Test(CANSparkMax motor, Config config, double currentTestTime) {
        motor.set(config.percentOutput);

        if (currentTestTime > config.duration) {
            return (motor.getOutputCurrent() >= config.minimumCurrent) ? TestStatus.PASS : TestStatus.FAIL;
        }

        return TestStatus.UNKNOWN;
    }
}
