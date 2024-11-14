import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args){

        List<Worker> workers = new ArrayList<Worker>();

        // Start 3 worker threads
        for (int i = 0; i < 3; i++){
            workers.add(new Worker());
            workers.get(i).start();
        }

        // Start controller thread
        Controller controller = new Controller();
        controller.start();

    }
}
