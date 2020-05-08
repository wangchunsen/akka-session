package session.akka.anno;

public interface LifeCycleAware {
  void onInitial();

  void onInitialed();

  void onStopping();

  void onStopped();
}
