package pl.wipb.securityofcomputernetworks;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.wipb.securityofcomputernetworks.gui.DesGui;

import java.io.IOException;

@SpringBootApplication
public class SecurityOfComputerNetworksApplication {

    public static void main(String[] args) throws IOException {
        DesGui mainGui = new DesGui();
        mainGui.main();
    }

}
