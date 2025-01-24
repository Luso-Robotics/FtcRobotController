
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/*
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When a selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

@TeleOp
public class ReginaldOp extends LinearOpMode {

    @Override
    public void runOpMode() {

        float grabOffset = 0.9f;
        double driveSpeed = 1;
        int manualArm = 0;

        Gamepad currentGamepad1 = new Gamepad();
        Gamepad currentGamepad2 = new Gamepad();


        Gamepad previousGamepad1 = new Gamepad();
        Gamepad previousGamepad2 = new Gamepad();

        DcMotor pivot = this.hardwareMap.get(DcMotor.class, "pivot");
        DcMotor arm = this.hardwareMap.get(DcMotor.class, "arm");
        DcMotor grabWheel1 = this.hardwareMap.get(DcMotor.class, "grabWheel1");
        DcMotor grabWheel2 = this.hardwareMap.get(DcMotor.class, "grabWheel2");
        DcMotor frontRightDrive = this.hardwareMap.get(DcMotor.class, "front_right_drive");
        DcMotor frontLeftDrive = this.hardwareMap.get(DcMotor.class, "front_left_drive");
        DcMotor backRightDrive = this.hardwareMap.get(DcMotor.class, "back_right_drive");
        DcMotor backLeftDrive = this.hardwareMap.get(DcMotor.class, "back_left_drive");

        MecanumController driveController = new MecanumController((LinearOpMode)this, frontLeftDrive, frontRightDrive,
                backLeftDrive, backRightDrive);




        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();

        arm.setTargetPosition(arm.getCurrentPosition());
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm.setPower(1);

        pivot.setTargetPosition(pivot.getCurrentPosition());
        pivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        pivot.setPower(1);



        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {



            previousGamepad1.copy(currentGamepad1);
            previousGamepad2.copy(currentGamepad2);

            currentGamepad1.copy(gamepad1);
            currentGamepad2.copy(gamepad2);


            if(gamepad1.dpad_up && driveSpeed < 1) {
                driveSpeed = driveController.setSpeed(driveSpeed + 0.01);

            } else if (gamepad1.dpad_down && driveSpeed > 0) {
                driveSpeed = driveController.setSpeed(driveSpeed - 0.01);
            }
            if(gamepad2.left_trigger > 0) {
                grabWheel1.setPower(-gamepad2.left_trigger * grabOffset);
                grabWheel2.setPower(gamepad2.left_trigger * grabOffset);
            } else if(gamepad2.right_trigger > 0) {
                grabWheel1.setPower(gamepad2.right_trigger * grabOffset);
                grabWheel2.setPower(-gamepad2.right_trigger * grabOffset);
            } else {
                grabWheel1.setPower(0);
                grabWheel2.setPower(0);
            }

            if(!previousGamepad2.a && currentGamepad2.a) {
                telemetry.addLine("hhi");
                if(pivot.getCurrentPosition() >= 1550 && arm.getCurrentPosition() > -780 && manualArm != 1) {
                    manualArm = 1;
                } else if (pivot.getCurrentPosition() >= 1550 && pivot.getCurrentPosition() <= 1780 && manualArm != 2) {
                    manualArm = 2;
                }
            }

            if(!previousGamepad2.y && currentGamepad2.y) {
                telemetry.addLine("hhi");
                if(manualArm != 0 && arm.getCurrentPosition() > -330) {
                    manualArm = 0;
                } else if (pivot.getCurrentPosition() >= 1550  && arm.getCurrentPosition() > -780 && manualArm != 1) {
                    manualArm = 1;
                }
            }

            if(!previousGamepad2.b && currentGamepad2.b) {
                DcMotor.RunMode armMode = arm.getMode();
                DcMotor.RunMode pivotMode = pivot.getMode();

                arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                pivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                arm.setMode(armMode);
                System.out.println(armMode);
                pivot.setMode(pivotMode);
            }


            driveController.move(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);

            int pivotPos = (pivot.getCurrentPosition() + (int)(70 * -gamepad2.left_stick_y));
            if(pivotPos < 3520 && pivotPos > 0) {
                pivot.setTargetPosition(pivotPos);
            }

            // Restricts pivot movement if manual mode is of to prevent exceed horizontal expansion limit
            // when manual 1 3520 to 1550
            // when manual 2 1780 to 1550
            // when manual 0 3520 t0 0
            //if(true || (!(pivotPos > pivotManual) && !(pivotPos < 1550) && manualArm == 2) || (manualArm == 1 && !(pivotPos > pivotMax) && !(pivotPos < 1550)) || (manualArm == 0 && !(pivotPos > pivotMax) && !(pivotPos < pivotMin))) {
            //pivot.setTargetPosition(pivotPos);
            //}

            int armPos = -arm.getCurrentPosition() + (int)(500 * gamepad2.right_stick_y);
            armPos = clampArm(armPos, pivotPos);
            arm.setTargetPosition(-armPos);

            // Restricts slide position if manual mode is turned on to prevent exceed horizontal expansion limit
            // when manuak is 0 NULL
            // when manual is 1 -780 to 0
            // when manual is 2 -2200 to 0
            //if(true || (!(armPos < armMax) && !(armPos > armMin) && manualArm == 2) || (manualArm == 1 && !(armPos < armManual) && !(armPos > armMin))) {
            //    arm.setTargetPosition(armPos);
            //}



            telemetry.addData("Drive Speed", driveSpeed);
            telemetry.addData("manual Mode", manualArm);
            telemetry.addData("arm", arm.getCurrentPosition());
            telemetry.addData("pivot", pivot.getCurrentPosition());
            telemetry.update();
        }
    }

    public int clampArm(int targetArmPos, int currentPivotPos) {
        int maxArm;
        if(currentPivotPos < 2080) {
            maxArm = (int)(-0.0012 * Math.pow((double)currentPivotPos - 340, 2) ) - 131;
        } else {
            maxArm = (int)(-0.0016 * Math.pow((double)currentPivotPos - 3320, 2) ) - 1211;
        }

        if(maxArm > 0) {
            maxArm = 0;
        }

        telemetry.addData("test", maxArm);

        return Math.max(targetArmPos, maxArm);
    }
}
