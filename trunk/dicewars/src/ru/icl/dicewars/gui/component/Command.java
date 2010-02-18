package ru.icl.dicewars.gui.component;


public interface Command {
    public final static Command BLANK = new Command() {
        public void execute() {}
    };
    
    public void execute();
}
