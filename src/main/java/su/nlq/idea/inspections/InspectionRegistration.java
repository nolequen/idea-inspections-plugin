package su.nlq.idea.inspections;

import com.intellij.codeInspection.InspectionToolProvider;
import org.jetbrains.annotations.NotNull;

public final class InspectionRegistration implements InspectionToolProvider {

  @SuppressWarnings({"rawtypes", "unchecked"})
  @NotNull
  @Override
  public Class[] getInspectionClasses() {
    return new Class[]{
        NotNullInspection.class,
        FinalOrAbstractClassInspection.class,
        StaticSyncInspection.class,
        PublicOrStringSyncInspection.class
    };
  }
}
