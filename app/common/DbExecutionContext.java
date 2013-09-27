package common;

import play.libs.Akka;
import scala.concurrent.ExecutionContext;

public class DbExecutionContext {
    public final static ExecutionContext ctx = Akka.system().dispatchers().lookup("akka.db-dispatcher");
}
