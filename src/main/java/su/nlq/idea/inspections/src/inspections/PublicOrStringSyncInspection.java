package inspections;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class PublicOrStringSyncInspection extends SynchronizationInspection {
  @NotNull
  private static final String STRING_CLASS_NAME = "java.lang.String";

  @Override
  @NotNull
  public String getDisplayName() {
    return "Synchronization on public or " + STRING_CLASS_NAME + " field";
  }

  @Override
  @NotNull
  public String getShortName() {
    return "PublicOrStringSyncInspection";
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
      if (!(expression instanceof PsiReferenceExpression)) {
        return;
      }
      final PsiReference reference = expression.getReference();
      if (reference == null) {
        return;
      }
      final PsiElement element = reference.resolve();
      if (!(element instanceof PsiField)) {
        return;
      }
      final PsiField field = (PsiField) element;
      if (field.hasModifierProperty("public")) {
        registerProblem(expression, "Synchronization on public field");
      }
      final PsiClassType typeString = PsiType.getTypeByName(STRING_CLASS_NAME, statement.getProject(), statement.getResolveScope());
      if (Objects.equals(field.getType(), typeString)) {
        registerProblem(expression, "Synchronization on " + STRING_CLASS_NAME + " field");
      }
    }
  }
}