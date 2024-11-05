public class Test {

    public static void main(String[] args){

        // Start worker thread
        Worker worker = new Worker();
        worker.start();

        // Start controller thread
        Controller controller = new Controller();
        controller.start();

    }
}
