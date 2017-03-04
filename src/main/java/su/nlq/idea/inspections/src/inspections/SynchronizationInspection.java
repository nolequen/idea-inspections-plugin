package inspections;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public abstract class SynchronizationInspection extends BaseJavaLocalInspectionTool {

  @Override
  public final boolean isEnabledByDefault() {
    return true;
  }

  @Override
  @Nls
  @NotNull
  public final String getGroupDisplayName() {
    return GroupNames.THREADING_GROUP_NAME;
  }

  @NotNull
  @Override
  public final PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return buildVisitor(holder);
  }

  @NotNull
  protected abstract ElementVisitor buildVisitor(@NotNull ProblemsHolder holder);

  protected abstract static class ElementVisitor extends JavaElementVisitor {
    @NotNull
    private final ProblemsHolder holder;

    protected ElementVisitor(@NotNull ProblemsHolder holder) {
      this.holder = holder;
    }

    protected final void registerProblem(@NotNull PsiElement element, @NotNull String description) {
      holder.registerProblem(element, description, LocalQuickFix.EMPTY_ARRAY);
    }
  }
}