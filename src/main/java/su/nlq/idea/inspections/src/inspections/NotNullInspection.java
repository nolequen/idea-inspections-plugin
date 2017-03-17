package inspections;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import com.intellij.psi.util.TypeConversionUtil;
import fixes.InsertAnnotationFix;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiPredicate;

public final class NotNullInspection extends BaseJavaLocalInspectionTool {
  @NotNull
  private static final ProblemHighlightType HIGHLIGHT_TYPE = ProblemHighlightType.GENERIC_ERROR_OR_WARNING;

  @NotNull
  private static final LocalQuickFix[] fixes = {new InsertAnnotationFix(NotNull.class), new InsertAnnotationFix(Nullable.class)};

  public NotNullInspection() {
  }

  @Override
  public boolean isEnabledByDefault() {
    return true;
  }

  @Override
  @Nls
  @NotNull
  public String getGroupDisplayName() {
    return GroupNames.BUGS_GROUP_NAME;
  }

  @Override
  @Nls
  @NotNull
  public String getDisplayName() {
    return "@NotNull/@Nullable missing";
  }

  @Override
  @NonNls
  @NotNull
  public String getShortName() {
    return "NotNullNullableValidation";
  }

  @Nullable
  @Override
  public ProblemDescriptor[] checkField(@NotNull PsiField field, @NotNull InspectionManager manager, boolean isOnTheFly) {
    if (field instanceof PsiEnumConstant) {
      return null;
    }
    return Problem.checkFor(field, field.getType())
        .map(problem -> new ProblemDescriptors(manager).add(field, problem.message).toArray())
        .orElse(null);
  }

  @Nullable
  @Override
  public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
    final ProblemDescriptors descriptors = new ProblemDescriptors(manager);

    Problem.checkFor(method, Optional.ofNullable(method.getReturnType()).orElse(PsiType.VOID))
        .ifPresent(problem -> descriptors.add(method, problem.message));

    for (PsiParameter parameter : method.getParameterList().getParameters()) {
      Problem.checkFor(parameter, parameter.getType())
          .ifPresent(problem -> descriptors.add(parameter, problem.message));
    }

    return descriptors.toArray();
  }

  private enum Problem implements BiPredicate<PsiModifierListOwner, PsiType> {

    AnnotationIsMissing("@NotNull or @Nullable annotation is missing") {
      @Override
      public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiType type) {
        if (type instanceof PsiPrimitiveType) {
          return false;
        }
        final AnnotationsOwner owner = AnnotationsOwner.of(member);
        return !owner.has(NotNull.class) && !owner.has(Nullable.class);
      }
    },

    BothAnnotationsPresent("Both @NotNull and @Nullable annotations presents") {
      @Override
      public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiType type) {
        final AnnotationsOwner owner = AnnotationsOwner.of(member);
        return owner.has(NotNull.class) && owner.has(Nullable.class);
      }
    },

    AnnotatedPrimitive("Primitives cannot be annotated with @NotNull or @Nullable") {
      @Override
      public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiType type) {
        if (!(type instanceof PsiPrimitiveType)) {
          return false;
        }
        final AnnotationsOwner owner = AnnotationsOwner.of(member);
        return owner.has(NotNull.class) || owner.has(Nullable.class);
      }
    },

    TransientNotNull("Transient fields cannot be annotated with @NotNull") {
      @Override
      public boolean test(@NotNull PsiModifierListOwner member, @NotNull PsiType type) {
        return member.hasModifierProperty(PsiModifier.TRANSIENT) && AnnotationsOwner.of(member).has(NotNull.class);
      }
    };

    @NotNull
    private final String message;

    @NotNull
    public static Optional<Problem> checkFor(@NotNull PsiModifierListOwner member, @NotNull PsiType type) {
      return Arrays.stream(Problem.values()).filter(problem -> problem.test(member, type)).findFirst();
    }

    private Problem(@NotNull String message) {
      this.message = message;
    }
  }

  private static final class ProblemDescriptors {
    @NotNull
    private final InspectionManager manager;
    @NotNull
    private final Collection<ProblemDescriptor> descriptors = new ArrayList<>();

    public ProblemDescriptors(@NotNull InspectionManager manager) {
      this.manager = manager;
    }

    @NotNull
    public ProblemDescriptors add(@NotNull PsiElement element, @NotNull String message) {
      descriptors.add(manager.createProblemDescriptor(element, message, fixes, HIGHLIGHT_TYPE, false, false));
      return this;
    }

    @Nullable
    public ProblemDescriptor[] toArray() {
      return descriptors.isEmpty() ? null : descriptors.toArray(new ProblemDescriptor[descriptors.size()]);
    }
  }
}

