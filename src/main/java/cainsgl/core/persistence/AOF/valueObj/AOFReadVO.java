package cainsgl.core.persistence.AOF.valueObj;

public class AOFReadVO implements Comparable<AOFReadVO> {

    private long commandTime;

    private byte[] command;

    private byte[][] args;

    public AOFReadVO() {
    }

    public AOFReadVO(long commandTime, byte[] command, byte[][] args) {
        this.commandTime = commandTime;
        this.command = command;
        this.args = args;
    }

    public long getCommandTime() {
        return commandTime;
    }

    public void setCommandTime(long commandTime) {
        this.commandTime = commandTime;
    }

    public byte[] getCommand() {
        return command;
    }

    public void setCommand(byte[] command) {
        this.command = command;
    }

    public byte[][] getArgs() {
        return args;
    }

    public void setArgs(byte[][] args) {
        this.args = args;
    }

    @Override
    public int compareTo(AOFReadVO other) {
        return Long.compare(commandTime, other.commandTime);
    }
}
