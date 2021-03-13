package su.nlq.idea.inspections;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiTypeParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nlq.idea.inspections.fixes.FinalOrAbstractClassFix;

public final class FinalOrAbstractClassInspection extends AbstractBaseJavaLocalInspectionTool {
  @NotNull
  private static final ProblemHighlightType HIGHLIGHT_TYPE = ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
  @NotNull
  private static final LocalQuickFix[] fixes = {new FinalOrAbstractClassFix()};

  @Nullable
  @Override
  public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
    if (aClass.isEnum() || aClass.isInterface() || aClass instanceof PsiTypeParameter || aClass instanceof PsiAnonymousClass) {
      return null;
    }
    if (aClass.hasModifierProperty(PsiModifier.ABSTRACT) || aClass.hasModifierProperty(PsiModifier.FINAL)) {
      return null;
    }
    return new ProblemDescriptor[]{manager.createProblemDescriptor(aClass, getDisplayName(), fixes, HIGHLIGHT_TYPE, false, false)};
  }
}
