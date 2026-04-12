package mainmod;

import service.HospitalManagementServiceImpl;

public class MainModule {

    public static void main(String[] args) {
        if (args.length > 0 && "--check".equals(args[0])) {
            new HospitalManagementServiceImpl().initializeDatabase();
            System.out.println("Startup check passed.");
            return;
        }
        gui.HospitalManagementGui.main(args);
    }
}

