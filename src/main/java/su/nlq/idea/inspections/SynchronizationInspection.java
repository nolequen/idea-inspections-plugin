package su.nlq.idea.inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public abstract class SynchronizationInspection extends AbstractBaseJavaLocalInspectionTool {
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