package ru.icl.dicewars.gui.component;


public interface Command {
    public static Command Blank = new Command() {
        public void execute() {}
    };
    public void execute();
}
