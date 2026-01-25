package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.ClickNotiBean;
import java.util.List;

public interface ClickNotificationClient {
    boolean enviarClicks(List<ClickNotiBean> clicks);
}