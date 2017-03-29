package su.nlq.idea.inspections.src.inspections;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public final class StaticSyncInspection extends SynchronizationInspection {

  @Override
  @NotNull
  public String getDisplayName() {
    return "Synchronized static method or class object synchronization";
  }

  @Override
  @NotNull
  public String getShortName() {
    return "StaticSyncInspection";
  }

  @NotNull
  @Override
  protected ElementVisitor buildVisitor(@NotNull ProblemsHolder holder) {
    return new Visitor(holder);
  }

  private static final class Visitor extends ElementVisitor {

    public Visitor(@NotNull ProblemsHolder holder) {
      super(holder);
    }

    @Override
    public void visitSynchronizedStatement(@NotNull PsiSynchronizedStatement statement) {
      super.visitSynchronizedStatement(statement);

      final PsiExpression expression = statement.getLockExpression();
      if (expression instanceof PsiClassObjectAccessExpression) {
        registerProblem(expression, "Class object synchronization");
      }
    }

    @Override
    public void visitMethod(@NotNull PsiMethod method) {
      super.visitMethod(method);

      final PsiModifierList modifiers = method.getModifierList();
      if (modifiers.hasModifierProperty(PsiModifier.STATIC) && modifiers.hasModifierProperty(PsiModifier.SYNCHRONIZED)) {
        registerProblem(method, "Static field synchronization");
      }
    }
  }
}