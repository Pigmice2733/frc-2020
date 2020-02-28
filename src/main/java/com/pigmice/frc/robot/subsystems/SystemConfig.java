package com.pigmice.frc.robot.subsystems;

import edu.wpi.first.wpilibj.SPI;

public class SystemConfig {
    public static class DrivetrainConfiguration {
        public static final int frontRightMotorPort = 1;
        public static final int backRightMotorPort = 2;
        public static final int frontLeftMotorPort = 3;
        public static final int backLeftMotorPort = 4;

        public static final SPI.Port navxPort = SPI.Port.kMXP;

        public static final double rotationToDistanceConversion = 16.13;
        public static final double wheelBase = 0.603;
    }

    public static class ShooterConfiguration {
        public static final int leaderMotorPort = 5;
        public static final int followerMotorPort = 6;

        public static final int forwardSolenoidPort = 3;
        public static final int reverseSolenoidPort = 2;

        public static final boolean inverted = false;
        public static final double reduction = 1.0 / 1.5;
    }

    public static class IntakeConfiguration {
        public static final int motorPort = 7;
        public static final boolean motorInverted = true;

        public static final int forwardSolenoidPort = 0;
        public static final int reverseSolenoidPort = 1;
    }

    public static class FeederConfiguration {
        public static final int liftLeaderMotorPort = 6;
        public static final int liftFollowerMotorPort = 3;
        public static final boolean liftInverted = false;

        public static final int hopperLeaderMotorPort = 2;
        public static final int hopperFollowerMotorPort = 5;
        public static final boolean hopperInverted = false;
    }

    public static class ClimberConfiguration {
        public static final int leaderMotorPort = 1;
        public static final int followerMotorPort = 4;

        public static final boolean leaderInverted = false;
        public static final boolean followerInverted = true;
    }

    public static class LEDConfiguration {
        public static final int ledPort = 0;
        public static final int ledCount = 38 + 38 + 11;
    }
}
