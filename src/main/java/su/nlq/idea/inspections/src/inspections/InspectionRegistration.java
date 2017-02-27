package inspections;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

public final class InspectionRegistration implements ApplicationComponent, InspectionToolProvider {

  public InspectionRegistration() {
  }

  @Override
  public void initComponent() {
    // do nothing
  }

  @Override
  public void disposeComponent() {
    // do nothing
  }

  @Override
  @NotNull
  public String getComponentName() {
    return "InspectionRegistration";
  }

  @Override
  @NotNull
  public Class<?>[] getInspectionClasses() {
    return new Class<?>[]{NotNullInspection.class};
  }
}
