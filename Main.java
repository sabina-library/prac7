import java.util.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("===== COMMAND PATTERN =====");

        RemoteControl remote = new RemoteControl(3);

        Light light = new Light();
        TV tv = new TV();
        AirConditioner ac = new AirConditioner();

        remote.setCommand(0, new LightOnCommand(light), new LightOffCommand(light));
        remote.setCommand(1, new TVOnCommand(tv), new TVOffCommand(tv));
        remote.setCommand(2, new ACOnCommand(ac), new ACOffCommand(ac));

        remote.pressOn(0);
        remote.pressOff(0);
        remote.undo();
        remote.redo();

        System.out.println("\n===== TEMPLATE METHOD =====");

        ReportGenerator pdf = new PdfReport();
        pdf.generate();

        ReportGenerator excel = new ExcelReport();
        excel.generate();

        System.out.println("\n===== MEDIATOR PATTERN =====");

        ChatMediator mediator = new ChatMediator();

        ChatUser u1 = new ChatUser("Alice", mediator);
        ChatUser u2 = new ChatUser("Bob", mediator);
        ChatUser u3 = new ChatUser("Charlie", mediator);

        mediator.addUser(u1);
        mediator.addUser(u2);
        mediator.addUser(u3);

        u1.send("Hello everyone!");
        mediator.removeUser(u3);
        u2.send("Bye Charlie!");
    }
}

interface Command {
    void execute();
    void undo();
}

class Light {
    void on() { System.out.println("Light ON"); }
    void off() { System.out.println("Light OFF"); }
}

class TV {
    void on() { System.out.println("TV ON"); }
    void off() { System.out.println("TV OFF"); }
}

class AirConditioner {
    void on() { System.out.println("AC ON"); }
    void off() { System.out.println("AC OFF"); }
}

class LightOnCommand implements Command {
    private Light light;
    LightOnCommand(Light light) { this.light = light; }
    public void execute() { light.on(); }
    public void undo() { light.off(); }
}

class LightOffCommand implements Command {
    private Light light;
    LightOffCommand(Light light) { this.light = light; }
    public void execute() { light.off(); }
    public void undo() { light.on(); }
}

class TVOnCommand implements Command {
    private TV tv;
    TVOnCommand(TV tv) { this.tv = tv; }
    public void execute() { tv.on(); }
    public void undo() { tv.off(); }
}

class TVOffCommand implements Command {
    private TV tv;
    TVOffCommand(TV tv) { this.tv = tv; }
    public void execute() { tv.off(); }
    public void undo() { tv.on(); }
}

class ACOnCommand implements Command {
    private AirConditioner ac;
    ACOnCommand(AirConditioner ac) { this.ac = ac; }
    public void execute() { ac.on(); }
    public void undo() { ac.off(); }
}

class ACOffCommand implements Command {
    private AirConditioner ac;
    ACOffCommand(AirConditioner ac) { this.ac = ac; }
    public void execute() { ac.off(); }
    public void undo() { ac.on(); }
}

class RemoteControl {

    private Command[] onCommands;
    private Command[] offCommands;
    private Stack<Command> undoStack = new Stack<>();
    private Stack<Command> redoStack = new Stack<>();

    RemoteControl(int slots) {
        onCommands = new Command[slots];
        offCommands = new Command[slots];

        for (int i = 0; i < slots; i++) {
            onCommands[i] = new NoCommand();
            offCommands[i] = new NoCommand();
        }
    }

    void setCommand(int slot, Command on, Command off) {
        onCommands[slot] = on;
        offCommands[slot] = off;
    }

    void pressOn(int slot) {
        onCommands[slot].execute();
        undoStack.push(onCommands[slot]);
        redoStack.clear();
    }

    void pressOff(int slot) {
        offCommands[slot].execute();
        undoStack.push(offCommands[slot]);
        redoStack.clear();
    }

    void undo() {
        if (!undoStack.isEmpty()) {
            Command cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
        } else {
            System.out.println("Nothing to undo");
        }
    }

    void redo() {
        if (!redoStack.isEmpty()) {
            Command cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
        } else {
            System.out.println("Nothing to redo");
        }
    }
}

class NoCommand implements Command {
    public void execute() { System.out.println("No command assigned"); }
    public void undo() { }
}

abstract class ReportGenerator {

    final void generate() {
        fetchData();
        formatData();
        createHeader();
        saveReport();
    }

    void fetchData() { System.out.println("Fetching data..."); }
    abstract void formatData();
    abstract void createHeader();
    void saveReport() { System.out.println("Saving report..."); }
}

class PdfReport extends ReportGenerator {
    void formatData() { System.out.println("Formatting PDF data"); }
    void createHeader() { System.out.println("Creating PDF header"); }
}

class ExcelReport extends ReportGenerator {
    void formatData() { System.out.println("Formatting Excel data"); }
    void createHeader() { System.out.println("Creating Excel header"); }
    void saveReport() { System.out.println("Saving Excel file (.xlsx)"); }
}

interface Mediator {
    void sendMessage(String message, ChatUser sender);
    void addUser(ChatUser user);
    void removeUser(ChatUser user);
}

class ChatMediator implements Mediator {

    private List<ChatUser> users = new ArrayList<>();

    public void addUser(ChatUser user) {
        users.add(user);
        System.out.println(user.getName() + " joined chat");
    }

    public void removeUser(ChatUser user) {
        users.remove(user);
        System.out.println(user.getName() + " left chat");
    }

    public void sendMessage(String message, ChatUser sender) {
        for (ChatUser user : users) {
            if (user != sender) {
                user.receive(sender.getName() + ": " + message);
            }
        }
    }
}

class ChatUser {

    private String name;
    private Mediator mediator;

    ChatUser(String name, Mediator mediator) {
        this.name = name;
        this.mediator = mediator;
    }

    String getName() { return name; }

    void send(String message) {
        mediator.sendMessage(message, this);
    }

    void receive(String message) {
        System.out.println(name + " received -> " + message);
    }

}
