Log file location on RIO: /var/local/natinst/log/FRC_UserProgram.log

- "Output not updated often enough" error will occur when there are no subsystems
using the robot.  We were doing an experiment with not making the DrivetrainSubsystem
a subclass of Subsystem, and this error started right away.  We couldn't even use
the robot again in Teleop until I put the subclass of DrivetrainSubsystem -> Subsystem back
in place.