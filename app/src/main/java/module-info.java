module ia.samuelvanie.refutation.algo{
  requires java.desktop;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.base;
  requires javafx.graphics;

  opens ia.samuelvanie.refutation.algo to javafx.graphics, javafx.fxml;
  exports ia.samuelvanie.refutation.algo;
}
