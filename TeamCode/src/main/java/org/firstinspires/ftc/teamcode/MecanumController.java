package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

// Class dedicated to turning joystick input into motor commands
public class MecanumController {
    LinearOpMode opMode;    // May not be neccasary

    // 40:1 Dc motors driving the wheels
    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;

    double driveSpeed;

    public MecanumController(LinearOpMode opMode, DcMotor frontLeftDrive, DcMotor frontRightDrive, DcMotor backLeftDrive,
                             DcMotor backRightDrive) {
        this.opMode = opMode;

        this.frontLeftDrive = frontLeftDrive;
        this.frontRightDrive = frontRightDrive;
        this.backLeftDrive = backLeftDrive;
        this.backRightDrive = backRightDrive;

        this.driveSpeed = -1;
    }

    public double move(double x, double y, double rx) {
        double _y = -y;
        double _x = x * 1.1;
        double _rx = rx * 0.8;

        // Denom ensures that the power is in a range [-1, 1] while keeping the same ratio
        double denom = Math.max(Math.abs(_y) + Math.abs(_x) + Math.abs(_rx), 1);

        // Set motor powers
        this.frontLeftDrive.setPower(driveSpeed * (_x + _y + _rx) / denom);
        this.frontRightDrive.setPower(driveSpeed * (_x - _y + _rx) / denom);
        this.backLeftDrive.setPower(driveSpeed * (_x - _y - _rx) / denom);
        this.backRightDrive.setPower(driveSpeed * (_x + _y - _rx) / denom);

        return (_y + x + rx) / denom;
    }

    public double setSpeed(double speed) {
        driveSpeed = -speed;
        return speed;
    }
}