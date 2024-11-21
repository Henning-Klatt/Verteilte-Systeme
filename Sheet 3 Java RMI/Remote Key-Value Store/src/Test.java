public class Test {

    public static void main(String[] args) {
        String hostname = "localhost";
        CachedRMIClient CachedRMIClient_1 = new CachedRMIClient(hostname, 41337);
        CachedRMIClient CachedRMIClient_2 = new CachedRMIClient(hostname, 41337);
    }

}
