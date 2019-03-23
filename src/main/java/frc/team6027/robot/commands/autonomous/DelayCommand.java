package frc.team6027.robot.commands.autonomous;

import edu.wpi.first.wpilibj.command.Command;


public class DelayCommand  extends Command {

    protected int delayMs;
    protected long startTime;
    protected long finishTime; 

    public DelayCommand(int delayMs) {    

        this.setName("No Command");
        this.delayMs = delayMs;

    }
    @Override
    public void start() {
        this.startTime = System.currentTimeMillis();
        this.finishTime = startTime + delayMs;

        }

    @Override
    protected boolean isFinished() {
      if(System.currentTimeMillis() >= finishTime) {
     
        return true;

        }
    else {
        return false;
  }
 }
}