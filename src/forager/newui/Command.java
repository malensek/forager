package forager.newui;

interface Command {

    public abstract void execute(String[] args) throws Exception;

    public abstract String name();

}
