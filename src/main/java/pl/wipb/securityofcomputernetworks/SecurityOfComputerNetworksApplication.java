package pl.wipb.securityofcomputernetworks;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.wipb.securityofcomputernetworks.gui.DesGui;

@SpringBootApplication
public class SecurityOfComputerNetworksApplication {

    public static void main(String[] args) {
        DesGui mainGui = new DesGui();
        mainGui.main();
    }

}
